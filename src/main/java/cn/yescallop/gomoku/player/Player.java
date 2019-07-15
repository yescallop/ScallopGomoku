package cn.yescallop.gomoku.player;

import cn.yescallop.gomoku.event.GameListener;
import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Side;

import java.util.Objects;

/**
 * @author Scallop Ye
 */
public abstract class Player implements GameListener {

    private Side side = null;

    /**
     * Sets the side of this player.
     * Side can only be set once.
     *
     * @param side the side.
     */
    public final void setSide(Side side) {
        Objects.requireNonNull(side);
        if (this.side == null) {
            this.side = side;
        } else throw new IllegalStateException("Side already set");
    }

    /**
     * Requests a move with specified timeout.
     *
     * @param timeoutMillis timeout in milliseconds, 0 for no timeout.
     * @return the move requested.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     */
    public abstract Board.Grid requestMove(long timeoutMillis) throws InterruptedException;

    /**
     * Requests a choice with specified timeout.
     *
     * @param timeoutMillis timeout in milliseconds, 0 for no timeout.
     * @return the choice requested.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     */
    public abstract int requestChoice(ChoiceSet choiceSet, long timeoutMillis) throws InterruptedException;

    @Override
    public final void moveRequested(Side side) {
        //ignored
    }

    @Override
    public final void multipleMovesRequested(int count, Side side) {
        if (side == this.side)
            multipleMovesRequested(count);
    }

    @Override
    public final void choiceRequested(ChoiceSet choiceSet, Side side) {
        //ignored
    }

    @Override
    public final void moveMade(Board.Grid move, Side side) {
        if (side != this.side)
            opponentMoveMade(move);
    }

    @Override
    public final void choiceMade(int choice, Side side) {
        if (side != this.side)
            opponentChoiceMade(choice);
    }

    /**
     * Called when multiple moves are requested.
     * TODO: Implements multiple moves
     *
     * @param count the count of moves requested.
     */
    protected abstract void multipleMovesRequested(int count);

    /**
     * Called when the opponent makes a move.
     *
     * @param move the move made.
     */
    protected abstract void opponentMoveMade(Board.Grid move);

    /**
     * Called when the opponent makes a choice.
     *
     * @param choice the choice made.
     */
    protected abstract void opponentChoiceMade(int choice);
}
