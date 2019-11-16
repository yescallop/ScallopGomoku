package cn.yescallop.gomoku.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The game thread.
 *
 * @author Scallop Ye
 */
class GameTask implements Runnable {

    private final GameImpl game;
    final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "RequestThread"));

    private final long[] moveTimeRemaining;
    final long[] gameTimeRemaining;

    private int multipleMovesNeeded = 0;
    private List<Board.Grid> multipleMoves;

    private int choiceIndex = 0;

    GameTask(GameImpl game) {
        this.game = game;
        moveTimeRemaining = game.moveTimeout == 0 ?
                null : new long[]{game.moveTimeout, game.moveTimeout};
        gameTimeRemaining = game.gameTimeout == 0 ?
                null : new long[]{game.gameTimeout, game.gameTimeout};
    }

    @Override
    public void run() {
        while (!game.isEnded()) {
            Side side = null;
            long startTime = System.currentTimeMillis();
            try {
                if ((side = game.sideAwaitingChoice()) != null) {
                    requestChoice(game.choiceSet(), side);
                } else {
                    requestMove(side = game.currentSide());
                }
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (gameTimeRemaining != null)
                    gameTimeRemaining[side.ordinal()] -= elapsedTime;
                if (moveTimeRemaining != null)
                    moveTimeRemaining[side.ordinal()] = game.moveTimeout;
            } catch (InterruptedException e) {
                // Thread interrupted
                game.controller.end(Result.Type.INTERRUPT, null);
            } catch (ExecutionException | IllegalMoveException | IllegalChoiceException e) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                // Player exception
                game.listenerGroup.exceptionCaught(e, side);

                if (gameTimeRemaining != null)
                    gameTimeRemaining[side.ordinal()] -= elapsedTime;
                if (moveTimeRemaining != null)
                    moveTimeRemaining[side.ordinal()] -= elapsedTime;
            } catch (TimeoutException e) {
                // Timeout
                game.controller.end(Result.Type.TIMEOUT, side.opposite());
            } catch (RuntimeException e) {
                // Unexpected exception
                game.listenerGroup.exceptionCaught(e, null);
                game.controller.end(Result.Type.EXCEPTION, null);
                throw e;
            }
        }
    }

    void multipleMovesRequested(int count) {
        multipleMovesNeeded = count;
        multipleMoves = new ArrayList<>(count);
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

        if (multipleMovesNeeded > 0) {
            for (Board.Grid m : multipleMoves) {
                if (grid == m)
                    throw new IllegalMoveException("Duplicate multiple moves");
                if (grid.equalsSymmetrically(m))
                    throw new IllegalMoveException("Symmetrical multiple moves");
            }
            multipleMoves.add(grid);
            game.listenerGroup.moveOffered(grid, side);
            if (--multipleMovesNeeded == 0) {
                game.controller.requestChoice(
                        ChoiceSet.ofMoves(multipleMoves.toArray(Board.Grid[]::new)),
                        side.opposite()
                );
                multipleMoves = null;
            }
        } else game.judge.processMove(game.currentMoveIndex() + 1, grid, side);
    }

    private void requestChoice(ChoiceSet choiceSet, Side side)
            throws InterruptedException, ExecutionException, TimeoutException, IllegalChoiceException {
        game.listenerGroup.choiceRequested(choiceSet, side);
        int choice = getChoice(choiceSet, side);

        if (!choiceSet.validate(choice))
            throw new IllegalChoiceException();

        game.resetChoice();
        game.listenerGroup.choiceMade(choiceSet, choice, side);
        if (choiceSet.type() == ChoiceSet.Type.MOVES) {
            Board.Grid move = choiceSet.moves()[choice];
            game.controller.makeMove(move);
        } else game.judge.processChoice(++choiceIndex, choice, side);
    }

    private long sideTimeout(Side side) {
        int i = side.ordinal();
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
