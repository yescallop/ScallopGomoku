package cn.yescallop.gomoku.handling;

import cn.yescallop.gomoku.game.*;

/**
 * A global game handler.
 *
 * @author Scallop Ye
 */
public abstract class GlobalHandler {

    protected Game game;
    protected Game.GlobalController controller;

    /**
     * Called when the game is started.
     * This method must not be blocking.
     *
     * @param game       the game.
     * @param controller the player controller.
     */
    public void gameStarted(Game game, Game.GlobalController controller) {
        this.game = game;
        this.controller = controller;
    }

    /**
     * Called when a move is requested.
     * This method must not be blocking.
     *
     * @param side the side requested to move.
     */
    public abstract void moveRequested(Side side);

    /**
     * Called when a move is made.
     * This method must not be blocking.
     *
     * @param move the move.
     */
    public abstract void moveMade(Board.Grid move);

    /**
     * Called when a choice is requested.
     * This method must not be blocking.
     *
     * @param choices the choices to choose from.
     * @param side    the side.
     */
    public abstract void choiceRequested(Choice[] choices, Side side);

    /**
     * Called when a choice is made.
     * This method must not be blocking.
     *
     * @param choice the move.
     * @param side   the side.
     */
    public abstract void choiceMade(Choice choice, Side side);

    /**
     * Called when the sides are swapped.
     * This method must not be blocking.
     */
    public abstract void sideSwapped();

    /**
     * Called when the game ends.
     * This method must not be blocking.
     *
     * @param result the result of the game.
     */
    public abstract void gameEnded(Result result);
}
