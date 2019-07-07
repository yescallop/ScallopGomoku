package cn.yescallop.gomoku.game;

/**
 * Thrown when a move violates the rule.
 *
 * @author Scallop Ye
 */
public class RuleViolationException extends Exception {

    public RuleViolationException(String message) {
        super(message);
    }
}
