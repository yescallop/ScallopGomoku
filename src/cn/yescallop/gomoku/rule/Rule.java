package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.*;

/**
 * A Rule processes every move of the game.
 *
 * @author Scallop Ye
 */
public abstract class Rule {

    protected Game game;
    protected Game.RuleController controller;

    public void gameStarted(Game game, Game.RuleController controller) {
        this.game = game;
        this.controller = controller;
    }

    /**
     * Processes a move made by the player.
     *
     * @param grid the grid where the move is made.
     * @param side the side of the move.
     * @throws RuleViolationException if the move violates the rule.
     */
    public abstract void processMove(Board.Grid grid, Side side) throws RuleViolationException;

    /**
     * Processes a choice made by the player.
     *
     * @param choice the choice made by the player.
     * @param side   the side of the choice.
     * @throws IllegalOperationException if the choice is illegal.
     */
    public abstract void processChoice(Choice choice, Side side);

    /**
     * Get the type of the rule.
     *
     * @return the type of the rule.
     */
    public abstract Type type();

    /**
     * Get the name of the rule.
     *
     * @return the name of the rule.
     */
    public abstract String name();

    public enum Type {
        NO_LIMIT,
        NONSTANDARD
    }
}
