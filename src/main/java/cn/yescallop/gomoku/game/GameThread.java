package cn.yescallop.gomoku.game;

import java.util.concurrent.*;

/**
 * The game thread.
 *
 * @author Scallop Ye
 */
class GameThread extends Thread {

    private final GameImpl game;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final long moveTimeout;
    private final long[] moveTimeRemaining;
    private final long[] gameTimeRemaining;

    GameThread(GameImpl game) {
        super("GameThread");
        this.game = game;
        moveTimeout = game.moveTimeout();
        moveTimeRemaining = moveTimeout == 0 ? null : new long[]{moveTimeout, moveTimeout};
        long gameTimeout = game.gameTimeout();
        gameTimeRemaining = gameTimeout == 0 ? null : new long[]{gameTimeout, gameTimeout};
    }

    @Override
    public void run() {
        while (!game.isEnded()) {
            Side side = game.currentSide();
            long startTime = System.currentTimeMillis();
            try {
                if (game.isAwaitingChoice()) {
                    requestChoice(game.choiceSet(), side);
                } else {
                    requestMove(side);
                }
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (gameTimeRemaining != null)
                    gameTimeRemaining[side.index()] -= elapsedTime;
                if (moveTimeRemaining != null)
                    moveTimeRemaining[side.index()] = moveTimeout;
            } catch (InterruptedException e) {
                // Thread interrupted
                break;
            } catch (ExecutionException | IllegalMoveException | IllegalChoiceException e) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                // Player exception
                game.listenerGroup.exceptionCaught(e, side);

                if (gameTimeRemaining != null)
                    gameTimeRemaining[side.index()] -= elapsedTime;
                if (moveTimeRemaining != null)
                    moveTimeRemaining[side.index()] -= elapsedTime;
            } catch (TimeoutException e) {
                // Timeout
                game.controller.end(Result.Type.TIMEOUT, side.opposite());
            } catch (Exception e) {
                // Unexpected exception
                game.listenerGroup.exceptionCaught(e, null);
                game.controller.end(Result.Type.EXCEPTION, null);
            }
        }
        executor.shutdown();
        if (!game.isEnded())
            game.controller.end(Result.Type.INTERRUPT, null);
    }

    private void requestMove(Side side)
            throws InterruptedException, ExecutionException, TimeoutException, IllegalMoveException {
        game.listenerGroup.moveRequested(side);

        Board.Point move = getMove(side);

        Board.Grid grid;
        try {
            grid = game.board().getGrid(move);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalMoveException("Out of board");
        }

        if (!grid.isEmpty())
            throw new IllegalMoveException("Moving into an occupied grid");

        game.judge.processMove(grid, side);
    }

    private void requestChoice(ChoiceSet choiceSet, Side side)
            throws InterruptedException, ExecutionException, TimeoutException, IllegalChoiceException {
        game.listenerGroup.choiceRequested(choiceSet, side);
        int choice = getChoice(choiceSet, side);

        if (!choiceSet.validate(choice))
            throw new IllegalChoiceException("Illegal choice");

        game.listenerGroup.choiceMade(choiceSet, choice, side);
        game.resetChoice();
        game.judge.processChoice(choice, side);
    }

    private long sideTimeout(Side side) {
        int i = side.index();
        if (gameTimeRemaining == null)
            return moveTimeRemaining == null ? 0 : moveTimeRemaining[i];
        return moveTimeRemaining == null ? gameTimeRemaining[i] :
                Math.min(gameTimeRemaining[i], moveTimeRemaining[i]);
    }

    private Board.Point getMove(Side side) throws InterruptedException, ExecutionException, TimeoutException {
        long timeout = sideTimeout(side);
        Future<Board.Point> moveFuture = executor.submit(() -> game.player(side).requestMove(timeout));
        Board.Point move;

        try {
            move = (timeout == 0) ? moveFuture.get() : moveFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            moveFuture.cancel(true);
            throw e;
        }
        return move;
    }

    private int getChoice(ChoiceSet choiceSet, Side side) throws InterruptedException, ExecutionException, TimeoutException {
        Future<Integer> choiceFuture = executor.submit(() -> game.player(side).requestChoice(choiceSet, moveTimeout));
        int choice;

        try {
            choice = (moveTimeout == 0) ? choiceFuture.get() : choiceFuture.get(moveTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            choiceFuture.cancel(true);
            throw e;
        }
        return choice;
    }
}
