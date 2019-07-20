package cn.yescallop.gomoku.console;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.player.PlayerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

/**
 * @author Scallop Ye
 */
public class ConsolePlayer extends PlayerAdapter {

    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    public ConsolePlayer(String name) {
        super(name);
    }

    private static String readLine() throws InterruptedException, IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while (true) {
            if (READER.ready()) {
                c = READER.read();
                if (c == '\r' || c == '\n') {
                    if (sb.length() == 0)
                        continue;
                    return sb.toString();
                } else {
                    sb.append((char) c);
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("[INTERRUPTED]");
                throw e;
            }
        }
    }

    @Override
    public Board.Point requestMove(long timeoutMillis) throws Exception {
        System.out.printf("[%s] [%s] Please enter your move: ", name, stone);
        try {
            return Board.Point.parse(readLine());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input mismatch");
        }
    }

    @Override
    public int requestChoice(ChoiceSet choiceSet, long timeoutMillis) throws Exception {
        System.out.println("----- CHOICE REQUEST -----");
        printChoices(choiceSet);
        System.out.println("--------------------------");
        System.out.printf("[%s] [%s] Please enter your choice: ", name, stone);

        return Integer.parseInt(readLine()) - 1;
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
                Board.Point[] moves = choiceSet.moves();
                StringJoiner sj = new StringJoiner(" ");
                for (int i = 0; i < moves.length; i++) {
                    sj.add(String.format("[%d] %s", i + 1, moves[i]));
                }
                System.out.println(sj);
                break;
            case MOVE_COUNT:
                System.out.println("Declare the count of the fifth moves.");
                System.out.println("Maximum count: " + choiceSet.maxMoveCount());
                break;
        }
    }
}
