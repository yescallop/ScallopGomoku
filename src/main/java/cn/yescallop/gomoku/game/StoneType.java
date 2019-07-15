package cn.yescallop.gomoku.game;

/**
 * @author Scallop Ye
 */
public enum StoneType {

    BLACK("Black"), WHITE("White");

    private final String name;

    StoneType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public StoneType opposite() {
        return this == BLACK ? WHITE : BLACK;
    }
}
