package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.game.StoneType;

/**
 * A judge of the rule "Swap2".
 *
 * @author Scallop Ye
 */
public class Swap2 extends StandardGomoku {

    private boolean choiceMade = false;

    @Override
    public void processMove(Board.Grid grid, Side side) {
        if (choiceMade) {
            super.processMove(grid, side);
            return;
        }
        controller.makeMove(grid);
        int index = game.currentMoveIndex();
        if (index == 3) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White", "Make 2 Moves and Choose by the Opponent"),
                    Side.SECOND);
        } else if (index == 5) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.FIRST);
        } else controller.swap(null);
    }

    @Override
    public void processChoice(int choice, Side side) {
        int index = game.currentMoveIndex();
        if (index == 3) {
            if (choice == 0) controller.swap(side);
            if (choice != 2) {
                controller.setSideByStoneType(StoneType.WHITE);
                choiceMade = true;
            }
        } else if (index == 5) {
            if (choice == 0) controller.swap(side);
            controller.setSideByStoneType(StoneType.WHITE);
            choiceMade = true;
        }
    }
}
