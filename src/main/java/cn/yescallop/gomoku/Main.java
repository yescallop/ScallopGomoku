package cn.yescallop.gomoku;

import cn.yescallop.gomoku.console.ConsoleGameListener;
import cn.yescallop.gomoku.console.ConsolePlayer;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.StandardRules;

/**
 * @author Scallop Ye
 */
public class Main {

    public static void main(String[] args) {
        Game game = Game.newBuilder()
                .rule(StandardRules.STANDARD_RENJU)
                .strict(true)
                .player(Side.FIRST, new ConsolePlayer("Player 1"))
                .player(Side.SECOND, new ConsolePlayer("Player 2"))
                .listener(new ConsoleGameListener())
                .build();
        game.start();
    }
}
