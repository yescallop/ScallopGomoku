package cn.yescallop.gomoku.ai;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Direction;
import cn.yescallop.gomoku.game.StoneShape;
import cn.yescallop.gomoku.game.StoneType;

import java.util.LinkedList;
import java.util.List;

import static cn.yescallop.gomoku.game.StoneShape.*;

/**
 * @author Scallop Ye
 */
public final class GomokuUtil {

    private GomokuUtil() {
        //no instance
    }

    public static int evaluate(List<StoneShape> list, boolean freestyle) {
        int res = 0;
        int fours = 0;
        boolean openFour = false;
        int openThrees = 0;
        for (StoneShape s : list) {
            switch (s) {
                case OPEN_FOUR:
                    res += 1000000000;
                    openFour = true;
                    continue;
                case BROKEN_OVERLINE:
                    if (freestyle)
                        s = SEMI_OPEN_FOUR;
                    else continue;
                case SEMI_OPEN_FOUR:
                    fours++;
                    break;
                case OPEN_THREE:
                    openThrees++;
                    break;
            }
            res += evaluate(s);
        }
        if (openFour)
            return res;
        if (fours >= 2)
            return 1000000000;
        if (fours + openThrees >= 2)
            res *= 10;
        return res;
    }

    public static int evaluate(StoneShape s) {
        switch (s) {
            case SEMI_OPEN_FOUR:
                return 5000000;
            case OPEN_THREE:
                return 4000000;
            case SEMI_OPEN_THREE:
                return 100000;
            case OPEN_TWO:
                return 10000;
            case SEMI_OPEN_TWO:
                return 1000;
        }
        return 0;
    }

    public static List<StoneShape> searchShapes(Board.Grid grid, StoneType stone) {
        assert grid.isEmpty() || grid.stone() == stone;

        List<StoneShape> res = new LinkedList<>();
        for (int d = 0; d < 4; d++) {
            ssl(grid, stone, d, res);
        }
        return res;
    }

    /**
     * Searches shapes in the line.
     *
     * @param grid the grid.
     * @param stone the stone type.
     * @param d the direction index.
     * @param res the list of shapes.
     */
    private static void ssl(Board.Grid grid, StoneType stone, int d, List<StoneShape> res) {
        int[] crl = crl(grid, stone, d);
        crlToShapes(crl, res);
    }

    private static void crlToShapes(int[] crl, List<StoneShape> res) {
        int central = crl[0];
        if (central == 5) {
            res.add(FIVE);
            return;
        }
        if (central > 5) {
            res.add(OVERLINE);
            return;
        }

        if (central == 1) {
            // -(X)-X-X-
            // fwd
            if (crl[1] == (1 << 16 | 1) && crl[2] == (1 << 16 | 1)) {
                res.add(SEMI_OPEN_THREE);
            }
            // bwd
            if (crl[3] == (1 << 16 | 1) && crl[4] == (1 << 16 | 1)) {
                res.add(SEMI_OPEN_THREE);
            }
            // -X-(X)-X-
            boolean flag = true;
            for (int k = 1; k < 5; k++) {
                if ((crl[k] & 0xffff) != 1) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                res.add(SEMI_OPEN_THREE);
        }

        int[] fwd = new int[]{crl[1] & 0xffff, crl[1] >>> 16};
        int[] bwd = new int[]{crl[3] & 0xffff, crl[3] >>> 16};

        int[] fwdEmpty = new int[]{crl[2] & 0xffff, crl[2] >>> 16};
        int[] bwdEmpty = new int[]{crl[4] & 0xffff, crl[4] >>> 16};

        int maxEmpty = maxEmpty(central);
        boolean flag = false;

        for (int i = 0; i < 2; i++) {
            if (i == 1) {
                int[] tmp = fwd;
                fwd = bwd;
                bwd = tmp;
                tmp = fwdEmpty;
                fwdEmpty = bwdEmpty;
                bwdEmpty = tmp;
            }
            if (central + fwd[0] == 5) {
                res.add(BROKEN_OVERLINE);
                continue;
            }
            int total = central;
            int open;
            if (fwd[0] != 0 && fwdEmpty[0] <= maxEmpty) {
                total += fwd[0];
                open = checkOpen(total, fwdEmpty[0], fwdEmpty[1], bwdEmpty[0], fwd[1], bwd[0]);
                if (open != -1)
                    res.add(StoneShape.ofLength(total, open == 1));
            } else {
                if (flag) return; // two same unbroken rows
                open = checkOpen(total, 0, fwdEmpty[0], bwdEmpty[0], fwd[0], bwd[0]);
                if (open != -1)
                    res.add(StoneShape.ofLength(total, open == 1));
                flag = true;
            }
        }
    }

    private static int checkOpen(int total, int emptyBetween,
                                 int empty1, int empty2,
                                 int row1, int row2) {
        if (total == 1)
            return -1;
        total += emptyBetween;
        if (total > 5)
            return -1;
        if (total == 5)
            return 0;
        boolean open = empty1 != 0 && empty2 != 0;
        int res = total + empty1 + empty2;
        if (row1 != 0) {
            open &= empty1 != 1;
            res--;
        }
        if (row2 != 0) {
            open &= empty2 != 1;
            res--;
        }
        if (res >= 6) {
            return open ? 1 : 0;
        } else if (res == 5) {
            return 0;
        } else {
            return -1;
        }
    }

    private static int[] crl(Board.Grid grid, StoneType stone, int d) {
        int central = 1;
        Board.Grid gFwd = grid;
        Board.Grid gBwd = grid;
        int dr = Direction.reverse(d);

        while (true) {
            gFwd = gFwd.adjacent(d);
            if (gFwd != null && gFwd.stone() == stone)
                central++;
            else break;
        }
        while (true) {
            gBwd = gBwd.adjacent(dr);
            if (gBwd != null && gBwd.stone() == stone)
                central++;
            else break;
        }

        int[] fwd = crd(gFwd, stone, d, central);
        int[] bwd = crd(gBwd, stone, dr, central);
        return new int[]{central, fwd[0], fwd[1], bwd[0], bwd[1]};
    }

    private static int[] crd(Board.Grid grid, StoneType stone, int d, int central) {
        if (central > 4)
            return new int[2];

        int count = 6 - central;
        int[] res = new int[4];
        int c = 0;
        boolean empty = true;
        for (int i = 0; i < count; i++) {
            if (grid != null) {
                StoneType curStone = grid.stone();
                if (curStone == stone) {
                    if (empty) {
                        c++;
                        empty = false;
                    }
                    res[c]++;
                    grid = grid.adjacent(d);
                    continue;
                } else if (curStone == null) {
                    if (!empty) {
                        c++;
                        if (c == 4)
                            break;
                        empty = true;
                    }
                    res[c]++;
                    grid = grid.adjacent(d);
                    continue;
                }
            }
            // border || opponent's
            break;
        }

        return new int[]{res[3] << 16 | res[1], res[2] << 16 | res[0]};
    }

    private static int maxEmpty(int central) {
        switch (central) {
            case 1:
                return 3;
            case 2:
                return 2;
            case 3:
                return 1;
            default:
                return 0;
        }
    }
}
