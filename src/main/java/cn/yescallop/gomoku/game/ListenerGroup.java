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
    public void moveRequested(Move.Attribute move, Side side) {
        forEach(l -> l.moveRequested(move, side));
    }

    @Override
    public void moveMade(Move move, Side side) {
        forEach(l -> l.moveMade(move, side));
    }

    @Override
    public void playerPassed(Side side) {
        forEach(l -> l.playerPassed(side));
    }

    @Override
    public void moveOffered(Move move, Side side) {
        forEach(l -> l.moveOffered(move, side));
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
