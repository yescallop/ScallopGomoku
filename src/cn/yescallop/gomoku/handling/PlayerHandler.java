package cn.yescallop.gomoku.handling;

import cn.yescallop.gomoku.game.*;

/**
 * A game handler for one player.
 *
 * @author Scallop Ye
 */
public abstract class PlayerHandler {

    protected Game game;
    protected Game.PlayerController controller;
    protected Side side;

    /**
     * Called when the game is started.
     * This method must not be blocking.
     *
     * @param game       the game.
     * @param controller the player controller.
     * @param side       the initial side of the player.
     */
    public void gameStarted(Game game, Game.PlayerController controller, Side side) {
        this.game = game;
        this.controller = controller;
        this.side = side;
    }

    /**
     * Called when a move is made by the opponent.
     * This method must not be blocking.
     *
     * @param move the move made by the opponent.
     */
    public abstract void opponentMoveMade(Board.Grid move);

    /**
     * Called when a move is requested.
     * This method must not be blocking.
     *
     * @param moveIndex the index of last move.
     */
    public abstract void moveRequested(int moveIndex);

    /**
     * Called when a choice is made by the opponent.
     * This method must not be blocking.
     *
     * @param choice the choice made by the opponent.
     */
    public abstract void opponentChoiceMade(Choice choice);

    /**
     * Called when a choice is requested.
     * This method must not be blocking.
     *
     * @param moveIndex the index of last move.
     * @param choices   the choices to choose from.
     */
    public abstract void choiceRequested(int moveIndex, Choice[] choices);

    /**
     * Called when the sides are swapped.
     * This method must not be blocking.
     */
    public void sideSwapped() {
        side = side.opposite();
    }

    /**
     * Called when the game ends.
     * This method must not be blocking.
     *
     * @param result the result of the game.
     */
    public abstract void gameEnded(Result result);
}
