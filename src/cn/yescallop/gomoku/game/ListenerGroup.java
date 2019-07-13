package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.event.GameListener;
import cn.yescallop.gomoku.rule.Rule;

import java.util.LinkedHashSet;

/**
 * A ListenerGroup holds several listeners.
 *
 * @author Scallop Ye
 */
class ListenerGroup extends LinkedHashSet<GameListener> implements GameListener {

    @Override
    public void gameStarted(Board board, Rule.Type ruleType) {
        forEach(l -> l.gameStarted(board, ruleType));
    }

    @Override
    public void moveRequested(Side side) {
        forEach(l -> l.moveRequested(side));
    }

    @Override
    public void moveMade(Board.Grid move, Side side) {
        forEach(l -> l.moveMade(move, side));
    }

    @Override
    public void choiceRequested(Choice[] choices, Side side) {
        forEach(l -> l.choiceRequested(choices, side));
    }

    @Override
    public void choiceMade(Choice choice, Side side) {
        forEach(l -> l.choiceMade(choice, side));
    }

    @Override
    public void stoneSwapped() {
        forEach(GameListener::stoneSwapped);
    }

    @Override
    public void gameEnded(Result result, Side winningSide) {
        forEach(l -> l.gameEnded(result, winningSide));
    }
}
