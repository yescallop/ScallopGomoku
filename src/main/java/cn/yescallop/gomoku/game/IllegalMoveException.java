package cn.yescallop.gomoku.game;

/**
 * Thrown if an illegal move was made.
 *
 * @author Scallop Ye
 */
public class IllegalMoveException extends Exception {

    public IllegalMoveException(String message) {
        super(message);
    }
}
