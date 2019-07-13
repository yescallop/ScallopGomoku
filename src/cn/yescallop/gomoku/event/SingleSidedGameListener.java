package cn.yescallop.gomoku.event;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Choice;
import cn.yescallop.gomoku.game.Result;
import cn.yescallop.gomoku.game.Side;

/**
 * A single sided game listener.
 *
 * @author Scallop Ye
 */
public abstract class SingleSidedGameListener implements GameListener {

    private final Side side;

    public SingleSidedGameListener(Side side) {
        this.side = side;
    }

    @Override
    public final void moveRequested(Side side) {
        if (side == this.side)
            moveRequested();
    }

    @Override
    public final void moveMade(Board.Grid move, Side side) {
        if (side != this.side)
            opponentMoveMade(move);
    }

    @Override
    public final void choiceRequested(Choice[] choices, Side side) {
        if (side == this.side)
            choiceRequested(choices);
    }

    @Override
    public final void choiceMade(Choice choice, Side side) {
        if (side != this.side)
            opponentChoiceMade(choice);
    }

    @Override
    public final void gameEnded(Result result, Side winningSide) {
        gameEnded(result, winningSide == this.side);
    }

    protected abstract void moveRequested();

    protected abstract void opponentMoveMade(Board.Grid move);

    protected abstract void choiceRequested(Choice[] choices);

    protected abstract void opponentChoiceMade(Choice choice);

    protected abstract void gameEnded(Result result, boolean winning);
}
