package cn.yescallop.gomoku.game;

import java.util.concurrent.*;

/**
 * @author Scallop Ye
 */
class GameThread extends Thread {

    private final Game game;

    private final long moveTimeout;
    private final long[] timeRemaining;

    GameThread(Game game) {
        this.game = game;
        moveTimeout = game.moveTimeout();
        long gameTimeout = game.gameTimeout();
        timeRemaining = gameTimeout == 0 ? null : new long[]{gameTimeout, gameTimeout};
    }

    @Override
    public void run() {
        while (!game.isEnded()) {
            Side side = game.currentSide();
            try {
                if (game.isAwaitingChoice()) {
                    requestChoice(game.choiceSet(), side);
                } else {
                    requestMove(side);
                }
            } catch (InterruptedException e) {
                // Thread interrupted
                break;
            } catch (ExecutionException e) {
                // Exception occurred while requesting move
                game.listenerGroup().exceptionCaught(e);
                game.controller().end(Result.Type.EXCEPTION, null);
            } catch (TimeoutException e) {
                // Timeout
                game.controller().end(Result.Type.TIMEOUT, side.opposite());
            } catch (IllegalMoveException | IllegalChoiceException e) {
                game.listenerGroup().exceptionCaught(e);
            }
        }
    }

    private void requestMove(Side side)
            throws InterruptedException, ExecutionException, TimeoutException, IllegalMoveException {
        game.listenerGroup().moveRequested(side);

        long startTime = System.currentTimeMillis();
        Board.Grid move = getMove(side);
        long elapsedTime = System.currentTimeMillis() - startTime;

        if (move.isOccupied())
            throw new IllegalMoveException("Moving into an occupied grid");

        game.rule().processMove(move, side);

        if (timeRemaining != null)
            timeRemaining[side.index()] -= elapsedTime;
    }

    private void requestChoice(ChoiceSet choiceSet, Side side)
            throws InterruptedException, ExecutionException, TimeoutException, IllegalChoiceException {
        game.listenerGroup().choiceRequested(choiceSet, side);
        int choice = getChoice(choiceSet, side);

        if (!choiceSet.validate(choice))
            throw new IllegalChoiceException("Illegal choice");

        game.listenerGroup().choiceMade(choice, side);
        game.resetChoice();
        game.rule().processChoice(choice, side);
    }

    private Long sideTimeout(Side side) {
        if (timeRemaining == null)
            return moveTimeout;
        long tr = timeRemaining[side.index()];
        return moveTimeout == 0 ? tr : Math.min(moveTimeout, tr);
    }

    private Board.Grid getMove(Side side) throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        long timeout = sideTimeout(side);
        Future<Board.Grid> moveFuture = executor.submit(() -> game.player(side).requestMove(timeout));
        Board.Grid move;

        try {
            move = (timeout == 0) ? moveFuture.get() : moveFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            moveFuture.cancel(true);
            throw e;
        }
        return move;
    }

    private int getChoice(ChoiceSet choiceSet, Side side) throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

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
