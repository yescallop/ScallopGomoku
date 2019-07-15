package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.IllegalMoveException;
import cn.yescallop.gomoku.game.Side;

/**
 * A Rule processes every move of the game.
 *
 * @author Scallop Ye
 */
public abstract class Rule {

    protected Game game;
    protected Game.Controller controller;

    public void gameStarted(Game game, Game.Controller controller) {
        this.game = game;
        this.controller = controller;
    }

    /**
     * Processes a move made by the player.
     *
     * @param grid the grid where the move is made.
     * @param side the side of the move.
     * @throws IllegalMoveException if the move is illegal.
     */
    public abstract void processMove(Board.Grid grid, Side side) throws IllegalMoveException;

    /**
     * Processes a choice made by the player.
     *
     * @param choice the choice made by the player.
     * @param side   the side of the choice.
     */
    public abstract void processChoice(int choice, Side side);

    /**
     * Get the type of the rule.
     *
     * @return the type of the rule.
     */
    public abstract Type type();

    public enum Type {
        NO_LIMIT("No Limit");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
