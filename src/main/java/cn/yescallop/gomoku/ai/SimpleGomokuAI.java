package cn.yescallop.gomoku.ai;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.player.PlayerAdapter;
import cn.yescallop.gomoku.rule.Rule;
import cn.yescallop.gomoku.rule.StandardRules;

/**
 * @author Scallop Ye
 */
public class SimpleGomokuAI extends PlayerAdapter {

    private Game game;
    private boolean freestyle;

    public SimpleGomokuAI() {
        super("Simple Gomoku AI");
    }

    @Override
    public void gameStarted(Game game) {
        Rule rule = game.rule();
        if (rule != StandardRules.STANDARD_GOMOKU && rule != StandardRules.FREESTYLE_GOMOKU) {
            throw new IllegalArgumentException("Not Standard or Free-style Gomoku");
        }
        this.game = game;
        this.freestyle = rule == StandardRules.FREESTYLE_GOMOKU;
    }

    @Override
    public Board.Point requestMove(long timeoutMillis) throws Exception {
        if (game.currentMoveIndex() == 0) {
            int c = game.board().size() / 2;
            return new Board.Point(c, c);
        }
        //TODO
        return null;
    }

    @Override
    public int requestChoice(ChoiceSet choiceSet, long timeoutMillis) throws Exception {
        return 0;
    }
}
