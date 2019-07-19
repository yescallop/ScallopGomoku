package cn.yescallop.gomoku.player;

import cn.yescallop.gomoku.game.*;

/**
 * An adapter of Player.
 *
 * @author Scallop Ye
 */
public abstract class PlayerAdapter extends AbstractPlayer {

    protected PlayerAdapter(String name) {
        super(name);
    }

    @Override
    public void multipleMovesRequested(int count) {

    }

    @Override
    public void opponentMoveMade(Board.Grid move) {

    }

    @Override
    public void opponentChoiceMade(ChoiceSet choiceSet, int choice) {

    }

    @Override
    public void stoneSwapped() {

    }

    @Override
    public void gameStarted(Game game) {

    }

    @Override
    public void gameEnded(Result result) {

    }

    @Override
    public void exceptionCaught(Throwable t, Side side) {

    }
}
