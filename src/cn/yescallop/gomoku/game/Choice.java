package cn.yescallop.gomoku.game;

/**
 * Declares a choice.
 *
 * @author Scallop Ye
 */
public enum Choice {

    CHOOSE_BLACK("Choose black"),
    CHOOSE_WHITE("Choose white"),
    CONTINUE("Continue"),
    ACCEPT_DRAW("Accept the draw request"),
    DECLINE_DRAW("Decline the draw request");

    private final String description;

    Choice(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}