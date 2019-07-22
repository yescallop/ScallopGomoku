package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.*;
import cn.yescallop.gomoku.rule.AbstractJudge;
import cn.yescallop.gomoku.rule.RuleHelper;
import cn.yescallop.gomoku.rule.StoneShape;

import java.util.List;

/**
 * A judge of the rule "Standard Renju".
 *
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
        } else if (game.isStrict()) {
            List<StoneShape> shapes = RuleHelper.searchShapes(grid);
            if (shapes.contains(StoneShape.FIVE)) {
                controller.end(Result.Type.CHAIN_COMPLETED, side);
                return;
            }
            String description = RuleHelper.describeForbiddenMove(shapes);
            if (description != null) {
                controller.end(Result.Type.FORBIDDEN_MOVE_MADE, side.opposite(), description);
                return;
            }
        } else {
            if (RuleHelper.longestChainSize(grid) == 5) {
                controller.end(Result.Type.CHAIN_COMPLETED, side);
                return;
            }
        }
//        controller.switchSide();
    }

    @Override
    public void processChoice(int choice, Side side) {

    }
}
