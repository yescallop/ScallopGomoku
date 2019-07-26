package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Gomoku-Pro".
 *
 * @author Scallop Ye
 */
public class GomokuPro extends StandardGomoku {

    @Override
    public void processMove(int index, Board.Grid grid, Side side) throws IllegalMoveException {
        if (index > 3) {
            super.processMove(index, grid, side);
            return;
        }
        if (index == 1 &&
                RuleHelper.chebyshevDistToCenter(grid) != 0) {
            throw new IllegalMoveException("The first move not in the center");
        } else if (index == 3 &&
                RuleHelper.chebyshevDistToCenter(grid) < 3) {
            throw new IllegalMoveException("The third move inside central 5x5 area");
        }
        controller.makeMove(grid);
        controller.switchSide();
    }
}
