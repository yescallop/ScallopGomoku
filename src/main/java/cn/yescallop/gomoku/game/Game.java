package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.event.GameListener;
import cn.yescallop.gomoku.player.Player;
import cn.yescallop.gomoku.rule.Rule;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Declares a game.
 *
 * @author Scallop Ye
 */
public class Game {

    private final Board board;
    private final Rule rule;
    private final long gameTimeout;
    private final long moveTimeout;

    private final Controller controller = new Controller();

    private final ListenerGroup listenerGroup;

    private final Player[] players;

    private GameThread gameThread;

    private boolean started;
    private boolean ended;
    private boolean swapped = false;

    private boolean awaitingChoice = false;
    private ChoiceSet choiceSet = null;

    private Side currentSide = Side.FIRST;

    private Result result = null;

    private Game(Builder builder) {
        this.rule = Objects.requireNonNull(builder.rule);

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

    // Public methods

    /**
     * Starts the game.
     *
     * @return the future of this Game when it's ended.
     */
    public GameFuture start() {
        if (started || ended)
            throw new IllegalStateException("Illegal start");
        started = true;

        rule.gameStarted(this, controller);

        listenerGroup.gameStarted(new Settings());

        gameThread = new GameThread(this);
        gameThread.start();

        return new GameFuture();
    }

    /**
     * Gets the current side of turn.
     *
     * @return the current side.
     */
    public Side currentSide() {
        return currentSide;
    }

    /**
     * Gets the current stone type of turn.
     *
     * @return the current stone type.
     */
    public StoneType currentStoneType() {
        return stoneTypeBySide(currentSide);
    }

    /**
     * Gets the stone type by the side.
     *
     * @param side the side.
     * @return the stone type.
     */
    public StoneType stoneTypeBySide(Side side) {
        boolean first = side == Side.FIRST;
        return (first ^ swapped) ? StoneType.BLACK : StoneType.WHITE;
    }

    /**
     * Gets the side by the stone type.
     *
     * @param stone the stone type.
     * @return the side.
     */
    public Side sideByStoneType(StoneType stone) {
        boolean black = stone == StoneType.BLACK;
        return (black ^ swapped) ? Side.FIRST : Side.SECOND;
    }

    /**
     * Interrupts the game.
     */
    public void interrupt() {
        gameThread.interrupt();
    }

    /**
     * Gets the result of the game.
     *
     * @return the result of the game.
     */
    public Result result() {
        return result;
    }

    /**
     * Gets the board.
     *
     * @return the board of this game.
     */
    public Board board() {
        return board;
    }

    /**
     * Gets the game timeout.
     *
     * @return the game timeout.
     */
    public long gameTimeout() {
        return gameTimeout;
    }

    /**
     * Gets the move timeout.
     *
     * @return the move timeout.
     */
    public long moveTimeout() {
        return moveTimeout;
    }

    /**
     * Gets the current move index.
     */
    public int currentMoveIndex() {
        return board.currentMoveIndex();
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isEnded() {
        return ended;
    }

    public boolean isSwapped() {
        return swapped;
    }

    public boolean isAwaitingChoice() {
        return awaitingChoice;
    }

    // Package-private methods

    Rule rule() {
        return rule;
    }

    Controller controller() {
        return controller;
    }

    ListenerGroup listenerGroup() {
        return listenerGroup;
    }

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

    /**
     * A builder class for Game
     */
    public static class Builder {

        private Rule rule;
        private ListenerGroup listenerGroup = new ListenerGroup();
        private Player[] players = new Player[2];
        private long gameTimeout = 0;
        private long moveTimeout = 0;

        /**
         * Sets the rule of the game.
         *
         * @param rule a Rule instance standardizing the game.
         * @return this builder.
         */
        public Builder rule(Rule rule) {
            this.rule = rule;
            return this;
        }

        /**
         * Adds a game listener to the listener group.
         *
         * @param listener the listener to be added.
         * @return this builder.
         */
        public Builder addListener(GameListener listener) {
            listenerGroup.add(listener);
            return this;
        }

        /**
         * Sets the player.
         *
         * @param side   the side.
         * @param player the player.
         * @return this builder.
         */
        public Builder player(Side side, Player player) {
            players[side.index()] = player;
            return this;
        }

        /**
         * Sets the game timeout.
         *
         * @param timeout the game timeout, 0 for no timeout.
         * @param unit    the time unit of the timeout argument.
         * @return this builder.
         */
        public Builder gameTimeout(long timeout, TimeUnit unit) {
            if (timeout < 0)
                throw new IllegalArgumentException("Negative timeout");
            this.gameTimeout = unit.toMillis(timeout);
            return this;
        }

        /**
         * Sets the move timeout.
         *
         * @param timeout the move timeout, 0 for no timeout.
         * @param unit    the time unit of the timeout argument.
         * @return this builder.
         */
        public Builder moveTimeout(long timeout, TimeUnit unit) {
            if (timeout < 0)
                throw new IllegalArgumentException("Negative timeout");
            this.moveTimeout = unit.toMillis(timeout);
            return this;
        }

        /**
         * Builds the game.
         *
         * @return a Game.
         */
        public Game build() {
            return new Game(this);
        }
    }

    // Non-static inner classes

    /**
     * The future of the game.
     */
    public class GameFuture {

        private GameFuture() {
        }

        /**
         * Waits for the game to end and gets the game.
         *
         * @return the ended game.
         * @throws InterruptedException if the current thread was interrupted while waiting.
         */
        public Game get() throws InterruptedException {
            gameThread.join();
            return Game.this;
        }

        /**
         * Waits for at most the given time for the game to end and gets the game.
         *
         * @param timeout the maximum time to wait.
         * @param unit    the time unit of the timeout argument.
         * @return the ended game.
         * @throws InterruptedException if the current thread was interrupted while waiting.
         * @throws TimeoutException     if the wait timed out.
         */
        public Game get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
            unit.timedJoin(gameThread, timeout);
            if (gameThread.isAlive()) throw new TimeoutException();
            return Game.this;
        }
    }

    public class Settings {
        private Settings() {
        }

        public Board board() {
            return board;
        }

        public Rule.Type ruleType() {
            return rule.type();
        }

        public long gameTimeout() {
            return gameTimeout;
        }

        public long moveTimeout() {
            return moveTimeout;
        }
    }

    /**
     * A public game controller class.
     */
    public class Controller {

        private Controller() {
            // private access
        }

        /**
         * Swaps control of the stones.
         */
        public void swap() {
            swapped = !swapped;
            listenerGroup.stoneSwapped();
        }

        /**
         * Switch the turn to move.
         */
        public void switchSide() {
            currentSide = currentSide.opposite();
        }

        /**
         * Sets the current side.
         *
         * @param side the side.
         */
        public void setSide(Side side) {
            currentSide = side;
        }

        /**
         * Sets the current side by the stone type.
         *
         * @param stone the stone type.
         */
        public void setSideByStoneType(StoneType stone) {
            currentSide = sideByStoneType(stone);
        }

        /**
         * Makes a move.
         *
         * @param grid the grid where the move is made.
         */
        public void makeMove(Board.Grid grid) {
            board.move(grid, currentStoneType());

            listenerGroup.moveMade(grid, currentSide);
        }

        /**
         * Requests multiple moves.
         *
         * @param count the count of moves requested.
         */
        public void requestMultipleMoves(int count) {
            // TODO: Implements multiple moves
        }

        /**
         * Requests a choice.
         *
         * @param choiceSet the choice set.
         * @param side      the side.
         */
        public void requestChoice(ChoiceSet choiceSet, Side side) {
            currentSide = side;
            awaitingChoice = true;
            Game.this.choiceSet = choiceSet;
        }

        /**
         * Ends the game.
         *
         * @param resultType  the result type of the game.
         * @param winningSide the winning side.
         */
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
