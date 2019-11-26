package cn.yescallop.gomoku.player;

import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Move;
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

    public abstract Move requestMove(Move.Attribute attr, long timeoutMillis) throws Exception;

    public abstract int requestChoice(ChoiceSet choiceSet, long timeoutMillis) throws Exception;

    @Override
    public final void moveRequested(Move.Attribute attr, Side side) {
        //ignored
    }

    @Override
    public final void choiceRequested(ChoiceSet choiceSet, Side side) {
        //ignored
    }

    @Override
    public final void moveMade(Move move, Side side) {
        if (side != this.side)
            opponentMoveMade(move);
    }

    @Override
    public void playerPassed(Side side) {
        if (side != this.side)
            opponentPassed();
    }

    @Override
    public final void moveOffered(Move move, Side side) {
        if (side != this.side)
            opponentMoveOffered(move);
    }

    @Override
    public final void choiceMade(ChoiceSet choiceSet, int choice, Side side) {
        if (side != this.side)
            opponentChoiceMade(choiceSet, choice);
    }
}
