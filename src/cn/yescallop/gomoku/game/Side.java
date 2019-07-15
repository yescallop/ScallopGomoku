package cn.yescallop.gomoku.game;

/**
 * @author Scallop Ye
 */
public enum Side {

    FIRST(0), SECOND(1);

    private final int index;

    Side(int index) {
        this.index = index;
    }

    public Side opposite() {
        return this == FIRST ? SECOND : FIRST;
    }

    public int index() {
        return index;
    }
}
