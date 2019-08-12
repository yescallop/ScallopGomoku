package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Side;

/**
 * A judge of the rule "Swap2".
 *
 * @author Scallop Ye
 */
public class Swap2 extends StandardGomoku {

    private boolean choiceMade = false;

    @Override
    public void processMove(int index, Board.Grid grid, Side side) throws IllegalMoveException {
        if (choiceMade) {
            super.processMove(index, grid, side);
            return;
        }
        controller.makeMove(grid);
        if (index == 3) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White", "Make 2 Moves and Choose by the Opponent"),
                    Side.SECOND);
        } else if (index == 5) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.FIRST);
        } else controller.swap();
    }

    @Override
    public void processChoice(int index, int choice, Side side) {
        if (choice == 0) controller.swap();
        if (choice != 2) {
            choiceMade = true;
        }
    }
}
