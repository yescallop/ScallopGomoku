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
    public void moveMade(Move move, Side side) {
        String name = game.playerNameBySide(side);
        StoneType stone = game.stoneTypeBySide(side);
        if (move.attr().isOfDraw()) {
            LOGGER.info("{} ({}) moved and offered a draw: {}", stone, name, move.point());
        } else {
            LOGGER.info("{} ({}) moved: {}", stone, name, move.point());
        }
//        StoneShape[] shapes = GomokuUtil.searchShapes(move, stone)
//                .toArray(StoneShape[]::new);
//        System.out.println(Arrays.toString(shapes));
    }

    @Override
    public void playerPassed(Side side) {
        String name = game.playerNameBySide(side);
        StoneType stone = game.stoneTypeBySide(side);
        LOGGER.info("{} ({}) passed.", stone, name);
    }

    @Override
    public void gameEnded(Result result) {
        Side side = result.winningSide();

        game.board().printTo(System.out);
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

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static String timeoutString(OptionalLong t) {
        return t.isPresent() ? Duration.ofMillis(t.getAsLong()).toString() : "N/A";
    }
}
