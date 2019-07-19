package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.event.GameListener;
import cn.yescallop.gomoku.player.Player;
import cn.yescallop.gomoku.rule.Rule;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A Game is a game.
 *
 * @author Scallop Ye
 */
public interface Game {

    /**
     * Creates a new builder of Game.
     *
     * @return the builder.
     */
    static Game.Builder newBuilder() {
        return new GameBuilderImpl();
    }

    /**
     * Starts the game.
     *
     * @return the future of this Game.
     */
    Game.Future start();

    /**
     * Gets the current side of turn.
     *
     * @return the current side.
     */
    Side currentSide();

    /**
     * Gets the current stone type of turn.
     *
     * @return the current stone type.
     */
    StoneType currentStoneType();

    /**
     * Gets the stone type by the side.
     *
     * @param side the side.
     * @return the stone type.
     */
    StoneType stoneTypeBySide(Side side);

    /**
     * Gets the side by the stone type.
     *
     * @param stone the stone type.
     * @return the side.
     */
    Side sideByStoneType(StoneType stone);

    /**
     * Gets the result of the game.
     *
     * @return the result of this game, null if not ended.
     */
    Result result();

    /**
     * Gets the board.
     *
     * @return the board of this game.
     */
    Board board();

    /**
     * Gets the game timeout.
     *
     * @return the game timeout.
     */
    long gameTimeout();

    /**
     * Gets the move timeout.
     *
     * @return the move timeout.
     */
    long moveTimeout();

    /**
     * Gets the current move index.
     *
     * @return the move index.
     */
    int currentMoveIndex();

    /**
     * Gets the rule of the game.
     *
     * @return the rule.
     */
    Rule rule();

    /**
     * Gets the player name by the side.
     *
     * @param side the side.
     * @return the player name.
     */
    String playerNameBySide(Side side);

    /**
     * Tells whether this game is started.
     */
    boolean isStarted();

    /**
     * Tells whether this game is ended.
     */
    boolean isEnded();

    /**
     * Tells whether the stones are swapped.
     */
    boolean isSwapped();

    /**
     * Tells whether a choice is being awaited.
     */
    boolean isAwaitingChoice();

    /**
     * A builder of Game.
     */
    interface Builder {

        /**
         * Sets the rule of the game.
         *
         * @param rule a Rule instance constraining the game.
         * @return this builder.
         */
        Builder rule(Rule rule);

        /**
         * Adds a game listener to the listener group.
         *
         * @param listener the listener to be added.
         * @return this builder.
         */
        Builder addListener(GameListener listener);

        /**
         * Adds a player.
         *
         * @param side   the side.
         * @param player the player.
         * @return this builder.
         */
        Builder addPlayer(Side side, Player player);

        /**
         * Sets the game timeout.
         *
         * @param timeout the game timeout, 0 for no timeout.
         * @param unit    the time unit of the timeout argument.
         * @return this builder.
         */
        Builder gameTimeout(long timeout, TimeUnit unit);

        /**
         * Sets the move timeout.
         *
         * @param timeout the move timeout, 0 for no timeout.
         * @param unit    the time unit of the timeout argument.
         * @return this builder.
         */
        Builder moveTimeout(long timeout, TimeUnit unit);

        /**
         * Builds the game.
         *
         * @return a Game.
         */
        Game build();
    }

    /**
     * The future of the game.
     */
    interface Future {

        /**
         * Interrupts the game.
         */
        void interrupt();

        /**
         * Waits for the game to end and gets the game.
         *
         * @return the ended game.
         * @throws InterruptedException if the current thread was interrupted while waiting.
         */
        Game get() throws InterruptedException;

        /**
         * Waits for at most the given time for the game to end and gets the game.
         *
         * @param timeout the maximum time to wait.
         * @param unit    the time unit of the timeout argument.
         * @return the ended game.
         * @throws InterruptedException if the current thread was interrupted while waiting.
         * @throws TimeoutException     if the wait timed out.
         */
        Game get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;
    }

    /**
     * A public game controller class.
     */
    interface Controller {

        /**
         * Swaps control of the stones.
         *
         * @param side the side which made the swap.
         */
        void swap(Side side);

        /**
         * Switches the turn to move.
         */
        void switchSide();

        /**
         * Sets the current side.
         *
         * @param side the side.
         */
        void setSide(Side side);

        /**
         * Sets the current side by the stone type.
         *
         * @param stone the stone type.
         */
        void setSideByStoneType(StoneType stone);

        /**
         * Makes a move.
         *
         * @param grid the grid where the move is made.
         */
        void makeMove(Board.Grid grid);

        /**
         * Requests multiple moves.
         *
         * @param count the count of moves requested.
         */
        void requestMultipleMoves(int count);

        /**
         * Requests a choice.
         *
         * @param choiceSet the choice set.
         * @param side      the side.
         */
        void requestChoice(ChoiceSet choiceSet, Side side);

        /**
         * Ends the game.
         *
         * @param resultType  the result type of the game.
         * @param winningSide the winning side.
         */
        void end(Result.Type resultType, Side winningSide);
    }
}
