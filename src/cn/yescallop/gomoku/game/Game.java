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
    private GlobalHandler globalHandler;
    private PlayerHandler firstPlayerHandler;
    private PlayerHandler secondPlayerHandler;
    private boolean started;
    private boolean ended;
    private boolean swapped = false;
    private Side currentSide = Side.BLACK;
    private Side sideAwaitingChoice = null;
    private boolean requestingForDraw = false;

    private Game(Builder builder) {
        this.rule = builder.rule;
        this.timeLimitPerMove = builder.timeLimitPerMove;

        if (builder.globalHandler == null) {
            this.firstPlayerHandler = builder.firstPlayerHandler;
            this.secondPlayerHandler = builder.secondPlayerHandler;
        } else {
            this.globalHandler = builder.globalHandler;
        }

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

        if (globalHandler == null) {
            firstPlayerHandler.gameStarted(this, new PlayerController(true), Side.BLACK);
            secondPlayerHandler.gameStarted(this, new PlayerController(false), Side.WHITE);

            firstPlayerHandler.moveRequested(0);
        } else {
            globalHandler.gameStarted(this, new GlobalController());
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
     * @return the type of the rule..
     */
    public Rule.Type ruleType() {
        return rule.type();
    }

    /**
     * Gets the rule name.
     *
     * @return the name of the rule..
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
            if (rule == null)
                throw new NullPointerException("Rule");

            if (globalHandler == null) {
                if (firstPlayerHandler == null || secondPlayerHandler == null)
                    throw new NullPointerException("Handler");
            }
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
         * @param choice the choice.
         * @throws IllegalOperationException if the choice is illegal.
         */
        public void makeChoice(Choice choice) {
            if (sideAwaitingChoice == null)
                throw new IllegalOperationException("No choice is requested");

            rule.processChoice(choice, sideAwaitingChoice);
            sideAwaitingChoice = null;
        }

        /**
         * Ends the game.
         *
         * @param result the result of the game.
         */
        public void end(Result result) {
            ended = true;
            started = false;
            globalHandler.gameEnded(result);
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
         * @param choice the choice.
         * @throws IllegalOperationException if the choice is illegal.
         */
        public void makeChoice(Choice choice) {
            if (sideAwaitingChoice == null)
                throw new IllegalOperationException("No choice is requested");

            Side side = side();
            if (sideAwaitingChoice != side)
                throw new IllegalOperationException("Making opponent's choice");
            PlayerHandler opponent = player(side.opposite());

            if (requestingForDraw) {
                switch (choice) {
                    case ACCEPT_DRAW:
                        ruleController.end(new Result(Result.Type.DRAW, null));
                        break;
                    case DECLINE_DRAW:
                        opponent.opponentChoiceMade(choice);
                        break;
                    default:
                        throw new IllegalOperationException("No such choice");
                }
                requestingForDraw = false;
                return;
            }

            rule.processChoice(choice, side);
            opponent.opponentChoiceMade(choice);

            sideAwaitingChoice = null;
        }

        /**
         * Requests for a draw
         */
        public void requestForDraw() {
            Side side = side();
            if (side != currentSide)
                throw new IllegalOperationException("Requesting for draw inopportunely");

            requestingForDraw = true;
            ruleController.requestChoice(new Choice[]{Choice.ACCEPT_DRAW, Choice.DECLINE_DRAW}, side.opposite());
        }

        /**
         * Quits the game.
         */
        public void quit() {
            ended = true;
            started = false;

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

            if (globalHandler == null) {
                firstPlayerHandler.sideSwapped();
                secondPlayerHandler.sideSwapped();
            } else {
                globalHandler.sideSwapped();
            }
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

            if (globalHandler == null) {
                player(side.opposite()).opponentMoveMade(grid);
            } else {
                globalHandler.moveMade(grid);
            }
        }

        /**
         * Requests the next move
         */
        public void requestNextMove() {
            if (globalHandler == null) {
                currentPlayer().moveRequested(currentMoveIndex() + 1);
            } else {
                globalHandler.moveRequested(currentSide);
            }
        }

        /**
         * Requests a choice.
         *
         * @param choices the choices.
         * @param side    the side.
         */
        public void requestChoice(Choice[] choices, Side side) {
            sideAwaitingChoice = side;

            if (globalHandler == null) {
                player(side).choiceRequested(currentMoveIndex(), choices);
            } else {
                globalHandler.choiceRequested(choices, side);
            }
        }

        /**
         * Ends the game.
         *
         * @param result the result of the game.
         */
        public void end(Result result) {
            ended = true;
            started = false;

            if (globalHandler == null) {
                firstPlayerHandler.gameEnded(result);
                secondPlayerHandler.gameEnded(result);
            } else {
                globalHandler.gameEnded(result);
            }
        }
    }
}
