package cn.yescallop.gomoku.console;

import cn.yescallop.gomoku.event.GameListenerAdapter;
import cn.yescallop.gomoku.game.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        LOGGER.info("----- GAME SETTINGS ----");
        LOGGER.info("Game timeout: " + game.gameTimeout());
        LOGGER.info("Move timeout: " + game.moveTimeout());
        LOGGER.info("Rule: " + game.rule().name());
        LOGGER.info("----- GAME STARTED -----");
    }

    @Override
    public void stoneSwapped(Side side) {
        LOGGER.info((side == null ? "Judge" : game.playerNameBySide(side)) + " swapped the stones.");
    }

    @Override
    public void gameEnded(Result result) {
        Side side = result.winningSide();

        LOGGER.info("------ GAME ENDED ------");
        if (side != null)
            LOGGER.info("The winner: {} ({})", game.stoneTypeBySide(side), game.playerNameBySide(side));
        else LOGGER.info("The game ended in a draw.");
        LOGGER.info("Reason: " + result.type().message());
        result.description().ifPresent(d -> LOGGER.info("Description: " + d));
    }

    @Override
    public void exceptionCaught(Throwable t, Side side) {
        String name = game.playerNameBySide(side);
        StoneType stone = game.stoneTypeBySide(side);
        if (t instanceof IllegalMoveException) {
            LOGGER.warn("Invalid move by {} ({}): {}", stone, name, t.getMessage());
        } else if (t instanceof IllegalChoiceException) {
            LOGGER.warn("Invalid choice by {} ({}): {}", stone, name, t.getMessage());
        } else if (t instanceof ExecutionException) {
            t = t.getCause();
            LOGGER.warn(t.getClass().getSimpleName() + " occurred by {} ({}): {}", stone, name, t.getMessage());
        } else {
            LOGGER.error("Fatal error: ", t);
        }
    }
}
