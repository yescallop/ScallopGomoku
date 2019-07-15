package cn.yescallop.gomoku.player;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.Result;

/**
 * @author Scallop Ye
 */
public abstract class PlayerAdapter extends Player {

    @Override
    protected void multipleMovesRequested(int count) {

    }

    @Override
    protected void opponentMoveMade(Board.Grid move) {

    }

    @Override
    protected void opponentChoiceMade(int choice) {

    }

    @Override
    public void gameStarted(Game.Settings settings) {

    }

    @Override
    public void stoneSwapped() {

    }

    @Override
    public void gameEnded(Result result) {

    }

    @Override
    public void exceptionCaught(Throwable t) {

    }
}
