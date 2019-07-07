package cn.yescallop.gomoku.game;

/**
 * @author Scallop Ye
 */
public class Board {

    // Directions used for identifying adjacent grids
    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_DOWN = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_LEFT = 3;
    public static final int DIRECTION_UP_RIGHT = 4;
    public static final int DIRECTION_UP_LEFT = 5;
    public static final int DIRECTION_DOWN_RIGHT = 6;
    public static final int DIRECTION_DOWN_LEFT = 7;

    /**
     * {x, y} delta for each direction.
     */
    private static final int[][] ADJ_DELTA = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    /**
     * The matrix of the board.
     * <p>
     * Real board:
     * A2, B2
     * A1, B1
     * matrix:
     * [[A1, B1], [A2, B2]]
     * matrixLeftTilted:
     * [[A1], [A2, B1], [B2]]
     * matrixRightTilted:
     * [[B1], [A1, B2], [A2]]
     */
    private final Grid[][] matrix = new Grid[15][15];

    /**
     * The current move index.
     */
    private int curMoveIndex;

    /**
     * Creates a standard 15x15 gomoku board.
     */
    Board() {
        for (int y = 0; y < 15; y++) {
            for (int x = 0; x < 15; x++) {
                matrix[y][x] = new Grid(x, y);
            }
        }

        // Fills the adjacent grids
        for (int y = 0; y < 15; y++) {
            for (int x = 0; x < 15; x++) {
                for (int i = 0; i < 8; i++) {
                    int adjX = x + ADJ_DELTA[i][0];
                    int adjY = y + ADJ_DELTA[i][1];
                    if (adjX >= 0 && adjX < 15 && adjY >= 0 && adjY < 15)
                        matrix[y][x].adjacentGrids[i] = matrix[adjY][adjX];
                }
            }
        }
    }

    /**
     * Gets the current move index.
     */
    public int currentMoveIndex() {
        return curMoveIndex;
    }

    public Grid getGrid(int x, int y) {
        if (x < 0 || x > 15 || y < 0 || y > 15)
            throw new IndexOutOfBoundsException("Out of board");
        return matrix[y][x];
    }

    /**
     * Makes a move.
     *
     * @param grid the grid where the move is made.
     * @param side the side which requested the move.
     */
    void move(Grid grid, Side side) {
        if (grid.side != null)
            throw new IllegalOperationException("Moving into an occupied grid");
        grid.side = side;
        grid.moveIndex = ++curMoveIndex;
    }

    /**
     * A grid instance holding its coordinate,
     * side type, move index and the adjacent grid instances.
     */
    public class Grid {

        private final int x, y;

        /**
         * An array holding the adjacent grid instances,
         * which is initialized in the constructor and should never be modified.
         * <p>
         * The mapping from indexes to directions can be found in the class Board as integer constants.
         */
        private final Grid[] adjacentGrids = new Grid[8];

        /**
         * The side of this grid, null if unoccupied.
         */
        private Side side = null;

        /**
         * The move index of this grid starting from 1, 0 if unoccupied.
         */
        private int moveIndex = 0;

        private Grid(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public Side side() {
            return side;
        }

        public boolean isOccupied() {
            return side != null;
        }

        public int moveIndex() {
            return moveIndex;
        }

        public Grid adjacent(int direction) {
            return adjacentGrids[direction];
        }

        @Override
        public String toString() {
            return "Grid{" + x + ", " + y + ", " + side + "}";
        }
    }
}
