package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;
import cn.yescallop.gomoku.rule.Opening;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Soosyrv-N".
 *
 * @author Scallop Ye
 */
public class Soosyrv implements Opening {

    private final int n;
    private int moveCount;

    public Soosyrv(int n) {
        if (n < 2)
            throw new IllegalArgumentException("n < 2");
        this.n = n;
    }

    @Override
    public void processMove(Game.Controller controller,
                            int index, Board.Grid grid, Side side) throws IllegalMoveException {
        RuleHelper.validateStandardOpening(grid, index);
        controller.makeMove();

        if (index < 3) {
            controller.swap();
        } else if (index == 3) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.SECOND);
        } else {
            controller.requestChoice(ChoiceSet.ofMoveCount(n), side);
            controller.endOpening();
        }
    }

    @Override
    public void processChoice(Game.Controller controller, int index, int choice, Side side) {
        if (index == 2) {
            moveCount = choice;
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.SECOND);
            return;
        }
        if ((choice == 0) ^ (controller.game().stoneTypeBySide(side) == StoneType.BLACK))
            controller.swap();
        if (index == 3 && moveCount != 1) {
            controller.requestMultipleMoves(moveCount);
        }
    }
}
