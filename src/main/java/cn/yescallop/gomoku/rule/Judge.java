package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Side;

/**
 * A Judge serves as a judge of the game.
 *
 * @author Scallop Ye
 */
public interface Judge {

    /**
     * Called when the game is started.
     *
     * @param game       the game.
     * @param controller the game controller.
     */
    void gameStarted(Game game, Game.Controller controller);

    /**
     * Processes a move made by the player.
     *
     * @param grid the grid where the move is made.
     * @param side the side of the move.
     * @throws IllegalMoveException if the move is illegal.
     */
    void processMove(Board.Grid grid, Side side) throws IllegalMoveException;

    /**
     * Processes a choice made by the player.
     *
     * @param choice the choice made by the player.
     * @param side   the side of the choice.
     */
    void processChoice(int choice, Side side);

}
