package cn.yescallop.gomoku;

import cn.yescallop.gomoku.ai.SimpleGomokuAI;
import cn.yescallop.gomoku.console.ConsoleGameListener;
import cn.yescallop.gomoku.console.ConsolePlayer;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.Side;
import cn.yescallop.gomoku.rule.StandardRules;

/**
 * @author Scallop Ye
 */
public class MainTest {

    public static void main(String[] args) {
        Game game = Game.newBuilder()
                .rule(StandardRules.FREESTYLE_GOMOKU)
                .strict(true)
                .player(Side.FIRST, new ConsolePlayer("Human"))
                .player(Side.SECOND, new SimpleGomokuAI())
                .listener(new ConsoleGameListener())
                .build();
        game.start().thenAccept(g -> System.out.println(g.result()));
    }
}
