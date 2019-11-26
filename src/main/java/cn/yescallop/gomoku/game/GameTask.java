package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.rule.RuleHelper;

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
    private final GameImpl.ControllerImpl controller;
    private final ListenerGroup listenerGroup;
    final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "RequestThread"));

    private final long[] moveTimeRemaining;
    final long[] gameTimeRemaining;

    private int multipleMovesTotal = 0;
    private int multipleMovesLeft = 0;
    private List<Board.Grid> multipleMoves;

    private int choiceIndex = 0;
    private int maxMoveIndex;

    private boolean lastPass = false;
    private boolean lastDraw = false;

    GameTask(GameImpl game) {
        this.game = game;
        controller = (GameImpl.ControllerImpl) game.controller;
        listenerGroup = game.listenerGroup;
        int size = game.board().size();
        maxMoveIndex = size * size;
        moveTimeRemaining = game.moveTimeout == 0 ?
                null : new long[]{game.moveTimeout, game.moveTimeout};
        gameTimeRemaining = game.gameTimeout == 0 ?
                null : new long[]{game.gameTimeout, game.gameTimeout};
    }

    @Override
    public void run() {
        while (!game.isEnded()) {
            if (game.currentMoveIndex() == maxMoveIndex) {
                controller.end(Result.Type.BOARD_FULL, null);
                break;
            }
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
                controller.end(Result.Type.INTERRUPT, null);
            } catch (ExecutionException | IllegalMoveException | IllegalChoiceException e) {
                if (e instanceof IllegalMoveException)
                    lastDraw = false;
                long elapsedTime = System.currentTimeMillis() - startTime;
                // Player exception
                listenerGroup.exceptionCaught(e, side);

                if (gameTimeRemaining != null)
                    gameTimeRemaining[side.ordinal()] -= elapsedTime;
                if (moveTimeRemaining != null)
                    moveTimeRemaining[side.ordinal()] -= elapsedTime;
            } catch (TimeoutException e) {
                // Timeout
                controller.end(Result.Type.TIMEOUT, side.opposite());
            } catch (RuntimeException e) {
                // Unexpected exception
                listenerGroup.exceptionCaught(e, null);
                controller.end(Result.Type.EXCEPTION, null);
                throw e;
            }
        }
    }

    void multipleMovesRequested(int count) {
        multipleMovesTotal = count;
        multipleMovesLeft = count;
        multipleMoves = new ArrayList<>(count);
    }

    private void requestMove(Side side)
            throws InterruptedException, ExecutionException, TimeoutException, IllegalMoveException {
        Move.Attribute attr;
        if (lastDraw) {
            attr = Move.Attribute.DRAW;
        } else if (multipleMovesTotal != 0) {
            attr = Move.Attribute.ofMultiple(multipleMovesTotal - multipleMovesLeft + 1, multipleMovesTotal);
        } else attr = Move.Attribute.NONE;
        listenerGroup.moveRequested(attr, side);

        Move move = getMove(attr, side);

        Board.Grid grid = null;
        if (move != null) {
            try {
                grid = game.board().getGrid(move.point());
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalMoveException("Out of board");
            }
        }

        if (multipleMovesTotal != 0) {
            if (move == null)
                throw new IllegalMoveException("Pass when offering multiple moves");
            for (Board.Grid m : multipleMoves) {
                if (grid == m)
                    throw new IllegalMoveException("Duplicate multiple moves");
                if (grid.equalsSymmetrically(m))
                    throw new IllegalMoveException("Symmetrical multiple moves");
            }
            multipleMovesLeft--;
            multipleMoves.add(grid);
            grid.offered = true;
            listenerGroup.moveOffered(Move.of(grid, attr), side);
            if (multipleMovesLeft == 0) {
                controller.requestChoice(
                        ChoiceSet.ofMoves(multipleMoves.toArray(Board.Grid[]::new)),
                        side.opposite()
                );
            }
            return;
        }

        if (move == null) {
            if (lastDraw) {
                controller.end(Result.Type.DRAW_OFFER_ACCEPTED, null);
                return;
            }
        } else lastDraw = move.attr().isOfDraw();

        controller.cacheMove(move, grid);
        if (game.isInOpening()) {
            if (move == null)
                throw new IllegalMoveException("Pass in the opening");
            game.opening.processMove(controller, game.currentMoveIndex() + 1, grid, side);
        } else {
            if (move == null) {
                listenerGroup.playerPassed(side);
                game.switchStoneType();
                if (lastPass) {
                    controller.end(Result.Type.BOTH_PASS, null);
                }
                lastPass = true;
                return;
            }
            lastPass = false;
            RuleHelper.processMove(controller, grid, side);
        }
    }

    private void requestChoice(ChoiceSet choiceSet, Side side)
            throws InterruptedException, ExecutionException, TimeoutException, IllegalChoiceException {
        listenerGroup.choiceRequested(choiceSet, side);
        int choice = getChoice(choiceSet, side);

        if (!choiceSet.validate(choice))
            throw new IllegalChoiceException();

        game.resetChoice();
        listenerGroup.choiceMade(choiceSet, choice, side);
        if (choiceSet.type() == ChoiceSet.Type.MOVES) {
            Board.Grid move = choiceSet.moves()[choice];
            controller.makeMove(move);
            for (Board.Grid grid : multipleMoves) {
                grid.offered = false;
            }
            multipleMovesTotal = 0;
            multipleMoves = null;
        } else game.opening.processChoice(controller, ++choiceIndex, choice, side);
    }

    private long sideTimeout(Side side) {
        int i = side.ordinal();
        if (gameTimeRemaining == null)
            return moveTimeRemaining == null ? 0 : moveTimeRemaining[i];
        return moveTimeRemaining == null ? gameTimeRemaining[i] :
                Math.min(gameTimeRemaining[i], moveTimeRemaining[i]);
    }

    private Move getMove(Move.Attribute attr, Side side) throws InterruptedException, ExecutionException, TimeoutException {
        long timeout = sideTimeout(side);
        Future<Move> moveFuture = executor.submit(() ->
                game.player(side).requestMove(attr, timeout));
        Move move;

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
