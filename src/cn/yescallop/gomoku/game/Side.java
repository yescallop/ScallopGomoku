package cn.yescallop.gomoku.game;

/**
 * @author Scallop Ye
 */
public enum Side {

    FIRST("First"), SECOND("Second");

    private final String name;

    Side(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Side opposite() {
        return this == FIRST ? SECOND : FIRST;
    }
}
