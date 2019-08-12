package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Sataka".
 *
 * @author Scallop Ye
 */
public class Sakata extends StandardRenju {

    @Override
    public void processMove(int index, Board.Grid grid, Side side) throws IllegalMoveException {
        if (index > 5) {
            super.processMove(index, grid, side);
            return;
        }
        RuleHelper.validateStandardOpening(grid, index);
        switch (index) {
            case 4:
                if (RuleHelper.chebyshevDistToCenter(grid) > 3)
                    throw new IllegalMoveException("The fourth move outside central 7x7 area");
                break;
            case 5:
                if (RuleHelper.chebyshevDistToCenter(grid) > 4)
                    throw new IllegalMoveException("The fifth move outside central 9x9 area");
                controller.makeMove(grid);
                controller.requestChoice(
                        ChoiceSet.ofStrings("Choose Black", "Choose White"),
                        Side.FIRST);
                return;
        }
        controller.makeMove(grid);
        if (index != 3) {
            controller.swap();
        }
    }

    @Override
    public void processChoice(int index, int choice, Side side) {
        if (choice == 0) controller.swap();
    }
}
