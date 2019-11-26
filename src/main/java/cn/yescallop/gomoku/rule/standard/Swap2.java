package cn.yescallop.gomoku.rule.standard;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.Opening;

/**
 * A judge of the rule "Swap2".
 *
 * @author Scallop Ye
 */
public class Swap2 implements Opening {

    @Override
    public void processMove(Game.Controller controller,
                            int index, Board.Grid grid, Side side) {
        controller.makeMove();
        if (index == 3) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White", "Make 2 Moves and Choose by the Opponent"),
                    Side.SECOND);
        } else if (index == 5) {
            controller.requestChoice(
                    ChoiceSet.ofStrings("Choose Black", "Choose White"),
                    Side.FIRST);
        } else controller.swap();
    }

    @Override
    public void processChoice(Game.Controller controller, int index, int choice, Side side) {
        if (choice == 0) controller.swap();
        if (choice != 2) {
            controller.endOpening();
        }
    }
}
