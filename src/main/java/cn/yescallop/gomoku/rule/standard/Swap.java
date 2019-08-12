package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Side;

/**
 * A judge of the rule "Swap".
 *
 * @author Scallop Ye
 */
public class Swap extends StandardGomoku {

    @Override
    public void processMove(int index, Board.Grid grid, Side side) throws IllegalMoveException {
        if (index > 3) {
            super.processMove(index, grid, side);
            return;
        }
        controller.makeMove(grid);
        if (index == 3) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.SECOND);
        } else controller.swap();
    }

    @Override
    public void processChoice(int index, int choice, Side side) {
        if (choice == 0) controller.swap();
    }
}
