package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Choice;
import cn.yescallop.gomoku.game.Side;

/**
 * @author Scallop Ye
 */
public class NoLimit extends Rule {

    @Override
    public void processMove(Board.Grid grid, Side side) {
        controller.makeMove(grid);
        controller.switchSide();
        controller.requestNextMove();
    }

    @Override
    public void processChoice(Choice choice, Side side) {

    }

    @Override
    public Type type() {
        return Type.NO_LIMIT;
    }
}
