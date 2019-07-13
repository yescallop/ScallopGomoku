package cn.yescallop.gomoku.game;

/**
 * An enum indicating the result of a game.
 *
 * @author Scallop Ye
 */
public enum Result {

    CHAIN_COMPLETED("A chain has been completed"),
    FORBIDDEN_MOVE_MADE("A forbidden move was made"),
    TIME_LIMIT_EXCEEDED("The time limit has been exceeded"),
    QUIT("Player quitting"),
    DRAW("Draw request has been accepted");

    private final String description;

    Result(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
