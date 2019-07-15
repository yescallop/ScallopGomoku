package cn.yescallop.gomoku.game;

/**
 * Thrown when an illegal move was made.
 *
 * @author Scallop Ye
 */
public class IllegalChoiceException extends Exception {

    public IllegalChoiceException(String message) {
        super(message);
    }
}
