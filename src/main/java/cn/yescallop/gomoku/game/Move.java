package cn.yescallop.gomoku.game;

/**
 * @author Scallop Ye
 */
public class Move {

    private final Board.Point point;
    private final Attribute attr;

    private Move(Board.Point point, Attribute attr) {
        this.point = point;
        this.attr = attr;
    }

    public static Move of(Board.Point point) {
        return new Move(point, Attribute.NONE);
    }

    public static Move of(Board.Grid grid) {
        return of(grid.point());
    }

    public static Move of(Board.Point point, Attribute attr) {
        return new Move(point, attr);
    }

    public static Move of(Board.Grid grid, Attribute attr) {
        return of(grid.point(), attr);
    }

    public static Move ofPoint(int x, int y) {
        return new Move(new Board.Point(x, y), Attribute.NONE);
    }

    public static Move ofDraw(Board.Point point) {
        return new Move(point, Attribute.DRAW);
    }

    public static Move ofDraw(Board.Grid grid) {
        return ofDraw(grid.point());
    }

    public static Move ofMultiple(Board.Point point, int ordinal, int total) {
        return new Move(point, Attribute.ofMultiple(ordinal, total));
    }

    public static Move ofMultiple(Board.Grid grid, int ordinal, int total) {
        return ofMultiple(grid.point(), ordinal, total);
    }

    public Board.Point point() {
        return point;
    }

    public Attribute attr() {
        return attr;
    }

    public static class Attribute {

        public static Attribute NONE = new Attribute(1, 1, false);
        public static Attribute DRAW = new Attribute(1, 1, true);

        private final int ordinal;
        private final int total;
        private final boolean draw;

        private Attribute(int ordinal, int total, boolean draw) {
            this.ordinal = ordinal;
            this.total = total;
            this.draw = draw;
        }

        public static Attribute ofMultiple(int ordinal, int total) {
            if (ordinal < 1 || total < 1 || ordinal > total)
                throw new IllegalArgumentException("Illegal ordinal or total");
            return new Attribute(ordinal, total, false);
        }

        public boolean isMultiple() {
            return total != 1;
        }

        public int ordinal() {
            return ordinal;
        }

        public int total() {
            return total;
        }

        public boolean isOfDraw() {
            return draw;
        }
    }
}
