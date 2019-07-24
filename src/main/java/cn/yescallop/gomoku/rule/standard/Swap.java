package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;

/**
 * A judge of the rule "Swap".
 *
 * @author Scallop Ye
 */
public class Swap extends StandardGomoku {

    @Override
    public void processMove(Board.Grid grid, Side side) throws IllegalMoveException {
        int index = game.currentMoveIndex();
        if (index > 2) {
            super.processMove(grid, side);
            return;
        }
        controller.makeMove(grid);
        if (index == 2) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.SECOND);
        } else controller.swap();
    }

    @Override
    public void processChoice(int choice, Side side) {
        if (choice == 0) controller.swap();
        controller.setSideByStoneType(StoneType.WHITE);
    }
}
