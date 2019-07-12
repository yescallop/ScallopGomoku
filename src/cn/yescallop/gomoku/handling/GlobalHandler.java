package cn.yescallop.gomoku.handling;

import cn.yescallop.gomoku.game.*;

/**
 * A global game handler.
 *
 * @author Scallop Ye
 */
public interface GlobalHandler {

    /**
     * Called when the game is started.
     * This method must not be blocking.
     *
     * @param game       the game.
     * @param controller the global controller, null if player handlers exist.
     */
    void gameStarted(Game game, Game.GlobalController controller);

    /**
     * Called when a move is requested.
     * This method must not be blocking.
     *
     * @param side the side requested to move.
     */
    void moveRequested(Side side);

    /**
     * Called when a move is made.
     * This method must not be blocking.
     *
     * @param move the move.
     */
    void moveMade(Board.Grid move);

    /**
     * Called when a choice is requested.
     * This method must not be blocking.
     *
     * @param choices the choices to choose from.
     * @param side    the side.
     */
    void choiceRequested(Choice[] choices, Side side);

    /**
     * Called when a choice is made.
     * This method must not be blocking.
     *
     * @param choice the move.
     * @param side   the side.
     */
    void choiceMade(Choice choice, Side side);

    /**
     * Called when the sides are swapped.
     * This method must not be blocking.
     */
    void sideSwapped();

    /**
     * Called when the game ends.
     * This method must not be blocking.
     *
     * @param result the result of the game.
     */
    void gameEnded(Result result);
}
