package cn.yescallop.gomoku.game;

/**
 * A class indicating the result of an operation,
 * such as a move, an end of a game, etc.
 *
 * @author Scallop Ye
 */
public class Result {

    /**
     * Presets for general results
     */
    public static final Result SUCCESS = new Result(Type.SUCCESS, null);
    public static final Result INVALID_MOVE = new Result(Type.INVALID, "Invalid move");

    public static final Result WIN_CHAIN = new Result(Type.WIN,
            "A chain has been completed");
    public static final Result WIN_OPPONENT_FORBIDDEN_MOVE = new Result(Type.WIN,
            "The opponent made a forbidden move");
    public static final Result WIN_TIME_LIMIT_EXCEEDED = new Result(Type.WIN,
            "The opponent has exceeded the time limit");
    public static final Result WIN_OPPONENT_QUIT = new Result(Type.WIN,
            "The opponent has quited the game");

    public static final Result LOSS_CHAIN = new Result(Type.LOSS,
            "A chain has been completed");
    public static final Result LOSS_FORBIDDEN_MOVE = new Result(Type.LOSS,
            "You made a forbidden move");
    public static final Result LOSS_TIME_LIMIT_EXCEEDED = new Result(Type.WIN,
            "You have exceeded the time limit");
    public static final Result LOSS_QUIT = new Result(Type.WIN,
            "You have quited the game");

    public static final Result DRAW_SELF = new Result(Type.DRAW,
            "Your request for draw has been accepted");
    public static final Result DRAW_OPPONENT = new Result(Type.DRAW,
            "You have accepted the request for draw");

    private final Type type;
    private final String message;

    public Result(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type type() {
        return type;
    }

    public String message() {
        return message;
    }

    public enum Type {
        SUCCESS, INVALID, WIN, LOSS, DRAW
    }
}
