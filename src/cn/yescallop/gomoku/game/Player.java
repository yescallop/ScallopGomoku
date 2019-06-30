package cn.yescallop.gomoku.game;

/**
 * An interface representing a player as a handler.
 *
 * @author Scallop Ye
 */
public interface Player {

    /**
     * Called when the game is started.
     * This method must not be blocking.
     *
     * @param game The game
     * @param side The initial side of this player
     */
    void gameStarted(Game game, Side side);

    /**
     * Called when a move is made by the opponent.
     * This method must not be blocking.
     *
     * @param move The move
     */
    void moveMade(Board.Grid move);

    /**
     * Called when a move is requested.
     * This method must be interruptible.
     *
     * @return The grid where the next move is made
     */
    Board.Grid requestMove() throws InterruptedException;

    /**
     * Called when a choice is requested.
     * This method must be interruptible.
     *
     * @param choices The choices to choose from
     * @return The index corresponding to the choice
     */
    int requestChoice(String[] choices) throws InterruptedException;

    /**
     * Called when the sides are swapped.
     * This method must not be blocking.
     */
    void sideSwapped();

    /**
     * Called when a result is received.
     * This method must not be blocking.
     *
     * @param result The result
     */
    void resultReceived(Result result);
}
