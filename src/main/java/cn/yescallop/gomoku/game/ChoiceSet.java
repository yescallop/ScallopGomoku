package cn.yescallop.gomoku.game;

/**
 * A ChoiceSet represents a set of choices
 * consisting of strings, moves or move counts.
 *
 * @author Scallop Ye
 */
public class ChoiceSet {

    private final Type type;
    private final String[] strings;
    private final Board.Grid[] moves;
    private final int maxMoveCount;

    private ChoiceSet(Type type, String[] strings, Board.Grid[] moves, int maxMoveCount) {
        this.type = type;
        this.strings = strings;
        this.moves = moves;
        this.maxMoveCount = maxMoveCount;
    }

    public static ChoiceSet ofStrings(String... strings) {
        if (strings.length < 2)
            throw new IllegalArgumentException("length < 2");
        return new ChoiceSet(Type.STRINGS, strings, null, 0);
    }

    public static ChoiceSet ofMoves(Board.Grid[] moves) {
        if (moves.length < 2)
            throw new IllegalArgumentException("length < 2");
        return new ChoiceSet(Type.MOVES, null, moves, 0);
    }

    public static ChoiceSet ofMoveCount(int maxMoveCount) {
        if (maxMoveCount < 2)
            throw new IllegalArgumentException("maxMoveCount < 2");
        return new ChoiceSet(Type.MOVE_COUNT, null, null, maxMoveCount);
    }

    public static ChoiceSet ofMaxMoveCount(Board board) {
        int size = board.size();
        return ofMoveCount(size * size - board.currentMoveIndex());
    }

    boolean validate(int choice) {
        switch (type) {
            case STRINGS:
                return choice >= 0 && choice < strings.length;
            case MOVES:
                return choice >= 0 && choice < moves.length;
            case MOVE_COUNT:
                return choice >= 1 && choice <= maxMoveCount;
        }
        return false;
    }

    public Type type() {
        return type;
    }

    public String[] strings() {
        return strings;
    }

    public Board.Grid[] moves() {
        return moves;
    }

    public int maxMoveCount() {
        return maxMoveCount;
    }

    public enum Type {
        STRINGS, MOVES, MOVE_COUNT
    }
}
