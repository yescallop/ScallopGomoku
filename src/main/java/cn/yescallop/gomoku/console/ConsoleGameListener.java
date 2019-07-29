package cn.yescallop.gomoku.console;

import cn.yescallop.gomoku.event.GameListenerAdapter;
import cn.yescallop.gomoku.game.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.OptionalLong;
import java.util.concurrent.ExecutionException;

/**
 * @author Scallop Ye
 */
public class ConsoleGameListener extends GameListenerAdapter {

    private static final Logger LOGGER = LogManager.getLogger("Supervisor");

    private Game game;

    @Override
    public void gameStarted(Game game) {
        this.game = game;
        LOGGER.info("----- GAME SETTINGS -----");
        LOGGER.info("Game timeout: " + timeoutString(game.gameTimeoutMillis()));
        LOGGER.info("Move timeout: " + timeoutString(game.moveTimeoutMillis()));
        LOGGER.info("Strict mode: " + (game.isStrict() ? "Enabled" : "Disabled"));
        LOGGER.info("Rule: " + game.rule().name());
        LOGGER.info("----- GAME STARTED -----");
    }

    @Override
    public void stoneSwapped() {
        LOGGER.info("The stones are swapped.");
    }

    @Override
    public void moveMade(Board.Grid move, Side side) {
        String name = game.playerNameBySide(side);
        StoneType stone = game.stoneTypeBySide(side);
        LOGGER.info("{} ({}) made a move: {}", stone, name, move.pointString());
    }

    @Override
    public void moveRequested(Side side) {
        printBoard();
    }

    @Override
    public void gameEnded(Result result) {
        Side side = result.winningSide();

        printBoard();
        LOGGER.info("----- GAME ENDED -----");
        if (side != null)
            LOGGER.info("The winner: {} ({})", game.stoneTypeBySide(side), game.playerNameBySide(side));
        else LOGGER.info("The game ended in a draw.");
        LOGGER.info("Reason: " + result.type().message());
        result.description().ifPresent(d -> LOGGER.info("Description: " + d));
    }

    @Override
    public void exceptionCaught(Throwable t, Side side) {
        if (side != null) {
            String name = game.playerNameBySide(side);
            StoneType stone = game.stoneTypeBySide(side);
            if (t instanceof IllegalMoveException) {
                LOGGER.warn("Invalid move by {} ({}): {}", stone, name, t.getMessage());
            } else if (t instanceof IllegalChoiceException) {
                LOGGER.warn("Invalid choice by {} ({})", stone, name);
            } else if (t instanceof ExecutionException) {
                t = t.getCause();
                LOGGER.warn("{} occurred by {} ({}): {}", t.getClass().getSimpleName(), stone, name, t.getMessage());
            }
        } else {
            LOGGER.error("Fatal error: ", t);
        }
    }

    private void printBoard() {
        Board board = game.board();
        int size = board.size();
        for (int y = size - 1; y >= 0; y--) {
            if (y < 9) System.out.print(' ');
            System.out.print(y + 1);
            for (int x = 0; x < size; x++) {
                StoneType stone = board.getGrid(x, y).stone();
                System.out.print(stone == null ? "  -" : (stone == StoneType.BLACK ? "  X" : "  0"));
            }
            System.out.println();
        }
        System.out.print("  ");
        for (int i = 0; i < size; i++) {
            System.out.print("  ");
            System.out.print((char) ('A' + i));
        }
        System.out.println();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static String timeoutString(OptionalLong t) {
        return t.isPresent() ? Duration.ofMillis(t.getAsLong()).toString() : "N/A";
    }
}
