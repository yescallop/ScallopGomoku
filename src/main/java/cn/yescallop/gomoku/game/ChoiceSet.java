package cn.yescallop.gomoku.game;

/**
 * A ChoiceSet represents a set of choices,
 * consisting of general choices, moves or a integer range.
 *
 * @author Scallop Ye
 */
public class ChoiceSet {

    private final Type type;
    private final Choice[] choices;
    private final Board.Grid[] moves;
    private final int[] range;

    private ChoiceSet(Type type, Choice[] choices, Board.Grid[] moves, int[] range) {
        this.type = type;
        this.choices = choices;
        this.moves = moves;
        this.range = range;
    }

    public static ChoiceSet ofGeneralChoices(Choice... choices) {
        if (choices.length < 2)
            throw new IllegalArgumentException("length < 2");
        return new ChoiceSet(Type.GENERAL, choices, null, null);
    }

    public static ChoiceSet ofMoves(Board.Grid[] moves) {
        if (moves.length < 2)
            throw new IllegalArgumentException("length < 2");
        return new ChoiceSet(Type.MOVES, null, moves, null);
    }

    public static ChoiceSet ofRange(int start, int end) {
        if (start >= end)
            throw new IllegalArgumentException("start >= end");
        return new ChoiceSet(Type.RANGE, null, null, new int[]{start, end});
    }

    public boolean validate(int choice) {
        if (choices != null) // General choices
            return choice >= 0 && choice < choices.length;
        if (moves != null) // Moves
            return choice >= 0 && choice < moves.length;
        // Number range
        return choice >= range[0] && choice <= range[1];
    }

    public Type type() {
        return type;
    }

    public Choice[] choices() {
        return choices;
    }

    public Board.Grid[] moves() {
        return moves;
    }

    public int[] range() {
        return range;
    }

    public enum Type {
        GENERAL, MOVES, RANGE
    }
}
