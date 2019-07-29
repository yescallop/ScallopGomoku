package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Yamaguchi".
 *
 * @author Scallop Ye
 */
public class Yamaguchi extends StandardRenju {

    private int moveCount;

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
            controller.requestChoice(ChoiceSet.ofMaxMoveCount(game.board()), side);
        } else {
            controller.requestMultipleMoves(moveCount);
        }
    }

    @Override
    public void processChoice(int index, int choice, Side side) {
        if (index == 1) {
            moveCount = choice;
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.SECOND);
        } else if (choice == 0) {
            controller.swap();
        }
    }
}
