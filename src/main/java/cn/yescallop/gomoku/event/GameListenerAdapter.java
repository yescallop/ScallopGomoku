package cn.yescallop.gomoku.event;

import cn.yescallop.gomoku.game.*;

/**
 * An adapter of GameListener.
 *
 * @author Scallop Ye
 */
public abstract class GameListenerAdapter implements GameListener {

    @Override
    public void gameStarted(Game game) {

    }

    @Override
    public void moveRequested(Move.Attribute attr, Side side) {

    }

    @Override
    public void moveMade(Move move, Side side) {

    }

    @Override
    public void playerPassed(Side side) {

    }

    @Override
    public void moveOffered(Move move, Side side) {

    }

    @Override
    public void choiceRequested(ChoiceSet choiceSet, Side side) {

    }

    @Override
    public void choiceMade(ChoiceSet choiceSet, int choice, Side side) {

    }

    @Override
    public void stoneSwapped() {

    }

    @Override
    public void gameEnded(Result result) {

    }

    @Override
    public void exceptionCaught(Throwable t, Side side) {

    }
}
