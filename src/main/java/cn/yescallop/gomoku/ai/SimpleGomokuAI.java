package cn.yescallop.gomoku.ai;

import cn.yescallop.gomoku.game.*;
import cn.yescallop.gomoku.player.PlayerAdapter;
import cn.yescallop.gomoku.rule.Rule;
import cn.yescallop.gomoku.rule.StandardRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cn.yescallop.gomoku.game.StoneShape.FIVE;
import static cn.yescallop.gomoku.game.StoneShape.OVERLINE;

/**
 * @author Scallop Ye
 */
public class SimpleGomokuAI extends PlayerAdapter {

    private Game game;
    private Board board;
    private int boardSize;
    private boolean freestyle;
    private Random random;

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
        board = game.board();
        boardSize = board.size();
        freestyle = rule == StandardRules.FREESTYLE_GOMOKU;
        random = new Random();
    }

    @Override
    public Move requestMove(Move.Attribute attr, long timeoutMillis) {
        if (game.currentMoveIndex() == 0) {
            int c = boardSize / 2;
            return Move.ofPoint(c, c);
        }
        StoneType stone = game.stoneTypeBySide(side);
        List<Board.Grid> moves = new ArrayList<>();
        int maxScore = 0;
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                Board.Grid grid = board.getGrid(x, y);
                if (grid.isEmpty()) {
                    List<StoneShape> s = GomokuUtil.searchShapes(grid, stone);
                    if (s.contains(FIVE) || (freestyle && s.contains(OVERLINE))) {
                        return Move.of(grid);
                    }
                    List<StoneShape> so = GomokuUtil.searchShapes(grid, stone.opposite());
                    if (so.contains(FIVE) || (freestyle && so.contains(OVERLINE))) {
                        return Move.of(grid);
                    }
                    int score1 = GomokuUtil.evaluate(s, freestyle);
                    int score2 = GomokuUtil.evaluate(so, freestyle);
                    int score = score1 + score2;
                    if (score > maxScore) {
                        moves.clear();
                        moves.add(grid);
                        maxScore = score;
//                        System.out.println(String.format("%s: %d/%d", grid.pointString(), score1, score2));
                    } else if (score == maxScore) {
                        moves.add(grid);
                    }
                }
            }
        }
        boolean draw = false;
        if (maxScore == 0) {
            if (attr.isOfDraw())
                return null;
            draw = true;
        }
        Board.Grid grid = moves.get(random.nextInt(moves.size()));
        return draw ? Move.ofDraw(grid) : Move.of(grid);
    }

    @Override
    public int requestChoice(ChoiceSet choiceSet, long timeoutMillis) throws Exception {
        return 0;
    }
}
