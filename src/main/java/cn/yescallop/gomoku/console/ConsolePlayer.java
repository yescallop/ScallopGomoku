package cn.yescallop.gomoku.console;

import cn.yescallop.gomoku.game.Board;
import cn.yescallop.gomoku.game.ChoiceSet;
import cn.yescallop.gomoku.player.PlayerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        while (!Thread.interrupted()) {
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
        }
        System.out.println("[INTERRUPTED]");
        throw new InterruptedException();
    }

    @Override
    public Board.Point requestMove(long timeoutMillis) throws Exception {
        System.out.printf("[%s] [%s] It's your turn to move", name, stone);
        System.out.println();
        System.out.print("Please enter your move: ");
        try {
            return Board.Point.parse(readLine());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input mismatch");
        }
    }

    @Override
    public int requestChoice(ChoiceSet choiceSet, long timeoutMillis) throws Exception {
        return Integer.parseInt(readLine());
    }
}
