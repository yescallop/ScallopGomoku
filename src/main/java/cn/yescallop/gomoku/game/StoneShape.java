package cn.yescallop.gomoku.game;

/**
 * @author Scallop Ye
 */
public enum StoneShape {
    OVERLINE,
    BROKEN_OVERLINE,
    FIVE,
    FOUR,
    OPEN_FOUR,
    SEMI_OPEN_FOUR,
    THREE,
    OPEN_THREE,
    SEMI_OPEN_THREE,
    TWO,
    OPEN_TWO,
    SEMI_OPEN_TWO;

    public static StoneShape ofLength(int len, boolean open) {
        switch (len) {
            case 2:
                return open ? OPEN_TWO : SEMI_OPEN_TWO;
            case 3:
                return open ? OPEN_THREE : SEMI_OPEN_THREE;
            case 4:
                return open ? OPEN_FOUR : SEMI_OPEN_FOUR;
        }
        return null;
    }
}
