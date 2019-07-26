package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Side;
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
    public void processMove(int index, Board.Grid grid, Side side) throws IllegalMoveException {
        if (index == 4) {
            if (RuleHelper.chebyshevDistToCenter(grid) > 3)
                throw new IllegalMoveException("The fourth move outside central 7x7 area");
            controller.makeMove(grid);
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White", "Offer Moves"),
                    side.opposite()
            );
        } else super.processMove(index, grid, side);
    }

    @Override
    public void processChoice(int index, int choice, Side side) {
        if (index == 4 && choice == 2) {
            controller.requestMultipleMoves(n, side);
        } else super.processChoice(index, choice, side);
    }
}
