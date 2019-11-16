package cn.yescallop.gomoku.game;

import cn.yescallop.gomoku.player.Player;
import cn.yescallop.gomoku.rule.Judge;
import cn.yescallop.gomoku.rule.Rule;
import cn.yescallop.gomoku.rule.RuleHelper;

import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of Game.
 *
 * @author Scallop Ye
 */
class GameImpl implements Game {

    private final Board board;
    private final Rule rule;
    private final Player[] players;
    private final boolean strict;

    final long gameTimeout;
    final long moveTimeout;

    final Judge judge;
    final Controller controller = new ControllerImpl();
    final ListenerGroup listenerGroup;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "GameThread"));
    private GameTask gameTask;

    private boolean started;
    private boolean ended;
    private boolean swapped = false;

    private Side sideAwaitingChoice = null;
    private ChoiceSet choiceSet = null;

    private StoneType currentStoneType = StoneType.BLACK;

    private Result result = null;

    GameImpl(GameBuilderImpl builder) {
        this.rule = builder.rule;
        this.judge = rule.newJudge();

        this.players = builder.players;
        // Sets the sides of the players
        players[0].setSide(Side.FIRST);
        players[1].setSide(Side.SECOND);

        this.gameTimeout = builder.gameTimeout;
        this.moveTimeout = builder.moveTimeout;
        this.strict = builder.strict;

        this.listenerGroup = builder.listenerGroup;
        // Adds players to the listener group
        listenerGroup.add(players[0]);
        listenerGroup.add(players[1]);

        board = new Board(15);
    }

    @Override
    public CompletableFuture<Game> start() {
        if (started || ended)
            throw new IllegalStateException("Illegal start");
        started = true;

        judge.gameStarted(this, controller);

        listenerGroup.gameStarted(this);

        gameTask = new GameTask(this);

        return CompletableFuture.runAsync(gameTask, executor)
                .thenApply((Void) -> this);
    }

    @Override
    public Side currentSide() {
        return sideByStoneType(currentStoneType);
    }

    @Override
    public StoneType currentStoneType() {
        return currentStoneType;
    }

    @Override
    public StoneType stoneTypeBySide(Side side) {
        boolean first = side == Side.FIRST;
        return (first ^ swapped) ? StoneType.BLACK : StoneType.WHITE;
    }

    @Override
    public Side sideByStoneType(StoneType stone) {
        boolean black = stone == StoneType.BLACK;
        return (black ^ swapped) ? Side.FIRST : Side.SECOND;
    }

    @Override
    public Result result() {
        return result;
    }

    @Override
    public Board board() {
        return board;
    }

    @Override
    public boolean reportForbiddenMove(Board.Point point) {
        if (!started || !rule.isRenjuRule() || strict)
            return false;
        Board.Grid grid = board.getGrid(point);
        if (grid.stone() != StoneType.BLACK)
            return false;
        if (grid.moveIndex() == board.currentMoveIndex()) {
            List<StoneShape> shapes = RuleHelper.searchShapes(grid);
            String description = RuleHelper.describeForbiddenMove(shapes);
            if (description != null) {
                controller.end(Result.Type.FORBIDDEN_MOVE_MADE, sideByStoneType(StoneType.WHITE), description);
                return true;
            }
        } else if (RuleHelper.longestRowLen(grid) > 5) {
            controller.end(Result.Type.FORBIDDEN_MOVE_MADE, sideByStoneType(StoneType.WHITE), "Overline");
            return true;
        }
        return false;
    }

    @Override
    public OptionalLong gameTimeoutMillis() {
        return gameTimeout == 0 ? OptionalLong.empty() : OptionalLong.of(gameTimeout);
    }

    @Override
    public OptionalLong moveTimeoutMillis() {
        return moveTimeout == 0 ? OptionalLong.empty() : OptionalLong.of(moveTimeout);
    }

    @Override
    public OptionalLong gameTimeRemainingMillis(Side side) {
        return gameTask.gameTimeRemaining == null ?
                OptionalLong.empty() : OptionalLong.of(gameTask.gameTimeRemaining[side.ordinal()]);
    }

    @Override
    public boolean isStrict() {
        return strict;
    }

    @Override
    public int currentMoveIndex() {
        return board.currentMoveIndex();
    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public String playerNameBySide(Side side) {
        return player(side).name();
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isEnded() {
        return ended;
    }

    @Override
    public boolean isSwapped() {
        return swapped;
    }

    // Package-private methods

    Player player(Side side) {
        return players[side.ordinal()];
    }

    Side sideAwaitingChoice() {
        return sideAwaitingChoice;
    }

    ChoiceSet choiceSet() {
        return choiceSet;
    }

    void resetChoice() {
        sideAwaitingChoice = null;
        choiceSet = null;
    }

    // Non-static inner class implementations

    /**
     * Implementation of Game.Controller.
     */
    private class ControllerImpl implements Game.Controller {

        private ControllerImpl() {
            // private access
        }

        @Override
        public void swap() {
            swapped = !swapped;
            listenerGroup.stoneSwapped();
        }

        @Override
        public void makeMove(Board.Grid grid) {
            board.move(grid, currentStoneType);
            listenerGroup.moveMade(grid, currentSide());
            currentStoneType = currentStoneType.opposite();
        }

        @Override
        public void requestMultipleMoves(int count) {
            if (count < 2)
                throw new IllegalArgumentException("count < 2");

            gameTask.multipleMovesRequested(count);
            listenerGroup.multipleMovesRequested(count, currentSide());
        }

        @Override
        public void requestChoice(ChoiceSet choiceSet, Side side) {
            sideAwaitingChoice = side;
            GameImpl.this.choiceSet = choiceSet;
        }

        @Override
        public void end(Result.Type resultType, Side winningSide) {
            end(resultType, winningSide, null);
        }

        @Override
        public void end(Result.Type resultType, Side winningSide, String description) {
            ended = true;
            started = false;
            currentStoneType = null;
            sideAwaitingChoice = null;
            choiceSet = null;
            result = new Result(resultType, winningSide, description);
            listenerGroup.gameEnded(result);
            gameTask.executor.shutdown();
            executor.shutdown();
        }
    }
}
