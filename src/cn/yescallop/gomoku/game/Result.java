package cn.yescallop.gomoku.game;

/**
 * A Result indicates the result of a game.
 *
 * @author Scallop Ye
 */
public class Result {

    private final Type type;
    private final Side winningSide;

    public Result(Type type, Side winningSide) {
        this.type = type;
        this.winningSide = winningSide;
    }

    public Type type() {
        return type;
    }

    public Side winningSide() {
        return winningSide;
    }

    public enum Type {
        CHAIN_COMPLETED("A chain has been completed"),
        FORBIDDEN_MOVE_MADE("A forbidden move was made"),
        TIMEOUT("Timeout"),
        EXCEPTION("Exception occurred"),
        QUIT("Player quitting"),
        DRAW_REQUEST_ACCEPTED("Draw request has been accepted");

        private final String description;

        Type(String description) {
            this.description = description;
        }

        public String description() {
            return description;
        }
    }
}
