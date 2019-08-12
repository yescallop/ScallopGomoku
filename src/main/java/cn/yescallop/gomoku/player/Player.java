package cn.yescallop.gomoku.player;

import cn.yescallop.gomoku.event.GameListener;
import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Side;

/**
 * @author Scallop Ye
 */
public interface Player extends GameListener {

    /**
     * Gets the name of this player.
     *
     * @return the name.
     */
    String name();

    /**
     * Sets the side of this player.
     * Side can only be set once.
     *
     * @param side the side.
     */
    void setSide(Side side);

    /**
     * Requests a move with specified timeout.
     *
     * @param timeoutMillis timeout in milliseconds, 0 for no timeout.
     * @return the move requested.
     * @throws InterruptedException if the current thread was interrupted while waiting.
     */
    Board.Point requestMove(long timeoutMillis) throws Exception;

    /**
     * Requests a choice with specified timeout.
     *
     * @param choiceSet the choice set.
     * @param timeoutMillis timeout in milliseconds, 0 for no timeout.
     * @return the choice requested.
     * @throws InterruptedException if the current thread was interrupted while waiting.
     */
    int requestChoice(ChoiceSet choiceSet, long timeoutMillis) throws Exception;

    /**
     * Called when multiple moves are requested.
     *
     * @param count the count of moves requested.
     */
    void multipleMovesRequested(int count);

    /**
     * Called when the opponent makes a move.
     *
     * @param move the move made.
     */
    void opponentMoveMade(Board.Grid move);

    /**
     * Called when the opponent offers a move
     * as part of a set of multiple moves.
     *
     * @param move the move.
     */
    void opponentMoveOffered(Board.Grid move);

    /**
     * Called when the opponent makes a choice.
     *
     * @param choiceSet the choice set.
     * @param choice the choice made.
     */
    void opponentChoiceMade(ChoiceSet choiceSet, int choice);
}
