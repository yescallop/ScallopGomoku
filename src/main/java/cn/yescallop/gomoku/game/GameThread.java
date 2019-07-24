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

    private final long[] moveTimeRemaining;
    final long[] gameTimeRemaining;

    GameThread(GameImpl game) {
        super("GameThread");
        this.game = game;
        moveTimeRemaining = game.moveTimeout == 0 ?
                null : new long[]{game.moveTimeout, game.moveTimeout};
        gameTimeRemaining = game.gameTimeout == 0 ?
                null : new long[]{game.gameTimeout, game.gameTimeout};
    }

    @Override
    public void run() {
        Side side = null;
        long startTime = 0;
        try {
            while (!game.isEnded()) {
                side = game.currentSide();
                startTime = System.currentTimeMillis();
                if (game.isAwaitingChoice()) {
                    requestChoice(game.choiceSet(), side);
                } else {
                    requestMove(side);
                }
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (gameTimeRemaining != null)
                    gameTimeRemaining[side.index()] -= elapsedTime;
                if (moveTimeRemaining != null)
                    moveTimeRemaining[side.index()] = game.moveTimeout;
            }
        } catch (InterruptedException e) {
            // Thread interrupted
            game.controller.end(Result.Type.INTERRUPT, null);
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
        } finally {
            executor.shutdown();
        }
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
            throw new IllegalChoiceException();

        game.listenerGroup.choiceMade(choiceSet, choice, side);
        game.judge.processChoice(choice, side);
        game.resetChoice();
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
        Future<Integer> choiceFuture = executor.submit(() -> game.player(side).requestChoice(choiceSet, game.moveTimeout));
        int choice;

        try {
            choice = (game.moveTimeout == 0) ? choiceFuture.get() : choiceFuture.get(game.moveTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            choiceFuture.cancel(true);
            throw e;
        }
        return choice;
    }
}
