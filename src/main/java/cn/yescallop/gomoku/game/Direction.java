package cn.yescallop.gomoku.game;

/**
 * A Direction represents a direction on the board.
 *
 * @author Scallop Ye
 */
public enum Direction {

    UP(new int[]{0, 1}),
    RIGHT(new int[]{1, 0}),
    UP_RIGHT(new int[]{1, 1}),
    DOWN_RIGHT(new int[]{1, -1}),
    DOWN(new int[]{0, -1}),
    LEFT(new int[]{-1, 0}),
    DOWN_LEFT(new int[]{-1, -1}),
    UP_LEFT(new int[]{-1, 1});

    private final int[] delta;

    Direction(int[] delta) {
        this.delta = delta;
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
