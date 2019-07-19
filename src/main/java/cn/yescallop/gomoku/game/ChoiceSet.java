package cn.yescallop.gomoku.game;

/**
 * A ChoiceSet represents a set of choices
 * consisting of general choices, moves or an integer range.
 *
 * @author Scallop Ye
 */
public class ChoiceSet {

    private final String description;
    private final Type type;
    private final Choice[] choices;
    private final Board.Point[] moves;
    private final int[] range;

    private ChoiceSet(String description, Type type, Choice[] choices, Board.Point[] moves, int[] range) {
        this.description = description;
        this.type = type;
        this.choices = choices;
        this.moves = moves;
        this.range = range;
    }

    public static ChoiceSet ofGeneralChoices(String description, Choice... choices) {
        if (choices.length < 2)
            throw new IllegalArgumentException("length < 2");
        return new ChoiceSet(description, Type.GENERAL, choices, null, null);
    }

    public static ChoiceSet ofMoves(String description, Board.Point[] moves) {
        if (moves.length < 2)
            throw new IllegalArgumentException("length < 2");
        return new ChoiceSet(description, Type.MOVES, null, moves, null);
    }

    public static ChoiceSet ofRange(String description, int start, int end) {
        if (start >= end)
            throw new IllegalArgumentException("start >= end");
        return new ChoiceSet(description, Type.RANGE, null, null, new int[]{start, end});
    }

    boolean validate(int choice) {
        switch (type) {
            case GENERAL:
                return choice >= 0 && choice < choices.length;
            case MOVES:
                return choice >= 0 && choice < moves.length;
            case RANGE:
                return choice >= range[0] && choice <= range[1];
        }
        return false;
    }

    public String description() {
        return description;
    }

    public Type type() {
        return type;
    }

    public Choice[] choices() {
        return choices;
    }

    public Board.Point[] moves() {
        return moves;
    }

    public int[] range() {
        return range;
    }

    public enum Type {
        GENERAL, MOVES, RANGE
    }
}
