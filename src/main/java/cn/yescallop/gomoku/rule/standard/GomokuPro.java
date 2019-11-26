package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.Opening;
import cn.yescallop.gomoku.rule.RuleHelper;

/**
 * A judge of the rule "Gomoku-Pro".
 *
 * @author Scallop Ye
 */
public class GomokuPro implements Opening {

    @Override
    public void processMove(Game.Controller controller, int index, Board.Grid grid, Side side) throws IllegalMoveException {
        switch (index) {
            case 1:
                if (RuleHelper.chebyshevDistToCenter(grid) != 0)
                    throw new IllegalMoveException("The first move not in the center");
                break;
            case 3:
                if (RuleHelper.chebyshevDistToCenter(grid) < 3)
                    throw new IllegalMoveException("The third move inside central 5x5 area");
                controller.endOpening();
                break;
        }
        controller.makeMove();
    }

    @Override
    public void processChoice(Game.Controller controller, int index, int choice, Side side) {

    }
}
