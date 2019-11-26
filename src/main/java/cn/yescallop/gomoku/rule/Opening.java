package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Side;

/**
 * An Opening handles moves in the opening of the game.
 *
 * @author Scallop Ye
 */
public interface Opening {

    /**
     * Processes a move made by the player.
     *
     * @param controller the controller of the game.
     * @param index the move index.
     * @param grid the grid where the move is made.
     * @param side the side of the move.
     * @throws IllegalMoveException if the move is illegal.
     */
    void processMove(Game.Controller controller,
                     int index, Board.Grid grid, Side side) throws IllegalMoveException;

    /**
     * Processes a choice made by the player.
     *
     * @param controller the controller of the game.
     * @param index the choice index.
     * @param choice the choice made by the player.
     * @param side the side of the choice.
     */
    void processChoice(Game.Controller controller, int index, int choice, Side side);

}
