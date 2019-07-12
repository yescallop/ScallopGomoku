package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.handling.GlobalHandler;
import cn.yescallop.gomoku.handling.PlayerHandler;
import cn.yescallop.gomoku.rule.Rule;

/**
 * Declares a game.
 *
 * @author Scallop Ye
 */
public class Game {

    private final Board board;
    private final Rule rule;
    private final int timeLimitPerMove; //TODO
    private final RuleController ruleController = new RuleController();
    private final boolean hasPlayerHandler;

    private GlobalHandler globalHandler;

    private PlayerHandler firstPlayerHandler;
    private PlayerHandler secondPlayerHandler;

    private boolean started;
    private boolean ended;
    private boolean swapped = false;
    private boolean requestingForDraw = false;

    private Side currentSide = Side.BLACK;
    private Side sideAwaitingChoice = null;

    private Choice[] choices = null;

    private Game(Builder builder) {
        if (builder.rule == null)
            throw new NullPointerException("Rule");
        this.rule = builder.rule;
        this.timeLimitPerMove = builder.timeLimitPerMove;

        hasPlayerHandler = builder.firstPlayerHandler != null && builder.secondPlayerHandler != null;
        if (hasPlayerHandler) {
            this.firstPlayerHandler = builder.firstPlayerHandler;
            this.secondPlayerHandler = builder.secondPlayerHandler;
        } else if (builder.globalHandler == null) {
            throw new NullPointerException("Handler");
        }

        this.globalHandler = builder.globalHandler;

        board = new Board();
    }

    /**
     * Starts the game.
     */
    public void start() {
        if (started || ended)
            throw new IllegalStateException("Illegal start");
        started = true;

        rule.gameStarted(this, ruleController);

        if (hasPlayerHandler) {
            firstPlayerHandler.gameStarted(this, new PlayerController(true), Side.BLACK);
            secondPlayerHandler.gameStarted(this, new PlayerController(false), Side.WHITE);
            firstPlayerHandler.moveRequested(0);
        }

        if (globalHandler != null) {
            globalHandler.gameStarted(this, hasPlayerHandler ? null : new GlobalController());
            globalHandler.moveRequested(Side.BLACK);
        }
    }

    /**
     * Gets the current side of turn.
     *
     * @return the current side.
     */
    public Side currentSide() {
        return currentSide;
    }

    private PlayerHandler currentPlayer() {
        return player(currentSide);
    }

    private PlayerHandler player(Side side) {
        boolean black = side == Side.BLACK;
        return (black ^ swapped) ? firstPlayerHandler : secondPlayerHandler;
    }

    /**
     * Gets the board.
     *
     * @return the board of this game.
     */
    public Board board() {
        return board;
    }

    /**
     * Gets the rule type.
     *
     * @return the type of the rule.
     */
    public Rule.Type ruleType() {
        return rule.type();
    }

    /**
     * Gets the rule name.
     *
     * @return the name of the rule.
     */
    public String ruleName() {
        return rule.name();
    }

    /**
     * Gets the time limit.
     *
     * @return the time limit per move to this game.
     */
    public int timeLimitPerMove() {
        return timeLimitPerMove;
    }

    /**
     * Gets the current move index.
     */
    public int currentMoveIndex() {
        return board.currentMoveIndex();
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isEnded() {
        return ended;
    }

    public boolean isSwapped() {
        return swapped;
    }

    private void choiceMade(Choice choice, Side side) {
        if (hasPlayerHandler)
            player(side.opposite()).opponentChoiceMade(choice);

        if (globalHandler != null)
            globalHandler.choiceMade(choice, side);
    }

    public static class Builder {

        private Rule rule;
        private PlayerHandler firstPlayerHandler;
        private PlayerHandler secondPlayerHandler;
        private int timeLimitPerMove = -1;
        private GlobalHandler globalHandler;

        /**
         * Sets the rule of the game.
         *
         * @param rule a Rule instance standardizing the game.
         * @return this builder.
         */
        public Builder rule(Rule rule) {
            this.rule = rule;
            return this;
        }

        /**
         * Sets the handler of the first player.
         *
         * @param firstPlayerHandler the handler.
         * @return this builder.
         */
        public Builder firstPlayerHandler(PlayerHandler firstPlayerHandler) {
            this.firstPlayerHandler = firstPlayerHandler;
            return this;
        }

        /**
         * Sets the handler of the second player.
         *
         * @param secondPlayerHandler the handler.
         * @return this builder.
         */
        public Builder secondPlayerHandler(PlayerHandler secondPlayerHandler) {
            this.secondPlayerHandler = secondPlayerHandler;
            return this;
        }

        /**
         * Sets the global handler.
         *
         * @param globalHandler the handler.
         * @return this builder.
         */
        public Builder globalHandler(GlobalHandler globalHandler) {
            this.globalHandler = globalHandler;
            return this;
        }

        /**
         * Sets the time limit per move.
         *
         * @param secs time limit in seconds,
         *             negative value for no limit.
         * @return this builder.
         */
        public Builder timeLimitPerMove(int secs) {
            this.timeLimitPerMove = secs;
            return this;
        }

        /**
         * Builds the game.
         *
         * @return a Game.
         */
        public Game build() {
            return new Game(this);
        }
    }

    /**
     * A public global game controller
     */
    public class GlobalController {

        private GlobalController() {
        }

        /**
         * Makes a move.
         *
         * @param grid the grid where the move is made.
         * @throws RuleViolationException    if the move violates the rule.
         * @throws IllegalOperationException if the move is illegal.
         */
        public void makeMove(Board.Grid grid) throws RuleViolationException {
            if (sideAwaitingChoice != null)
                throw new IllegalOperationException("Moving while awaiting choice");

            rule.processMove(grid, currentSide);
        }

        /**
         * Makes a choice.
         *
         * @param i the choice index.
         * @throws IllegalOperationException if the choice is illegal.
         */
        public void makeChoice(int i) {
            if (sideAwaitingChoice == null)
                throw new IllegalOperationException("No choice is requested");

            if (i < 0 || i >= choices.length)
                throw new IndexOutOfBoundsException("Choice index");
            Choice choice = choices[i];

            choiceMade(choice, sideAwaitingChoice);
            rule.processChoice(choice, sideAwaitingChoice);

            sideAwaitingChoice = null;
            choices = null;
        }

        /**
         * Ends the game.
         *
         * @param result the result of the game.
         */
        public void end(Result result) {
            ruleController.end(result);
        }
    }

    /**
     * A public game controller class for players.
     */
    public class PlayerController {

        private final boolean first;

        private PlayerController(boolean first) {
            this.first = first;
        }

        /**
         * Returns whether the controller belongs to the first player.
         */
        public boolean isFirst() {
            return first;
        }

        private Side side() {
            return (first ^ swapped) ? Side.BLACK : Side.WHITE;
        }

        /**
         * Makes a move.
         *
         * @param grid the grid where the move is made.
         * @throws RuleViolationException    if the move violates the rule.
         * @throws IllegalOperationException if the move is illegal.
         */
        public void makeMove(Board.Grid grid) throws RuleViolationException {
            if (sideAwaitingChoice != null)
                throw new IllegalOperationException("Moving while awaiting choice");

            Side side = side();
            if (side != currentSide)
                throw new IllegalOperationException("Moving inopportunely");
            rule.processMove(grid, side);
        }

        /**
         * Makes a choice.
         *
         * @param i the choice index.
         * @throws IllegalOperationException if the choice is illegal.
         */
        public void makeChoice(int i) {
            if (sideAwaitingChoice == null)
                throw new IllegalOperationException("No choice is requested");

            Side side = side();
            if (sideAwaitingChoice != side)
                throw new IllegalOperationException("Making opponent's choice");

            if (i < 0 || i >= choices.length)
                throw new IndexOutOfBoundsException("Choice index");

            Choice choice = choices[i];
            choiceMade(choice, side);

            if (requestingForDraw) {
                switch (choice) {
                    case ACCEPT_DRAW:
                        ruleController.end(new Result(Result.Type.DRAW, null));
                        break;
                    case DECLINE_DRAW:
                        player(side).moveRequested(currentMoveIndex() + 1);
                        break;
                }
                requestingForDraw = false;
                sideAwaitingChoice = null;
                choices = null;
                return;
            }

            rule.processChoice(choice, side);

            sideAwaitingChoice = null;
            choices = null;
        }

        /**
         * Requests for a draw
         */
        public void requestForDraw() {
            Side side = side();
            if (side != currentSide)
                throw new IllegalOperationException("Requesting for draw inopportunely");

            ruleController.requestChoice(new Choice[]{Choice.ACCEPT_DRAW, Choice.DECLINE_DRAW}, side.opposite());
            requestingForDraw = true;
        }

        /**
         * Quits the game.
         */
        public void quit() {
            ruleController.end(new Result(Result.Type.QUIT, side().opposite()));
        }
    }

    /**
     * A public game controller class for rules.
     */
    public class RuleController {

        private RuleController() {
            // private access
        }

        /**
         * Swaps the black player and the white player.
         */
        public void swap() {
            swapped = !swapped;

            if (hasPlayerHandler) {
                firstPlayerHandler.sideSwapped();
                secondPlayerHandler.sideSwapped();
            }

            if (globalHandler != null)
                globalHandler.sideSwapped();
        }

        /**
         * Switch the turn to move.
         */
        public void switchSide() {
            currentSide = currentSide.opposite();
        }

        /**
         * Sets the current side.
         *
         * @param side the side.
         */
        public void setSide(Side side) {
            currentSide = side;
        }

        /**
         * Makes a move.
         *
         * @param grid the grid where the move is made.
         * @param side the side which requested the move.
         */
        public void makeMove(Board.Grid grid, Side side) {
            board.move(grid, side);

            if (hasPlayerHandler)
                player(side.opposite()).opponentMoveMade(grid);

            if (globalHandler != null)
                globalHandler.moveMade(grid);
        }

        /**
         * Requests the next move
         */
        public void requestNextMove() {
            if (hasPlayerHandler)
                currentPlayer().moveRequested(currentMoveIndex() + 1);

            if (globalHandler != null)
                globalHandler.moveRequested(currentSide);
        }

        /**
         * Requests a choice.
         *
         * @param choices the choices.
         * @param side    the side.
         */
        public void requestChoice(Choice[] choices, Side side) {
            if (sideAwaitingChoice != null)
                throw new IllegalOperationException("Duplicate choice request");

            sideAwaitingChoice = side;
            Game.this.choices = choices;

            if (hasPlayerHandler)
                player(side).choiceRequested(currentMoveIndex(), choices);

            if (globalHandler != null)
                globalHandler.choiceRequested(choices, side);
        }

        /**
         * Ends the game.
         *
         * @param result the result of the game.
         */
        public void end(Result result) {
            ended = true;
            started = false;

            if (hasPlayerHandler) {
                firstPlayerHandler.gameEnded(result);
                secondPlayerHandler.gameEnded(result);
            }

            if (globalHandler != null)
                globalHandler.gameEnded(result);
        }
    }
}
