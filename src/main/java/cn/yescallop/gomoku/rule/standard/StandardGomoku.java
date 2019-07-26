package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Result;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.AbstractJudge;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Standard Gomoku".
 * <p>
 * The rule requires a row of exactly five stones for a win:
 * rows of six or more, called overlines, do not count.
 *
 * @author Scallop Ye
 */
public class StandardGomoku extends AbstractJudge {

    @Override
    public void processMove(int index, Board.Grid grid, Side side) throws IllegalMoveException {
        controller.makeMove(grid);
        if (RuleHelper.longestRowSize(grid) == 5) {
            controller.end(Result.Type.ROW_COMPLETED, side);
            return;
        }
        controller.switchSide();
    }

    @Override
    public void processChoice(int index, int choice, Side side) {

    }
}
