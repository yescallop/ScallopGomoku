package cn.yescallop.gomoku;

import cn.yescallop.gomoku.console.ConsoleGameListener;
import cn.yescallop.gomoku.console.ConsolePlayer;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.StandardRules;

import java.util.concurrent.TimeUnit;

/**
 * @author Scallop Ye
 */
public class Main {

    public static void main(String[] args) {
        Game game = Game.newBuilder()
                .rule(StandardRules.NO_LIMIT)
                .moveTimeout(5, TimeUnit.SECONDS)
                .addPlayer(Side.FIRST, new ConsolePlayer("Player 1"))
                .addPlayer(Side.SECOND, new ConsolePlayer("Player 2"))
                .addListener(new ConsoleGameListener())
                .build();
        game.start();
    }
}
