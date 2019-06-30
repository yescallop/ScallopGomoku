package cn.yescallop.gomoku.game;

/**
 * A <code>Rule</code> processes every move of the game.
 *
 * @author Scallop Ye
 */
public abstract class Rule {

    protected Game game;

    void gameStarted(Game game) {
        this.game = game;
    }

    public abstract Result process(Board.Grid grid, Side side);

    public abstract String name();

    protected boolean move(Board.Grid grid, Side side) {
        return game.move(grid, side);
    }

    protected void swap() {
        game.swap();
    }

    protected void switchTurn() {
        game.switchTurn();
    }

}
