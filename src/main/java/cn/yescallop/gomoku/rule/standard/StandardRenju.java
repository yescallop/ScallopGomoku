package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;
import cn.yescallop.gomoku.rule.AbstractJudge;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * @author Scallop Ye
 */
public class StandardRenju extends AbstractJudge {

    @Override
    public void processMove(Board.Grid grid, Side side) throws IllegalMoveException {
        controller.makeMove(grid);
        if (grid.stone() == StoneType.WHITE) {
            if (RuleHelper.longestChainSize(grid) >= 5) {
                controller.end(Result.Type.CHAIN_COMPLETED, side);
                return;
            }
        } else {
            // TODO: Black
        }
        controller.switchSide();
    }

    @Override
    public void processChoice(int choice, Side side) {

    }
}
