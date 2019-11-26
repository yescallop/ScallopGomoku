package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.Opening;

/**
 * A judge of the rule "Chinese Swap", a.k.a. "一手交换".
 *
 * @author Scallop Ye
 */
public class ChineseSwap implements Opening {

    @Override
    public void processMove(Game.Controller controller,
                            int index, Board.Grid grid, Side side) {
        controller.makeMove();
        controller.requestChoice(
                ChoiceSet.ofStrings("Choose Black", "Choose White"),
                Side.SECOND);
        controller.endOpening();
    }

    @Override
    public void processChoice(Game.Controller controller,
                              int index, int choice, Side side) {
        if (choice == 0) controller.swap();
    }
}
