package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.Opening;

/**
 * A judge of the rule "Swap".
 *
 * @author Scallop Ye
 */
public class Swap implements Opening {

    @Override
    public void processMove(Game.Controller controller,
                            int index, Board.Grid grid, Side side) {
        controller.makeMove();
        if (index == 3) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.SECOND);
            controller.endOpening();
        } else controller.swap();
    }

    @Override
    public void processChoice(Game.Controller controller, int index, int choice, Side side) {
        if (choice == 0) controller.swap();
    }
}
