package cn.yescallop.gomoku.game;

/**
 * Thrown when an illegal operation was performed, such as
 * making a move in an occupied grid. making a choice when it's not
 * the player's turn, etc.
 *
 * @author Scallop Ye
 */
public class IllegalOperationException extends RuntimeException {

    public IllegalOperationException(String message) {
        super(message);
    }
}
