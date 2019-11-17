package cn.yescallop.gomoku.game;

import java.util.Optional;

/**
 * A Result indicates the result of a game.
 *
 * @author Scallop Ye
 */
public class Result {

    private final Type type;
    private final Side winningSide;
    private final String description;

    public Result(Type type, Side winningSide) {
        this(type, winningSide, null);
    }

    public Result(Type type, Side winningSide, String description) {
        this.type = type;
        this.winningSide = winningSide;
        this.description = description;
    }

    public Type type() {
        return type;
    }

    public Side winningSide() {
        return winningSide;
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    @Override
    public String toString() {
        return "Result{" +
                "type=" + type +
                ", winningSide=" + winningSide +
                ", description='" + description + '\'' +
                '}';
    }

    public enum Type {
        ROW_COMPLETED("A row has been completed"),
        FORBIDDEN_MOVE_MADE("A forbidden move was made"),
        TIMEOUT("Timeout"),
        EXCEPTION("Unexpected exception occurred"),
        INTERRUPT("Interrupted"),
        BOARD_FULL("The board is full"),
        QUIT("Player quitted"),
        DRAW_REQUEST_ACCEPTED("Draw request has been accepted");

        private final String message;

        Type(String message) {
            this.message = message;
        }

        public String message() {
            return message;
        }
    }
}
