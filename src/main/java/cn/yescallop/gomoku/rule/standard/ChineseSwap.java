package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;

/**
 * A judge of the rule "Chinese Swap", a.k.a. "一手交换".
 *
 * @author Scallop Ye
 */
public class ChineseSwap extends StandardGomoku {
    @Override
    public void processMove(Board.Grid grid, Side side) throws IllegalMoveException {
        int index = game.currentMoveIndex();
        if (index != 0) {
            super.processMove(grid, side);
            return;
        }
        controller.makeMove(grid);
        controller.requestChoice(
                ChoiceSet.ofStrings("Choose Black", "Choose White"),
                Side.SECOND);
    }

    @Override
    public void processChoice(int choice, Side side) {
        if (choice == 0) controller.swap();
        controller.setSideByStoneType(StoneType.WHITE);
    }
}
