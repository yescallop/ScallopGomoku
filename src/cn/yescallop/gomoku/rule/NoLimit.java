package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Result;
import cn.yescallop.gomoku.game.Rule;
import cn.yescallop.gomoku.game.Side;

/**
 * @author Scallop Ye
 */
public class NoLimit extends Rule {

    @Override
    public Result process(Board.Grid grid, Side side) {
        if (!move(grid, side))
            return Result.INVALID_MOVE;

        switchTurn();
        return Result.SUCCESS;
    }

    @Override
    public String name() {
        return "No Limit";
    }
}
