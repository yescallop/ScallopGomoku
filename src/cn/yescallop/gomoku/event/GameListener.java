package cn.yescallop.gomoku.event;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Choice;
import cn.yescallop.gomoku.game.Result;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.Rule;

/**
 * A game listener.
 *
 * @author Scallop Ye
 */
public interface GameListener {

    /**
     * Called when the game is started.
     * This method must not be blocking.
     *
     * @param board    The board of the game.
     * @param ruleType The rule type of the game.
     */
    void gameStarted(Board board, Rule.Type ruleType);

    /**
     * Called when a move is requested.
     * This method must not be blocking.
     *
     * @param side the side requested to move.
     */
    void moveRequested(Side side);

    /**
     * Called when a move is made.
     * This method must not be blocking.
     *
     * @param move the move.
     * @param side the side.
     */
    void moveMade(Board.Grid move, Side side);

    /**
     * Called when a choice is requested.
     * This method must not be blocking.
     *
     * @param choices the choices to choose from.
     * @param side    the side.
     */
    void choiceRequested(Choice[] choices, Side side);

    /**
     * Called when a choice is made.
     * This method must not be blocking.
     *
     * @param choice the move.
     * @param side   the side.
     */
    void choiceMade(Choice choice, Side side);

    /**
     * Called when the stones are swapped.
     * This method must not be blocking.
     */
    void stoneSwapped();

    /**
     * Called when the game ends.
     * This method must not be blocking.
     *
     * @param result      the result of the game.
     * @param winningSide the winning side.
     */
    void gameEnded(Result result, Side winningSide);
}
