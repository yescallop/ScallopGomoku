package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;
import cn.yescallop.gomoku.rule.Opening;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Yamaguchi".
 *
 * @author Scallop Ye
 */
public class Yamaguchi implements Opening {

    private int moveCount;

    @Override
    public void processMove(Game.Controller controller,
                            int index, Board.Grid grid, Side side) throws IllegalMoveException {
        RuleHelper.validateStandardOpening(grid, index);
        controller.makeMove();

        if (index < 3) {
            controller.swap();
        } else if (index == 3) {
            controller.requestChoice(ChoiceSet.ofMaxMoveCount(controller.game().board()), side);
        } else {
            controller.requestMultipleMoves(moveCount);
            controller.endOpening();
        }
    }

    @Override
    public void processChoice(Game.Controller controller, int index, int choice, Side side) {
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
