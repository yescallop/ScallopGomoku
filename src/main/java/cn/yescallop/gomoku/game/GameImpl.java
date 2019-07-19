package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.player.Player;
import cn.yescallop.gomoku.rule.Judge;
import cn.yescallop.gomoku.rule.Rule;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of Game.
 *
 * @author Scallop Ye
 */
class GameImpl implements Game {

    private final Board board;
    private final Rule rule;
    private final Player[] players;

    private final long gameTimeout;
    private final long moveTimeout;

    final Judge judge;
    final Controller controller = new ControllerImpl();
    final ListenerGroup listenerGroup;

    private GameThread gameThread;

    private boolean started;
    private boolean ended;
    private boolean swapped = false;

    private boolean awaitingChoice = false;
    private ChoiceSet choiceSet = null;

    private Side currentSide = Side.FIRST;

    private Result result = null;

    GameImpl(GameBuilderImpl builder) {
        this.rule = builder.rule;
        this.judge = rule.newJudge();

        this.players = builder.players;
        // Sets the sides of the players
        players[0].setSide(Side.FIRST);
        players[1].setSide(Side.SECOND);

        this.gameTimeout = builder.gameTimeout;
        this.moveTimeout = builder.moveTimeout;

        this.listenerGroup = builder.listenerGroup;
        // Adds players to the listener group
        listenerGroup.add(players[0]);
        listenerGroup.add(players[1]);

        board = new Board(15);
    }

    @Override
    public Game.Future start() {
        if (started || ended)
            throw new IllegalStateException("Illegal start");
        started = true;

        judge.gameStarted(this, controller);

        listenerGroup.gameStarted(this);

        gameThread = new GameThread(this);
        gameThread.start();

        return new FutureImpl();
    }

    @Override
    public Side currentSide() {
        return currentSide;
    }

    @Override
    public StoneType currentStoneType() {
        return stoneTypeBySide(currentSide);
    }

    @Override
    public StoneType stoneTypeBySide(Side side) {
        boolean first = side == Side.FIRST;
        return (first ^ swapped) ? StoneType.BLACK : StoneType.WHITE;
    }

    @Override
    public Side sideByStoneType(StoneType stone) {
        boolean black = stone == StoneType.BLACK;
        return (black ^ swapped) ? Side.FIRST : Side.SECOND;
    }

    @Override
    public Result result() {
        return result;
    }

    @Override
    public Board board() {
        return board;
    }

    @Override
    public long gameTimeout() {
        return gameTimeout;
    }

    @Override
    public long moveTimeout() {
        return moveTimeout;
    }

    @Override
    public int currentMoveIndex() {
        return board.currentMoveIndex();
    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public String playerNameBySide(Side side) {
        return player(side).name();
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isEnded() {
        return ended;
    }

    @Override
    public boolean isSwapped() {
        return swapped;
    }

    @Override
    public boolean isAwaitingChoice() {
        return awaitingChoice;
    }

    // Package-private methods

    Player player(Side side) {
        return players[side.index()];
    }

    ChoiceSet choiceSet() {
        return choiceSet;
    }

    void resetChoice() {
        awaitingChoice = false;
        choiceSet = null;
    }

    // Non-static inner class implementations

    /**
     * Implementation of Game.Future.
     */
    private class FutureImpl implements Game.Future {

        private FutureImpl() {
        }

        @Override
        public void interrupt() {
            gameThread.interrupt();
        }

        @Override
        public Game get() throws InterruptedException {
            gameThread.join();
            return GameImpl.this;
        }

        @Override
        public Game get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
            unit.timedJoin(gameThread, timeout);
            if (gameThread.isAlive()) throw new TimeoutException();
            return GameImpl.this;
        }
    }

    /**
     * Implementation of Game.Controller.
     */
    private class ControllerImpl implements Game.Controller {

        private ControllerImpl() {
            // private access
        }

        @Override
        public void swap(Side side) {
            swapped = !swapped;
            listenerGroup.stoneSwapped(side);
        }

        @Override
        public void switchSide() {
            currentSide = currentSide.opposite();
        }

        @Override
        public void setSide(Side side) {
            currentSide = side;
        }

        @Override
        public void setSideByStoneType(StoneType stone) {
            currentSide = sideByStoneType(stone);
        }

        @Override
        public void makeMove(Board.Grid grid) {
            board.move(grid, currentStoneType());
            listenerGroup.moveMade(grid, currentSide);
        }

        @Override
        public void requestMultipleMoves(int count) {
            // TODO: Implements multiple moves
        }

        @Override
        public void requestChoice(ChoiceSet choiceSet, Side side) {
            currentSide = side;
            awaitingChoice = true;
            GameImpl.this.choiceSet = choiceSet;
        }

        @Override
        public void end(Result.Type resultType, Side winningSide) {
            ended = true;
            started = false;
            awaitingChoice = false;
            choiceSet = null;
            currentSide = null;
            result = new Result(resultType, winningSide);
            listenerGroup.gameEnded(result);
        }
    }
}
