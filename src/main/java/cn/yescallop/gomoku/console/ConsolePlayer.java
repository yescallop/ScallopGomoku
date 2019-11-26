package cn.yescallop.gomoku.console;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.game.Game;
import cn.yescallop.gomoku.game.Move;
import cn.yescallop.gomoku.player.PlayerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Scallop Ye
 */
public class ConsolePlayer extends PlayerAdapter {

    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    private Game game;

    public ConsolePlayer(String name) {
        super(name);
    }

    private static String readLine() throws InterruptedException, IOException {
        while (true) { //TODO: Figure out why characters cannot be seen as soon as they are typed in CMD or PowerShell.
            if (READER.ready())
                return READER.readLine();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("[INTERRUPTED]");
                throw e;
            }
        }
    }

    @Override
    public void gameStarted(Game game) {
        this.game = game;
    }

    @Override
    public Move requestMove(Move.Attribute attr, long timeoutMillis) throws Exception {
        game.board().printTo(System.out);
        if (attr.isMultiple()) {
            System.out.printf("[%s] [%s] Please offer your move (%d/%d): ",
                    name, game.stoneTypeBySide(side),
                    attr.ordinal(), attr.total());
        } else {
            System.out.printf(attr.isOfDraw() ? "[%s] [%s] Please move (! for a draw): " : "[%s] [%s] Please move: ",
                    name, game.stoneTypeBySide(side));
        }
        String line = readLine();
        boolean draw = false;
        if (line.charAt(0) == '!') {
            if (line.length() == 1)
                return null;
            line = line.substring(1);
            draw = true;
        }
        Board.Point point;
        try {
            point = Board.Point.parse(line);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input mismatch");
        }
        return draw ? Move.ofDraw(point) : Move.of(point);
    }

    @Override
    public int requestChoice(ChoiceSet choiceSet, long timeoutMillis) throws Exception {
        System.out.println("----- CHOICE REQUEST -----");
        printChoices(choiceSet);
        System.out.println("--------------------------");
        System.out.printf("[%s] [%s] Please choose: ", name, game.stoneTypeBySide(side));

        int choice = Integer.parseInt(readLine());
        if (choiceSet.type() != ChoiceSet.Type.MOVE_COUNT)
            choice--;
        return choice;
    }

    private void printChoices(ChoiceSet choiceSet) {
        switch (choiceSet.type()) {
            case STRINGS:
                System.out.println("Make a choice from below:");
                String[] strings = choiceSet.strings();
                for (int i = 0; i < strings.length; i++) {
                    System.out.println(String.format("[%d] %s", i + 1, strings[i]));
                }
                break;
            case MOVES:
                System.out.println("Choose a move of the opponent from below:");
                Board.Grid[] moves = choiceSet.moves();
                for (int i = 0; i < moves.length; i++) {
                    System.out.print(String.format("[%d] %s ", i + 1, moves[i].pointString()));
                }
                System.out.println();
                break;
            case MOVE_COUNT:
                System.out.println("Declare the count of the fifth moves.");
                System.out.println("Maximum count: " + choiceSet.maxMoveCount());
                break;
        }
    }
}
