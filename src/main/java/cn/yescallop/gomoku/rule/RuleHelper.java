package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Direction;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.StoneType;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Scallop Ye
 */
public final class RuleHelper {

    /**
     * The maximum check depth of the interrelationship
     * of the forbidden moves.
     */
    private static final int FORBIDDEN_MOVE_MAX_CHECK_DEPTH = 5;

    private RuleHelper() {
        // no instance
    }

    /**
     * Calculates the size of the longest row
     * containing the specified grid.
     *
     * @param grid the grid.
     * @return the size.
     */
    public static int longestRowSize(Board.Grid grid) {
        if (grid.isEmpty())
            throw new IllegalArgumentException("Empty grid");
        int res = 0;
        for (int d = 0; d < 4; d++) {
            int cs = dcs(grid, d) + dcs(grid, Direction.reverse(d)) + 1;
            if (cs > res) res = cs;
        }
        return res;
    }

    /**
     * Calculates the size of the row in the direction.
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
     * @param grid the grid.
     * @return the Chebyshev Distance.
     */
    public static int chebyshevDistToCenter(Board.Grid grid) {
        int center = (grid.board().size() - 1) / 2;
        int dx = Math.abs(grid.x() - center);
        int dy = Math.abs(grid.y() - center);
        return Math.max(dx, dy);
    }

    /**
     * Describes the forbidden move contained in the shapes.
     *
     * @param shapes the shapes.
     * @return a String describing the forbidden move if
     * there is a forbidden move, or else null.
     */
    public static String describeForbiddenMove(List<StoneShape> shapes) {
        if (shapes.contains(StoneShape.FIVE))
            return null;
        int activeThrees = 0;
        int fours = 0;
        for (StoneShape s : shapes) {
            switch (s) {
                case OVERLINE:
                    return "Overline";
                case ACTIVE_THREE:
                    activeThrees++;
                    break;
                case FOUR:
                    fours++;
                    break;
            }
        }
        if (activeThrees >= 2 || fours >= 2) {
            StringJoiner sj = new StringJoiner("-");
            for (int i = 0; i < fours; i++) {
                sj.add("4");
            }
            for (int i = 0; i < activeThrees; i++) {
                sj.add("3");
            }
            return sj + " Forbidden Move";
        }
        return null;
    }

    /**
     * Checks whether a forbidden move is
     * contained in the shapes.
     *
     * @param shapes the shapes.
     * @return whether there's a forbidden move,
     */
    public static boolean checkForbiddenMove(List<StoneShape> shapes) {
        if (shapes.contains(StoneShape.FIVE))
            return false;
        int activeThrees = 0;
        int fours = 0;
        for (StoneShape s : shapes) {
            switch (s) {
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
     *
     * @param grid the grid.
     * @return a list of shapes.
     */
    public static List<StoneShape> searchShapes(Board.Grid grid) {
        return searchShapes(grid, null);
    }

    /**
     * Searches shapes containing the specified grid.
     *
     * @param grid    the grid.
     * @param checked the last node of checked grids.
     * @return a list of shapes.
     */
    private static List<StoneShape> searchShapes(Board.Grid grid, GridNode checked) {
        if (grid.isEmpty()) {
            checked = checked == null ? new GridNode(grid, null) : checked.next(grid);
        }

        List<StoneShape> res = new LinkedList<>();
        for (int d = 0; d < 4; d++) {
            lsp(grid, checked, d, res);
        }
        return res;
    }

    /**
     * Searches shapes in the line.
     *
     * @param grid    the grid.
     * @param checked the last node of checked grids.
     * @param d       the direction index.
     * @param res     the list of shapes.
     */
    private static void lsp(Board.Grid grid, GridNode checked, int d, List<StoneShape> res) {
        int[][] dsp = new int[2][2]; // dsp results
        int[] ds = new int[]{d, Direction.reverse(d)}; // directions
        boolean[] active = {dsp(grid, checked, ds[0], dsp[0]), dsp(grid, checked, ds[1], dsp[1])};
        int row = dsp[0][0] + dsp[1][0] + 1; // row size
        if (row == 5) {
            res.add(StoneShape.FIVE);
            return;
        }
        if (row > 5) {
            res.add(StoneShape.OVERLINE);
            return;
        }
        if (!active[0] && !active[1]) return;

        for (int i = 0; i < 2; i++) {
            int[] fwdDsp = dsp[i];
            int[] oppDsp = dsp[i ^ 1];
            int total = row + fwdDsp[1];
            if (total == 3) {
                // total = 3, active ahead and no row behind => Active Three
                if (active[i] && oppDsp[1] == 0) {
                    // Check forbidden move
                    Board.Grid aheadFirst = grid.adjacent(ds[i], fwdDsp[0] + 1);
                    Board.Grid aheadSecond = aheadFirst.adjacent(ds[i], fwdDsp[1] + 1);
                    Board.Grid behind = grid.adjacent(ds[i ^ 1], oppDsp[0] + 1);
                    if (cfm(aheadFirst, checked) ||
                            cfm(aheadSecond, checked) ||
                            cfm(behind, checked)) {
                        return;
                    }

                    res.add(StoneShape.ACTIVE_THREE);
                    return;
                }
            } else if (total == 4) {
                res.add(StoneShape.FOUR);
                if (row == 4) // Avoid double Fours
                    return;
            }
        }
    }

    /**
     * Checks whether there's a forbidden move in the grid.
     *
     * @param grid    the grid.
     * @param checked the last node of checked grids.
     * @return whether there's a forbidden move,
     */
    private static boolean cfm(Board.Grid grid, GridNode checked) {
        if (checked != null && (checked.index >= FORBIDDEN_MOVE_MAX_CHECK_DEPTH || checked.search(grid)))
            return false;

        return checkForbiddenMove(searchShapes(grid, checked));
    }

    /**
     * Calculates the size of the row in the direction,
     * and when it reaches the end, searches ahead for a
     * second row and also calculates the size.
     * Between the two rows is one empty grid.
     *
     * @param grid    the grid, exclusive.
     * @param checked the last node of checked grids.
     * @param d       the direction index.
     * @param res     an array of length 2 to store the result,
     *                in which the first element is the size of
     *                the first row, and the second element
     *                is the size of the second row (or -1 if no
     *                empty grid is reached).
     * @return true if the shape is active in the direction,
     * or else false.
     */
    private static boolean dsp(Board.Grid grid, GridNode checked, int d, int[] res) {
        res[0] = 0;
        res[1] = -1; // -1 if no empty grid is reached
        int i = 0; // Increment when reaching an empty grid
        while (i < 2) {
            grid = grid.adjacent(d);
            if (grid != null) {
                if (grid.stone() == StoneType.BLACK) {
                    res[i]++; // Black
                    continue;
                } else if (grid.isEmpty()) {
                    if (checked != null && checked.search(grid)) {
                        res[i]++; // Regard it as Black
                        continue;
                    }
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

    public static void validateStandardOpening(Board.Grid grid, int index) throws IllegalMoveException {
        switch (index) {
            case 1:
                if (chebyshevDistToCenter(grid) != 0)
                    throw new IllegalMoveException("The first move not in the center");
                break;
            case 2:
                if (chebyshevDistToCenter(grid) > 1)
                    throw new IllegalMoveException("The second move outside central 3x3 area");
                break;
            case 3:
                if (chebyshevDistToCenter(grid) > 2)
                    throw new IllegalMoveException("The third move outside central 5x5 area");
                break;
        }
    }

    private static class GridNode {
        final Board.Grid value;
        final GridNode prev;
        final int index;

        GridNode(Board.Grid value, GridNode prev) {
            this.value = value;
            this.prev = prev;
            this.index = prev == null ? 1 : prev.index + 1;
        }

        boolean search(Board.Grid grid) {
            GridNode cur = this;
            while (cur != null) {
                if (cur.value == grid)
                    return true;
                cur = cur.prev;
            }
            return false;
        }

        GridNode next(Board.Grid grid) {
            return new GridNode(grid, this);
        }
    }
}
