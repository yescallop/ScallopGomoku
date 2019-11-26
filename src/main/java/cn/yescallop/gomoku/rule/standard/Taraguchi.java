package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Taraguchi-N".
 *
 * @author Scallop Ye
 */
public class Taraguchi extends Tarannikov {

    private final int n;

    public Taraguchi(int n) {
        if (n < 2)
            throw new IllegalArgumentException("n < 2");
        this.n = n;
    }

    @Override
    public void processMove(Game.Controller controller,
                            int index, Board.Grid grid, Side side) throws IllegalMoveException {
        if (index == 4) {
            if (RuleHelper.chebyshevDistToCenter(grid) > 3)
                throw new IllegalMoveException("The fourth move outside central 7x7 area");
            controller.makeMove();
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White", "Offer " + n + " Moves"),
                    side.opposite()
            );
        } else super.processMove(controller, index, grid, side);
    }

    @Override
    public void processChoice(Game.Controller controller, int index, int choice, Side side) {
        if (index == 4 && choice == 2) {
            controller.requestMultipleMoves(n);
        } else super.processChoice(controller, index, choice, side);
    }
}
