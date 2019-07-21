package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Direction;
import cn.yescallop.gomoku.game.StoneType;

import java.util.stream.Stream;

import static cn.yescallop.gomoku.game.Direction.*;

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
        return Stream.of(UP, RIGHT, UP_RIGHT, DOWN_RIGHT)
                .mapToInt(d -> dcs(grid, d) + dcs(grid, d.opposite()) + 1)
                .max().getAsInt();
    }

    /**
     * Calculates the size of the longest chain to the direction.
     *
     * @param grid the grid, exclusive.
     * @param d    the direction.
     * @return the size.
     */
    private static int dcs(Board.Grid grid, Direction d) {
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
}
