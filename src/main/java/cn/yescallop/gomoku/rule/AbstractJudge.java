package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.game.Game;

/**
 * A simple abstract implementation of Judge.
 *
 * @author Scallop Ye
 */
public abstract class AbstractJudge implements Judge {

    protected Game game;
    protected Game.Controller controller;

    @Override
    public void gameStarted(Game game, Game.Controller controller) {
        this.game = game;
        this.controller = controller;
    }
}
