package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Result;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.AbstractJudge;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Free-style Gomoku".
 * <p>
 * The rule requires a row of five or more stones for a win.
 *
 * @author Scallop Ye
 */
public class FreestyleGomoku extends AbstractJudge {

    @Override
    public void processMove(int index, Board.Grid grid, Side side) {
        controller.makeMove(grid);
        if (RuleHelper.longestRowLen(grid) >= 5) {
            controller.end(Result.Type.ROW_COMPLETED, side);
        }
    }

    @Override
    public void processChoice(int index, int choice, Side side) {

    }
}
