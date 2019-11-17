package cn.yescallop.gomoku.game;

import java.io.PrintStream;

/**
 * A Board consists of the Grid matrix.
 *
 * @author Scallop Ye
 */
public class Board {

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
     * Creates a gomoku board.
     *
     * @param size the size of the board.
     */
    Board(int size) {
        this.size = size;
        matrix = new Grid[size][size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                matrix[y][x] = new Grid(this, x, y);
            }
        }

        // Fills the adjacent grids
        for (Direction d : Direction.values()) {
            int[] delta = d.delta();
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    int adjX = x + delta[0];
                    int adjY = y + delta[1];
                    if (adjX >= 0 && adjX < size && adjY >= 0 && adjY < size)
                        matrix[y][x].adjacentGrids[d.ordinal()] = matrix[adjY][adjX];
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

    public Grid getGrid(Point p) {
        return matrix[p.y][p.x];
    }

    /**
     * Prints the board to a PrintStream.
     *
     * @param out the output PrintStream.
     */
    public void printTo(PrintStream out) {
        for (int y = size - 1; y >= 0; y--) {
            if (y < 9) out.print(' ');
            out.print(y + 1);
            for (int x = 0; x < size; x++) {
                StoneType stone = matrix[y][x].stone;
                out.print(stone == null ? "  -" : (stone == StoneType.BLACK ? "  X" : "  0"));
            }
            out.println();
        }
        out.print("  ");
        for (int i = 0; i < size; i++) {
            out.print("  ");
            out.print((char) ('A' + i));
        }
        out.println();
    }

    /**
     * Makes a move.
     *
     * @param grid the grid where the move is made.
     * @param stone the stone type.
     */
    void move(Grid grid, StoneType stone) {
        grid.stone = stone;
        grid.moveIndex = ++curMoveIndex;
    }

    /**
     * A Grid holds its coordinate,
     * stone type, move index and the adjacent grid instances.
     */
    public static class Grid {

        private final Board board;
        private final int x, y;

        /**
         * An array holding the adjacent grid instances,
         * which is initialized in the constructor and should never be modified.
         * <p>
         * The mapping from indexes to directions can be found in the enum Direction.
         */
        private final Grid[] adjacentGrids = new Grid[8];

        /**
         * The stone type of this grid, or null if unoccupied.
         */
        private StoneType stone = null;

        /**
         * The move index of this grid starting from 1, or 0 if unoccupied.
         */
        private int moveIndex = 0;

        private Grid(Board board, int x, int y) {
            this.board = board;
            this.x = x;
            this.y = y;
        }

        public Board board() {
            return board;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public Point point() {
            return new Point(x, y);
        }

        public StoneType stone() {
            return stone;
        }

        public boolean isEmpty() {
            return stone == null;
        }

        public int moveIndex() {
            return moveIndex;
        }

        public Grid adjacent(int index) {
            return adjacentGrids[index];
        }

        public Grid adjacent(int index, int steps) {
            Grid grid = this;
            for (int i = 0; i < steps; i++) {
                grid = grid.adjacentGrids[index];
                if (grid == null)
                    return null;
            }
            return grid;
        }

        @Override
        public int hashCode() {
            return x << 16 | y;
        }

        @Override
        public String toString() {
            return "Grid{" + pointString() + ", " + stone + "}";
        }

        public String pointString() {
            return ((char) ('A' + x)) + String.valueOf(y + 1);
        }

        public boolean equalsSymmetrically(Grid other) {
            return (x == other.x || x + other.x + 1 == board.size) && (y == other.y || y + other.y + 1 == board.size);
        }
    }

    public static class Point {

        private final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Point parse(String s) {
            if (s.length() < 2)
                throw new IllegalArgumentException();

            char first = s.charAt(0);
            int x;
            if (first >= 'A' && first <= 'Z') {
                x = first - 'A';
            } else if (first >= 'a' && first <= 'z') {
                x = first - 'a';
            } else throw new IllegalArgumentException();

            char second = s.charAt(1);
            if (second < '1' || second > '9')
                throw new IllegalArgumentException();
            int y = Integer.parseInt(s.substring(1)) - 1;

            return new Point(x, y);
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        @Override
        public String toString() {
            return ((char) ('A' + x)) + String.valueOf(y + 1);
        }
    }
}
