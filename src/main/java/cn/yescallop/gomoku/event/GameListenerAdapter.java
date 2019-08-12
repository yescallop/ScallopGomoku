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
    public void moveRequested(Side side) {

    }

    @Override
    public void multipleMovesRequested(int count, Side side) {

    }

    @Override
    public void moveMade(Board.Grid move, Side side) {

    }

    @Override
    public void moveOffered(Board.Grid move, Side side) {

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
