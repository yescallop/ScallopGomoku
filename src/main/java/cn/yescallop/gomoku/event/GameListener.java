package cn.yescallop.gomoku.event;

import cn.yescallop.gomoku.game.*;

/**
 * A game listener.
 *
 * @author Scallop Ye
 */
public interface GameListener {

    /**
     * Called when the game is started.
     *
     * @param settings The settings of the game.
     */
    void gameStarted(Game.Settings settings);

    /**
     * Called when a move is requested.
     *
     * @param side the side requested to move.
     */
    void moveRequested(Side side);

    /**
     * Called when multiple moves are requested.
     * TODO: Implements multiple moves
     *
     * @param side the side requested to move.
     */
    void multipleMovesRequested(int count, Side side);

    /**
     * Called when a move is made.
     *
     * @param move the move.
     * @param side the side.
     */
    void moveMade(Board.Grid move, Side side);

    /**
     * Called when a choice is requested.
     *
     * @param choiceSet the choice set.
     * @param side      the side.
     */
    void choiceRequested(ChoiceSet choiceSet, Side side);

    /**
     * Called when a choice is made.
     *
     * @param choice the choice.
     * @param side   the side.
     */
    void choiceMade(int choice, Side side);

    /**
     * Called when the stones are swapped.
     */
    void stoneSwapped();

    /**
     * Called when the game ends.
     *
     * @param result the result of the game.
     */
    void gameEnded(Result result);

    /**
     * Called when an exception is caught.
     *
     * @param t the exception.
     */
    void exceptionCaught(Throwable t);
}
