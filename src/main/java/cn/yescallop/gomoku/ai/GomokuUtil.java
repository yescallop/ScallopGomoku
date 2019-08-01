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

    public static List<StoneShape> searchShapes(Board.Grid grid, StoneType stone, boolean standard) {
        assert grid.isEmpty() || grid.stone() == stone;

        List<StoneShape> res = new LinkedList<>();
        for (int d = 0; d < 4; d++) {
            ssl(grid, stone, d, standard, res);
        }
        return res;
    }

    /**
     * Searches shapes in the line.
     *
     * @param grid     the grid.
     * @param stone    the stone type.
     * @param d        the direction index.
     * @param standard whether the rule is Standard Gomoku.
     * @param res      the list of shapes.
     */
    private static void ssl(Board.Grid grid, StoneType stone, int d, boolean standard, List<StoneShape> res) {
        int[] crl = crl(grid, stone, d);
        if (standard) {
            sslStandard(crl, res);
        } else {
            sslFreestyle(crl, res);
        }
    }

    private static void sslStandard(int[] crl, List<StoneShape> res) {
        int central = crl[0];
        if (central == 5) {
            res.add(FIVE);
            return;
        }
        if (central > 5) {
            return;
        }

        int i = 0;
        int j = 2;

        if (central == 1) {
            // -(X)-X-X-
            // fwd
            if (crl[1] == (1 << 16 | 1) && crl[2] == (1 << 16 | 1)) {
                res.add(SEMI_OPEN_THREE);
                i = 1;
            }
            // bwd
            if (crl[3] == (1 << 16 | 1) && crl[4] == (1 << 16 | 1)) {
                res.add(SEMI_OPEN_THREE);
                j = 1;
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
            if (i == j)
                return;
        }

        boolean b = i == 0;
        int _fwd = crl[b ? 1 : 3];
        int[] fwd = new int[]{_fwd & 0xffff, _fwd >> 16};

        int _bwd = crl[b ? 3 : 1];
        int[] bwd = new int[]{_bwd & 0xffff, _bwd >> 16};

        int _fwdEmpty = crl[b ? 2 : 4];
        int[] fwdEmpty = new int[]{_fwdEmpty & 0xffff, _fwdEmpty >> 16};

        int _bwdEmpty = crl[b ? 4 : 2];
        int[] bwdEmpty = new int[]{_bwdEmpty & 0xffff, _bwdEmpty >> 16};

        int maxEmpty = maxEmpty(central);
        boolean flag = false;

        for (; i < j; i++) {
            if (b && i == 1) {
                int[] tmp = fwd;
                fwd = bwd;
                bwd = tmp;
                tmp = fwdEmpty;
                fwdEmpty = bwdEmpty;
                bwdEmpty = tmp;
            }
            int total = central;
            int open;
            if (fwdEmpty[0] <= maxEmpty && fwd[0] != 0) {
                total += fwd[0];
                open = checkOpen(total, fwdEmpty[0], fwdEmpty[1], bwdEmpty[0], fwd[1], bwd[0]);
                if (open != -1)
                    res.add(StoneShape.ofLength(total, open == 1));
            } else {
                if (flag) return;
                open = checkOpen(total, 0, fwdEmpty[0], bwdEmpty[0], fwd[0], bwd[0]);
                if (open != -1)
                    res.add(StoneShape.ofLength(total, open == 1));
                flag = true;
            }
        }
    }

    private static void sslFreestyle(int[] crl, List<StoneShape> res) {
        //TODO
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
