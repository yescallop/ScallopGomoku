package cn.yescallop.gomoku.game;

/**
 * A Direction represents a direction on the board.
 *
 * @author Scallop Ye
 */
public enum Direction {

    UP(0, new int[]{0, 1}),
    RIGHT(1, new int[]{1, 0}),
    UP_RIGHT(2, new int[]{1, 1}),
    DOWN_RIGHT(3, new int[]{1, -1}),
    DOWN(4, new int[]{0, -1}),
    LEFT(5, new int[]{-1, 0}),
    DOWN_LEFT(6, new int[]{-1, -1}),
    UP_LEFT(7, new int[]{-1, 1});

    private final int index;
    private final int[] delta;

    Direction(int index, int[] delta) {
        this.index = index;
        this.delta = delta;
    }

    public int index() {
        return index;
    }

    public int[] delta() {
        return delta;
    }

    public Direction opposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case UP_RIGHT:
                return DOWN_LEFT;
            case UP_LEFT:
                return DOWN_RIGHT;
            case DOWN_RIGHT:
                return UP_LEFT;
            case DOWN_LEFT:
                return UP_RIGHT;
        }
        return null;
    }

    /**
     * Reverses a direction index.
     *
     * @param d the direction index.
     * @return the reversed direction index.
     */
    public static int reverse(int d) {
        return d ^ 0b100;
    }
}
