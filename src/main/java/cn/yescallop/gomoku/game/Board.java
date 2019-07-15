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

    private final int size;

    /**
     * The matrix of the board.
     * <p>
     * Real board:
     * A2, B2
     * A1, B1
     * matrix:
     * [[A1, B1], [A2, B2]]
     */
    private final Grid[][] matrix;

    /**
     * The current move index.
     */
    private int curMoveIndex = 0;

    /**
     * Creates a standard 15x15 gomoku board.
     */
    Board(int size) {
        this.size = size;
        matrix = new Grid[size][size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                matrix[y][x] = new Grid(x, y);
            }
        }

        // Fills the adjacent grids
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                for (int i = 0; i < 8; i++) {
                    int adjX = x + ADJ_DELTA[i][0];
                    int adjY = y + ADJ_DELTA[i][1];
                    if (adjX >= 0 && adjX < size && adjY >= 0 && adjY < size)
                        matrix[y][x].adjacentGrids[i] = matrix[adjY][adjX];
                }
            }
        }
    }

    /**
     * Gets the size of the board.
     *
     * @return the size.
     */
    public int size() {
        return size;
    }

    /**
     * Gets the current move index.
     *
     * @return the current move index.
     */
    public int currentMoveIndex() {
        return curMoveIndex;
    }

    /**
     * Gets a grid.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @return the specified Grid instance.
     */
    public Grid getGrid(int x, int y) {
        return matrix[y][x];
    }

    /**
     * Makes a move.
     *
     * @param grid  the grid where the move is made.
     * @param stone the stone type.
     */
    void move(Grid grid, StoneType stone) {
        grid.stone = stone;
        grid.moveIndex = ++curMoveIndex;
    }

    /**
     * A grid instance holding its coordinate,
     * stone type, move index and the adjacent grid instances.
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
         * The stone of this grid, null if unoccupied.
         */
        private StoneType stone = null;

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

        public StoneType stone() {
            return stone;
        }

        public boolean isOccupied() {
            return stone != null;
        }

        public int moveIndex() {
            return moveIndex;
        }

        public Grid adjacent(int direction) {
            return adjacentGrids[direction];
        }

        @Override
        public String toString() {
            return "Grid{" + x + ", " + y + ", " + stone + "}";
        }
    }
}
