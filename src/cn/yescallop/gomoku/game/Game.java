package cn.yescallop.gomoku.game;

/**
 * @author Scallop Ye
 */
public class Game {

    private final Board board;
    private final Rule rule;
    private final int timeLimitPerMove; //TODO
    private final Player firstPlayer;
    private final Player secondPlayer;
    private boolean started;
    private boolean ended;
    private boolean swapped = false;
    private Side currentSide = Side.BLACK;

    private MoveRequestTask moveRequestTask;

    private Game(Builder builder) {
        this.rule = builder.rule;
        this.timeLimitPerMove = builder.timeLimitPerMove;

        this.firstPlayer = builder.firstPlayer;
        this.secondPlayer = builder.secondPlayer;

        board = new Board();
    }

    /**
     * Starts the game.
     */
    public void start() {
        if (started || ended)
            throw new IllegalStateException("Illegal start");

        rule.gameStarted(this);
        firstPlayer.gameStarted(this, Side.BLACK);
        secondPlayer.gameStarted(this, Side.WHITE);

        started = true;

        moveRequestTask = new MoveRequestTask(this);
        moveRequestTask.start();
    }

    /**
     * Swaps the black player and the white player.
     */
    void swap() {
        firstPlayer.sideSwapped();
        secondPlayer.sideSwapped();

        swapped = true;
    }

    /**
     * Switch the turn to move.
     */
    void switchTurn() {
        currentSide = currentSide.opposite();
    }

    /**
     * Makes a move.
     *
     * @param grid The grid where the move is made
     * @param side The side which requested the move
     * @return Whether the move is applied
     */
    boolean move(Board.Grid grid, Side side) {
        //TODO
        return board.move(grid, side);
    }

    /**
     * Ends the game.
     *
     * @param result The result of the game
     */
    void end(Result result) {
        moveRequestTask.interrupt();

        firstPlayer.resultReceived(result);
        secondPlayer.resultReceived(result);

        ended = true;
        started = false;
    }

    /**
     * Gets the current side of turn.
     *
     * @return The current side
     */
    public Side currentSide() {
        return currentSide;
    }

    Player player(Side side) {
        boolean first = side == Side.BLACK;
        if (swapped) first = !first;
        return first ? firstPlayer : secondPlayer;
    }

    /**
     * Gets the board.
     *
     * @return The board of this game
     */
    public Board board() {
        return board;
    }

    /**
     * Gets the rule.
     *
     * @return The rule of this game
     */
    public Rule rule() {
        return rule;
    }

    /**
     * Gets the time limit.
     *
     * @return The time limit per move to this game
     */
    public int timeLimitPerMove() {
        return timeLimitPerMove;
    }

    public static class Builder {

        private Rule rule;
        private Player firstPlayer;
        private Player secondPlayer;
        private int timeLimitPerMove = -1;

        /**
         * Sets the rule of the game.
         *
         * @param rule A <code>Rule</code> instance standardizing the game
         * @return This builder
         */
        public Builder rule(Rule rule) {
            this.rule = rule;
            return this;
        }

        /**
         * Sets the first player.
         *
         * @param firstPlayer A player
         * @return This builder
         */
        public Builder firstPlayer(Player firstPlayer) {
            this.firstPlayer = firstPlayer;
            return this;
        }

        /**
         * Sets the second player.
         *
         * @param secondPlayer A player
         * @return This builder
         */
        public Builder secondPlayer(Player secondPlayer) {
            this.secondPlayer = secondPlayer;
            return this;
        }

        /**
         * Sets the time limit per move.
         *
         * @param secs Time limit in seconds,
         *             negative value for no limit.
         * @return This builder
         */
        public Builder timeLimitPerMove(int secs) {
            this.timeLimitPerMove = secs;
            return this;
        }

        /**
         * Builds the game.
         *
         * @return A <code>Game</code>
         */
        public Game build() {
            if (rule == null || firstPlayer == null || secondPlayer == null)
                throw new NullPointerException();
            return new Game(this);
        }
    }
}
