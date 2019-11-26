package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.event.GameListener;
import cn.yescallop.gomoku.player.Player;
import cn.yescallop.gomoku.rule.Rule;

import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
    static Builder newBuilder() {
        return new GameBuilderImpl();
    }

    /**
     * Starts the game.
     *
     * @return a CompletableFuture of this game.
     */
    CompletableFuture<Game> start();

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
     * @return the result of this game,
     * or null if the game is not ended.
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
     * @return the game timeout in milliseconds.
     */
    OptionalLong gameTimeoutMillis();

    /**
     * Gets the move timeout.
     *
     * @return the move timeout in milliseconds.
     */
    OptionalLong moveTimeoutMillis();

    /**
     * Gets the remaining time of a side.
     *
     * @param side the side.
     * @return the remaining time in milliseconds.
     */
    OptionalLong gameTimeRemainingMillis(Side side);

    /**
     * Gets whether this game is strict.
     *
     * @return whether this game is strict.
     */
    boolean isStrict();

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
     * Tells whether the game is in the opening process.
     */
    boolean isInOpening();

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
        Builder listener(GameListener listener);

        /**
         * Sets the player by the side.
         *
         * @param side the side.
         * @param player the player.
         * @return this builder.
         */
        Builder player(Side side, Player player);

        /**
         * Sets the game timeout.
         *
         * @param timeout the game timeout, 0 for no timeout.
         * @param unit the time unit of the timeout argument.
         * @return this builder.
         */
        Builder gameTimeout(long timeout, TimeUnit unit);

        /**
         * Sets the move timeout.
         *
         * @param timeout the move timeout, 0 for no timeout.
         * @param unit the time unit of the timeout argument.
         * @return this builder.
         */
        Builder moveTimeout(long timeout, TimeUnit unit);

        /**
         * Sets whether the game is strict.
         * "Strict" means that in Renju rules,
         * if Black makes a forbidden move,
         * the game will end immediately with
         * White as the winner.
         *
         * @param strict whether the game is strict.
         * @return this builder.
         */
        Builder strict(boolean strict);

        /**
         * Builds the game.
         *
         * @return a Game.
         */
        Game build();
    }

    /**
     * A public game controller class.
     */
    interface Controller {

        /**
         * Return the game of this controller.
         *
         * @return the game.
         */
        Game game();

        /**
         * Ends the opening process.
         */
        void endOpening();

        /**
         * Swaps control of the stones.
         */
        void swap();

        /**
         * Makes the cached move.
         */
        void makeMove();

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
         * @param side the side.
         */
        void requestChoice(ChoiceSet choiceSet, Side side);

        /**
         * Ends the game.
         *
         * @param resultType the result type of the game.
         * @param winningSide the winning side.
         */
        void end(Result.Type resultType, Side winningSide);

        /**
         * Ends the game.
         *
         * @param resultType the result type of the game.
         * @param winningSide the winning side.
         * @param description the description.
         */
        void end(Result.Type resultType, Side winningSide, String description);
    }
}
