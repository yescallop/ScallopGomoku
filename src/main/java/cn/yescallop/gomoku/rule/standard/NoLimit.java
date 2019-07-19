package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.AbstractJudge;

/**
 * @author Scallop Ye
 */
public class NoLimit extends AbstractJudge {

    @Override
    public void processMove(Board.Grid grid, Side side) {
        controller.makeMove(grid);
        controller.switchSide();
    }

    @Override
    public void processChoice(int choice, Side side) {
    }
}
