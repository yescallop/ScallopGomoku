package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.*;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import static cn.yescallop.gomoku.game.StoneShape.*;

/**
 * @author Scallop Ye
 */
public final class RuleHelper {

    /**
     * The maximum check depth of the forbidden moves.
     */
    private static final int FORBIDDEN_MOVE_MAX_CHECK_DEPTH = 5;

    private RuleHelper() {
        // no instance
    }

    public static void processMove(Game.Controller controller,
                                   Board.Grid grid, Side side) throws IllegalMoveException {
        switch (controller.game().rule().type()) {
            case FREESTYLE_GOMOKU:
                if (!grid.isEmpty())
                    throw new IllegalMoveException("Moving into an occupied grid");
                controller.makeMove();
                if (RuleHelper.longestRowLen(grid) >= 5) {
                    controller.end(Result.Type.ROW_COMPLETED, side);
                }
                break;
            case STANDARD_GOMOKU:
                if (!grid.isEmpty())
                    throw new IllegalMoveException("Moving into an occupied grid");
                controller.makeMove();
                if (RuleHelper.longestRowLen(grid) == 5) {
                    controller.end(Result.Type.ROW_COMPLETED, side);
                }
                break;
            case STANDARD_RENJU:
                if (!grid.isEmpty()) {
                    if (grid.stone() == StoneType.BLACK &&
                            controller.game().currentStoneType() == StoneType.WHITE &&
                            reportForbiddenMove(controller, grid)) {
                        return;
                    }
                    throw new IllegalMoveException("Moving into an occupied grid");
                }
                controller.makeMove();
                if (grid.stone() == StoneType.WHITE) {
                    if (RuleHelper.longestRowLen(grid) >= 5) {
                        controller.end(Result.Type.ROW_COMPLETED, side);
                    }
                } else if (controller.game().isStrict()) {
                    List<StoneShape> shapes = RuleHelper.searchShapes(grid);
                    if (shapes.contains(StoneShape.FIVE)) {
                        controller.end(Result.Type.ROW_COMPLETED, side);
                        return;
                    }
                    String description = RuleHelper.describeForbiddenMove(shapes);
                    if (description != null) {
                        controller.end(Result.Type.FORBIDDEN_MOVE_MADE, side.opposite(), description);
                    }
                } else if (RuleHelper.longestRowLen(grid) == 5) {
                    controller.end(Result.Type.ROW_COMPLETED, side);
                }
                break;
        }
    }

    private static boolean reportForbiddenMove(Game.Controller controller, Board.Grid grid) {
        Game game = controller.game();
        if (game.rule().type() != Rule.Type.STANDARD_RENJU || game.isStrict())
            return false;
        if (grid.moveIndex() == game.currentMoveIndex()) {
            List<StoneShape> shapes = RuleHelper.searchShapes(grid);
            String description = RuleHelper.describeForbiddenMove(shapes);
            if (description != null) {
                controller.end(Result.Type.FORBIDDEN_MOVE_MADE, game.sideByStoneType(StoneType.WHITE), description);
                return true;
            }
        } else if (RuleHelper.longestRowLen(grid) > 5) {
            controller.end(Result.Type.FORBIDDEN_MOVE_MADE, game.sideByStoneType(StoneType.WHITE), "Overline");
            return true;
        }
        return false;
    }

    /**
     * Calculates the length of the longest row
     * containing the specified grid.
     *
     * @param grid the grid.
     * @return the length.
     */
    public static int longestRowLen(Board.Grid grid) {
        if (grid.isEmpty())
            throw new IllegalArgumentException("Empty grid");
        int res = 0;
        for (int d = 0; d < 4; d++) {
            int cs = rowLen(grid, d);
            if (cs > res) res = cs;
        }
        return res;
    }

    /**
     * Calculates the length of the row in the line.
     *
     * @param grid the grid.
     * @param d the direction index.
     * @return the length.
     */
    private static int rowLen(Board.Grid grid, int d) {
        StoneType stone = grid.stone();
        int res = 1;
        Board.Grid gFwd = grid;
        Board.Grid gBwd = grid;
        int dr = Direction.reverse(d);

        while (true) {
            gFwd = gFwd.adjacent(d);
            if (gFwd != null && gFwd.stone() == stone)
                res++;
            else break;
        }
        while (true) {
            gBwd = gBwd.adjacent(dr);
            if (gBwd != null && gBwd.stone() == stone)
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
        if (shapes.contains(FIVE))
            return null;
        int openThrees = 0;
        int fours = 0;
        for (StoneShape s : shapes) {
            switch (s) {
                case OVERLINE:
                    return "Overline";
                case OPEN_THREE:
                    openThrees++;
                    break;
                case FOUR:
                    fours++;
                    break;
            }
        }
        if (openThrees >= 2 || fours >= 2) {
            StringJoiner sj = new StringJoiner("-");
            for (int i = 0; i < fours; i++) {
                sj.add("4");
            }
            for (int i = 0; i < openThrees; i++) {
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
        if (shapes.contains(FIVE))
            return false;
        int openThrees = 0;
        int fours = 0;
        for (StoneShape s : shapes) {
            switch (s) {
                case OVERLINE:
                    return true;
                case OPEN_THREE:
                    openThrees++;
                    break;
                case FOUR:
                    fours++;
                    break;
            }
        }
        return openThrees >= 2 || fours >= 2;
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
     * @param grid the grid.
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
     * Searches shapes in a line.
     *
     * @param grid the grid.
     * @param checked the last node of checked grids.
     * @param d the direction index.
     * @param res the list of shapes.
     */
    private static void lsp(Board.Grid grid, GridNode checked, int d, List<StoneShape> res) {
        int[][] dsp = new int[2][2]; // dsp results
        int[] ds = new int[]{d, Direction.reverse(d)}; // directions
        boolean[] open = {dsp(grid, checked, ds[0], dsp[0]), dsp(grid, checked, ds[1], dsp[1])};
        int row = dsp[0][0] + dsp[1][0] + 1; // row length
        if (row == 5) {
            res.add(FIVE);
            return;
        }
        if (row > 5) {
            res.add(OVERLINE);
            return;
        }

        for (int i = 0; i < 2; i++) {
            int[] fwdDsp = dsp[i];
            int[] bwdDsp = dsp[i ^ 1];
            int total = row;
            if (fwdDsp[1] != -1)
                total += fwdDsp[1];
            if (total == 3) {
                // total = 3, open forward and backward
                // there's an empty grid and no row => Open Three
                if (open[i] && bwdDsp[1] == 0) {
                    // Check forbidden move
                    Board.Grid fwdFirst = grid.adjacent(ds[i], fwdDsp[0] + 1);
                    Board.Grid fwdSecond = fwdFirst.adjacent(ds[i], fwdDsp[1] + 1);
                    Board.Grid bwd = grid.adjacent(ds[i ^ 1], bwdDsp[0] + 1);
                    if (cfm(fwdFirst, checked) ||
                            cfm(fwdSecond, checked) ||
                            cfm(bwd, checked)) {
                        return;
                    }

                    res.add(OPEN_THREE);
                    return;
                }
            } else if (total == 4) {
                if (row != 4) {
                    res.add(FOUR);
                } else if (fwdDsp[1] == 0 && bwdDsp[1] <= 0) {
                    res.add(FOUR);
                    return;
                }
            }
        }
    }

    /**
     * Checks whether there's a forbidden move in the grid.
     *
     * @param grid the grid.
     * @param checked the last node of checked grids.
     * @return whether there's a forbidden move.
     */
    private static boolean cfm(Board.Grid grid, GridNode checked) {
        if (FORBIDDEN_MOVE_MAX_CHECK_DEPTH == 1)
            return false;
        if (checked != null && (checked.index + 3 > FORBIDDEN_MOVE_MAX_CHECK_DEPTH || checked.search(grid)))
            return false;

        return checkForbiddenMove(searchShapes(grid, checked));
    }

    /**
     * Calculates the length of the row in the direction,
     * and when it reaches the end, searches forward for a
     * second row and also calculates the length.
     * Between the two rows is one empty grid.
     *
     * @param grid the grid, exclusive.
     * @param checked the last node of checked grids.
     * @param d the direction index.
     * @param res an array of length 2 to store the result,
     * in which the first element is the length of
     * the first row, and the second element
     * is the length of the second row (or -1 if no
     * empty grid is reached).
     * @return true if the shape is open in the direction,
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
            this.index = prev == null ? 0 : prev.index + 1;
        }

        boolean search(Board.Grid grid) {
            for (GridNode cur = this; cur != null; cur = cur.prev) {
                if (cur.value == grid) {
                    return true;
                }
            }
            return false;
        }

        GridNode next(Board.Grid grid) {
            return new GridNode(grid, this);
        }
    }
}
