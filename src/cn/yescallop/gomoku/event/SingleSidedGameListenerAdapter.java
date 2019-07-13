package cn.yescallop.gomoku.event;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Choice;
import cn.yescallop.gomoku.game.Result;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.Rule;

/**
 * An adapter of SingleSidedGameListener
 *
 * @author Scallop Ye
 */
public class SingleSidedGameListenerAdapter extends SingleSidedGameListener {

    public SingleSidedGameListenerAdapter(Side side) {
        super(side);
    }

    @Override
    protected void moveRequested() {

    }

    @Override
    protected void opponentMoveMade(Board.Grid move) {

    }

    @Override
    protected void choiceRequested(Choice[] choices) {

    }

    @Override
    protected void opponentChoiceMade(Choice choice) {

    }

    @Override
    protected void gameEnded(Result result, boolean winning) {

    }

    @Override
    public void gameStarted(Board board, Rule.Type ruleType) {

    }

    @Override
    public void stoneSwapped() {

    }
}
