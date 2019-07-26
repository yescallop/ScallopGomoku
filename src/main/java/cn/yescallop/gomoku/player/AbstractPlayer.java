package cn.yescallop.gomoku.player;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Side;

import java.util.Objects;

/**
 * @author Scallop Ye
 */
public abstract class AbstractPlayer implements Player {

    protected final String name;
    protected Side side = null;

    /**
     * Constructs a player with specified name.
     *
     * @param name the name.
     */
    protected AbstractPlayer(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public final void setSide(Side side) {
        Objects.requireNonNull(side);
        if (this.side == null) {
            this.side = side;
        } else throw new IllegalStateException("Side already set");
    }

    public abstract Board.Point requestMove(long timeoutMillis) throws Exception;

    public abstract int requestChoice(ChoiceSet choiceSet, long timeoutMillis) throws Exception;

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
    public final void moveOffered(Board.Grid move, Side side) {
        if (side != this.side)
            opponentMoveOffered(move);
    }

    @Override
    public final void choiceMade(ChoiceSet choiceSet, int choice, Side side) {
        if (side != this.side)
            opponentChoiceMade(choiceSet, choice);
    }
}
