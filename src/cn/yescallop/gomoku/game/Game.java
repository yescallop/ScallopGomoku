package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.event.GameListener;
import cn.yescallop.gomoku.rule.Rule;

/**
 * Declares a game.
 *
 * @author Scallop Ye
 */
public class Game {

    private final Board board;
    private final Rule rule;

    private final int timeLimitToGame; //TODO
    private final int timeLimitToMove; //TODO

    private final RuleController ruleController = new RuleController();
    private final PlayerController firstPlayerController = new PlayerController(Side.FIRST);
    private final PlayerController secondPlayerController = new PlayerController(Side.SECOND);
    private final GlobalController globalController = new GlobalController();

    private final ListenerGroup listenerGroup;

    private boolean started;
    private boolean ended;
    private boolean swapped = false;

    private Side currentSide = Side.FIRST;
    private Side sideAwaitingChoice = null;

    private Choice[] choices = null;

    private Game(Builder builder) {
        if (builder.rule == null)
            throw new NullPointerException("Rule");
        this.rule = builder.rule;
        this.timeLimitToGame = builder.timeLimitToGame;
        this.timeLimitToMove = builder.timeLimitToMove;
        this.listenerGroup = builder.listenerGroup;

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

        listenerGroup.gameStarted(board, rule.type());
        listenerGroup.moveRequested(Side.FIRST);
    }

    /**
     * Gets the current side of turn.
     *
     * @return the current side.
     */
    public Side currentSide() {
        return currentSide;
    }

    /**
     * Gets the current stone type of turn.
     *
     * @return the current stone type.
     */
    public StoneType currentStoneType() {
        boolean first = currentSide == Side.FIRST;
        return (first ^ swapped) ? StoneType.BLACK : StoneType.WHITE;
    }

    /**
     * Gets the specified player controller.
     *
     * @param side the side.
     * @return the specified player controller.
     */
    public PlayerController playerController(Side side) {
        return side == Side.FIRST ? firstPlayerController : secondPlayerController;
    }

    /**
     * Gets the global controller.
     *
     * @return the global controller.
     */
    public GlobalController globalController() {
        return globalController;
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
     * Gets the time limit to a game.
     *
     * @return the time limit to a game.
     */
    public int timeLimitToGame() {
        return timeLimitToGame;
    }

    /**
     * Gets the time limit to a move.
     *
     * @return the time limit to a move.
     */
    public int timeLimitToMove() {
        return timeLimitToMove;
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
        private ListenerGroup listenerGroup = new ListenerGroup();
        private int timeLimitToGame = -1;
        private int timeLimitToMove = -1;

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
         * Adds a game listener to the listener group.
         * The first added will be the first called.
         *
         * @param listener the listener to be added.
         * @return this builder.
         */
        public Builder addListener(GameListener listener) {
            listenerGroup.add(listener);
            return this;
        }

        /**
         * Sets the time limit to the game.
         *
         * @param secs time limit in seconds,
         *             zero for no limit.
         * @return this builder.
         */
        public Builder timeLimitToGame(int secs) {
            if (secs < 0)
                throw new IllegalArgumentException("Negative time limit");
            this.timeLimitToGame = secs;
            return this;
        }

        /**
         * Sets the time limit to a move.
         *
         * @param secs time limit in seconds,
         *             zero for no limit.
         * @return this builder.
         */
        public Builder timeLimitToMove(int secs) {
            if (secs < 0)
                throw new IllegalArgumentException("Negative time limit");
            this.timeLimitToMove = secs;
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

            listenerGroup.choiceMade(choice, sideAwaitingChoice);
            rule.processChoice(choice, sideAwaitingChoice);

            sideAwaitingChoice = null;
            choices = null;
        }

        /**
         * Ends the game.
         *
         * @param result      the result of the game.
         * @param winningSide the winning side.
         */
        public void end(Result result, Side winningSide) {
            ruleController.end(result, winningSide);
        }
    }

    /**
     * A public game controller class for players.
     */
    public class PlayerController {

        private final Side side;

        private PlayerController(Side side) {
            this.side = side;
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

            if (sideAwaitingChoice != side)
                throw new IllegalOperationException("Making opponent's choice");

            if (i < 0 || i >= choices.length)
                throw new IndexOutOfBoundsException("Choice index");

            Choice choice = choices[i];
            listenerGroup.choiceMade(choice, sideAwaitingChoice);

            switch (choice) {
                case ACCEPT_DRAW:
                    ruleController.end(Result.DRAW, null);
                    break;
                case DECLINE_DRAW:
                    listenerGroup.moveRequested(side);
                    break;
                default:
                    rule.processChoice(choice, side);
            }

            sideAwaitingChoice = null;
            choices = null;
        }

        /**
         * Requests for a draw
         */
        public void requestForDraw() {
            if (side != currentSide)
                throw new IllegalOperationException("Requesting for draw inopportunely");

            ruleController.requestChoice(new Choice[]{Choice.ACCEPT_DRAW, Choice.DECLINE_DRAW}, side.opposite());
        }

        /**
         * Quits the game.
         */
        public void quit() {
            ruleController.end(Result.QUIT, side.opposite());
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
            listenerGroup.stoneSwapped();
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
         */
        public void makeMove(Board.Grid grid) {
            board.move(grid, currentStoneType());

            listenerGroup.moveMade(grid, currentSide);
        }

        /**
         * Requests the next move
         */
        public void requestNextMove() {
            listenerGroup.moveRequested(currentSide);
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

            listenerGroup.choiceRequested(choices, side);
        }

        /**
         * Ends the game.
         *
         * @param result      the result of the game.
         * @param winningSide the winning side.
         */
        public void end(Result result, Side winningSide) {
            ended = true;
            started = false;
            currentSide = null;
            sideAwaitingChoice = null;
            choices = null;
            listenerGroup.gameEnded(result, winningSide);
        }
    }
}
