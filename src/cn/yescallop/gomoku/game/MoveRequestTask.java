package cn.yescallop.gomoku.game;

/**
 * A <code>MoveRequestTask</code> repeatedly requests for moves.
 *
 * @author Scallop Ye
 */
public class MoveRequestTask extends Thread {

    private final Game game;
    private final Rule rule;

    MoveRequestTask(Game game) {
        this.game = game;
        this.rule = game.rule();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Side curSide = game.currentSide();
                Player curPlayer = game.player(curSide);
                Player idlePlayer = game.player(curSide.opposite());

                Board.Grid grid = curPlayer.requestMove();

                Result res = rule.process(grid, curSide);
                switch (res.type()) {
                    case SUCCESS:
                        idlePlayer.moveMade(grid);
                        break;
                    case INVALID:
                        curPlayer.resultReceived(res);
                        break;
                    default:
                        game.end(res);
                        return;
                }
            }
        } catch (InterruptedException e) {
            //ignored
        }
    }
}
