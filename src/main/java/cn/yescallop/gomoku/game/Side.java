package cn.yescallop.gomoku.game;

/**
 * @author Scallop Ye
 */
public enum Side {

    FIRST, SECOND;

    public Side opposite() {
        return this == FIRST ? SECOND : FIRST;
    }
}
