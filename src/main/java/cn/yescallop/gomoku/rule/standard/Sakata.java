package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;
import cn.yescallop.gomoku.rule.Opening;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Sataka".
 *
 * @author Scallop Ye
 */
public class Sakata implements Opening {

    @Override
    public void processMove(Game.Controller controller,
                            int index, Board.Grid grid, Side side) throws IllegalMoveException {
        RuleHelper.validateStandardOpening(grid, index);
        switch (index) {
            case 4:
                if (RuleHelper.chebyshevDistToCenter(grid) > 3)
                    throw new IllegalMoveException("The fourth move outside central 7x7 area");
                break;
            case 5:
                if (RuleHelper.chebyshevDistToCenter(grid) > 4)
                    throw new IllegalMoveException("The fifth move outside central 9x9 area");
                controller.makeMove();
                controller.requestChoice(
                        ChoiceSet.ofStrings("Choose Black", "Choose White"),
                        Side.FIRST);
                controller.endOpening();
                return;
        }
        controller.makeMove();
        if (index != 3) {
            controller.swap();
        }
    }

    @Override
    public void processChoice(Game.Controller controller, int index, int choice, Side side) {
        if (choice == 0) controller.swap();
    }
}
