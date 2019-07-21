package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Direction;
import cn.yescallop.gomoku.game.StoneType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Scallop Ye
 */
public final class RuleHelper {

    private RuleHelper() {
        // no instance
    }

    /**
     * Calculates the size of the longest chain
     * containing the specified grid.
     *
     * @param grid the grid.
     * @return the size.
     */
    public static int longestChainSize(Board.Grid grid) {
        int res = 0;
        for (int d = 0; d < 4; d++) {
            int cs = dcs(grid, d) + dcs(grid, Direction.reverse(d)) + 1;
            if (cs > res) res = cs;
        }
        return res;
    }

    /**
     * Calculates the size of the chain in the direction.
     *
     * @param grid the grid, exclusive.
     * @param d    the direction index.
     * @return the size.
     */
    private static int dcs(Board.Grid grid, int d) {
        StoneType stone = grid.stone();
        int res = 0;
        while (true) {
            grid = grid.adjacent(d);
            if (grid != null && grid.stone() == stone)
                res++;
            else break;
        }
        return res;
    }

    /**
     * Calculates the Chebyshev Distance
     * from the grid to the board center.
     *
     * @param grid      the grid.
     * @param boardSize the board size.
     * @return the Chebyshev Distance.
     */
    public static int chebyshevDistToCenter(Board.Grid grid, int boardSize) {
        int center = (boardSize - 1) / 2;
        int dx = Math.abs(grid.x() - center);
        int dy = Math.abs(grid.y() - center);
        return Math.max(dx, dy);
    }

    /**
     * Checks whether a forbidden move is
     * contained in the shapes.
     *
     * @param shapes the shapes.
     * @return whether there's a forbidden move,
     */
    public static boolean checkForbiddenMove(List<StoneShape> shapes) {
        int activeThrees = 0;
        int fours = 0;
        for (StoneShape p : shapes) {
            switch (p) {
                case OVERLINE:
                    return true;
                case ACTIVE_THREE:
                    activeThrees++;
                    break;
                case FOUR:
                    fours++;
                    break;
            }
        }
        return activeThrees >= 2 || fours >= 2;
    }

    /**
     * Searches shapes containing the specified grid.
     * TODO: Fix wrong shape judgement due to forbidden moves.
     *
     * @param grid the grid.
     * @return a list of shapes.
     */
    public static List<StoneShape> searchShapes(Board.Grid grid) {
        List<StoneShape> res = new ArrayList<>();
        for (int d = 0; d < 4; d++) {
            lsp(grid, d, res);
        }
        return res;
    }

    /**
     * Searches shapes in the line.
     *
     * @param grid the grid.
     * @param d    the direction index.
     * @param res  the list of shapes.
     */
    private static void lsp(Board.Grid grid, int d, List<StoneShape> res) {
        int[][] dsp = new int[2][2];
        boolean[] active = {dsp(grid, d, dsp[0]), dsp(grid, Direction.reverse(d), dsp[1])};
        int chain = dsp[0][0] + dsp[1][0] + 1;
        if (chain == 5) {
            res.add(StoneShape.FIVE);
            return;
        }
        if (chain > 5) {
            res.add(StoneShape.OVERLINE);
            return;
        }
        if (!active[0] && !active[1]) return;

        for (int i = 0; i < 2; i++) {
            int total = chain + dsp[i][1];
            if (total == 3) {
                if (active[i] && dsp[i ^ 1][1] == 0) {
                    // total = 3 and both directions active => Active Three
                    res.add(StoneShape.ACTIVE_THREE);
                    return;
                }
            } else if (total == 4) {
                res.add(StoneShape.FOUR);
                if (chain == 4) // Avoid double Fours
                    return;
            }
        }
    }

    /**
     * Calculates the size of the chain in the direction,
     * and when it reaches the end, searches ahead for a
     * second chain and also calculates the size.
     * Between the two chains is one empty grid.
     *
     * @param grid the grid, exclusive.
     * @param d    the direction index.
     * @param res  an array of length 2 to store the result,
     *             in which the first element is the size of
     *             the first chain, and the second element
     *             is the size of the second chain (-1 if no
     *             empty grid is reached).
     * @return true if the shape is active in the direction,
     * or else false.
     */
    private static boolean dsp(Board.Grid grid, int d, int[] res) {
        res[0] = 0;
        res[1] = -1; // -1 if no empty grid is reached
        int i = 0; // Increment when reaching an empty grid
        StoneType stone = grid.stone();
        while (i < 2) {
            grid = grid.adjacent(d);
            if (grid != null) {
                if (grid.stone() == stone) {
                    res[i]++; // Black
                    continue;
                } else if (grid.isEmpty()) {
                    i++; // Empty grid
                    if (i == 1)
                        res[1] = 0;
                    continue;
                }
            }
            return false; // White or Border
        }
        return res[1] == 0 ||
                ((grid = grid.adjacent(d)) == null || grid.stone() != StoneType.BLACK);
    }

}
