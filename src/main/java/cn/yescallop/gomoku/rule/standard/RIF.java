package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "RIF".
 *
 * @author Scallop Ye
 */
public class RIF extends StandardRenju {

    @Override
    public void processMove(int index, Board.Grid grid, Side side) throws IllegalMoveException {
        if (index > 4) {
            super.processMove(index, grid, side);
            return;
        }
        RuleHelper.validateStandardOpening(grid, index);
        controller.makeMove(grid);

        if (index < 3) {
            controller.swap();
        } else if (index == 3) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.SECOND);
        } else {
            controller.requestMultipleMoves(2, game.sideByStoneType(StoneType.BLACK));
        }
    }

    @Override
    public void processChoice(int index, int choice, Side side) {
        if (choice == 0) controller.swap();
        controller.setSideByStoneType(StoneType.WHITE);
    }
}
