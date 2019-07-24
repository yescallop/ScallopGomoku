package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.event.GameListener;

import java.util.HashSet;

/**
 * A ListenerGroup holds several listeners.
 *
 * @author Scallop Ye
 */
class ListenerGroup extends HashSet<GameListener> implements GameListener {

    @Override
    public void gameStarted(Game game) {
        forEach(l -> l.gameStarted(game));
    }

    @Override
    public void moveRequested(Side side) {
        forEach(l -> l.moveRequested(side));
    }

    @Override
    public void multipleMovesRequested(int count, Side side) {
        forEach(l -> l.multipleMovesRequested(count, side));
    }

    @Override
    public void moveMade(Board.Grid move, Side side) {
        forEach(l -> l.moveMade(move, side));
    }

    @Override
    public void choiceRequested(ChoiceSet choiceSet, Side side) {
        forEach(l -> l.choiceRequested(choiceSet, side));
    }

    @Override
    public void choiceMade(ChoiceSet choiceSet, int choice, Side side) {
        forEach(l -> l.choiceMade(choiceSet, choice, side));
    }

    @Override
    public void stoneSwapped() {
        forEach(GameListener::stoneSwapped);
    }

    @Override
    public void gameEnded(Result result) {
        forEach(l -> l.gameEnded(result));
    }

    @Override
    public void exceptionCaught(Throwable t, Side side) {
        forEach(l -> l.exceptionCaught(t, side));
    }
}
