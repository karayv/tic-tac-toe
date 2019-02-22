package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class TicTacToe {

    private static final int[][] winMasks = { //
            { 0b111111_000000_000000, 0b010101_000000_000000, 0b101010_000000_000000 }, //
            { 0b000000_111111_000000, 0b000000_010101_000000, 0b000000_101010_000000 }, //
            { 0b000000_000000_111111, 0b000000_000000_010101, 0b000000_000000_101010 }, //

            { 0b110000_110000_110000, 0b010000_010000_010000, 0b100000_100000_100000 }, //
            { 0b001100_001100_001100, 0b000100_000100_000100, 0b001000_001000_001000 }, //
            { 0b000011_000011_000011, 0b000001_000001_000001, 0b000010_000010_000010 }, //

            { 0b110000_001100_000011, 0b010000_000100_000001, 0b100000_001000_000010 }, //
            { 0b000011_001100_110000, 0b000001_000100_010000, 0b000010_001000_100000 } //
    };
    private static String[] tokens = { "-", "X", "O" };

    int board = 0;
    private int movesLeft = 9;
    private int[] aiCache = new int[0b1_000000_000000_000000];

    int winningMove(int board, boolean isCross) {
        if (wins(isCross)) {
            return -1;
        }
        for (int pos = 1; pos <= 9; pos++) {
            int updatedBoard = addToken(board, pos, isCross);
            if (updatedBoard != board && wins(updatedBoard, isCross)) {
                return pos;
            }
        }
        return -1;
    }

    public int aiMove(boolean isCross) {
        if (aiCache[board] != 0) {
            return aiCache[board];
        }
        aiMove(board, isCross, movesLeft);
        return aiCache[board];
    }

    private int aiMove(int board, boolean isCross, int movesLeft) {
        if (movesLeft == 0) {
            return 0;
        }

        int offensiveMove = winningMove(board, isCross);
        if (offensiveMove != -1) {
            aiCache[board] = offensiveMove;
            return 1;
        }

        int defensiveMove = winningMove(board, !isCross);
        if (defensiveMove != -1) {
            aiCache[board] = defensiveMove;
            int updatedBoard = addToken(board, defensiveMove, isCross);
            return -aiMove(updatedBoard, !isCross, movesLeft - 1);
        }

        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] posHolder = new ArrayList[3];
        int minOpponentDiff = Integer.MAX_VALUE;
        for (int i = 1; i <= 9; i++) {
            int updatedBoard = addToken(board, i, isCross);
            if (updatedBoard == board) {
                continue;
            }
            int opponentDiff = aiMove(updatedBoard, !isCross, movesLeft - 1);
            if (minOpponentDiff >= opponentDiff) {
                minOpponentDiff = opponentDiff;
                if (posHolder[minOpponentDiff + 1] == null) {
                    posHolder[minOpponentDiff + 1] = new ArrayList<>();
                }
                posHolder[minOpponentDiff + 1].add(i);
            }
        }
        int rndInd = ThreadLocalRandom.current().nextInt(posHolder[minOpponentDiff + 1].size());
        aiCache[board] = posHolder[minOpponentDiff + 1].get(rndInd);
        return -minOpponentDiff;
    }

    public boolean wins(boolean isCross) {
        return wins(board, isCross);
    }

    private boolean wins(int board, boolean isCross) {
        int ind = isCross ? 1 : 2;
        for (int[] masks : winMasks) {
            if ((masks[0] & board) == masks[ind]) {
                return true;
            }
        }
        return false;
    }

    private int addToken(int board, int cellId, boolean isCross) {
        // check input for correctness
        if (cellId < 1 || cellId > 9) {
            return board;
        }
        int shifts = (9 - cellId) * 2;

        // check if the cell is free
        if ((board & (0b11 << shifts)) != 0) {
            return board;
        }
        // place the token and return updated board
        return board | (isCross ? 0b01 : 0b10) << shifts;
    }

    public boolean addToken(int cellId, boolean isCross) {
        int updatedBoard = addToken(board, cellId, isCross);
        if (updatedBoard == board) {
            return false;
        }
        movesLeft--;
        board = updatedBoard;
        return true;
    }

    public void printBoard() {
        printBoard(board);
    }

    private void printBoard(int board) {
        System.out.println();
        String[] rowTokens = new String[3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                rowTokens[col] = tokens[(board >> 16) & 0b11];
                board <<= 2;
            }
            System.out.println(String.join("|", rowTokens));
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Hello, Player!");
        System.out.print("Please select your token ('X': 1, 'O': 2): ");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));) {
            boolean playesToken = readLine(reader).charAt(0) != '2'; // true means 'X', false 'O'

            System.out.println("\nYou play as " + (playesToken ? "'X'" : "'O'") + ". Here is your board layout:");
            System.out.println("\n1|2|3\n4|5|6\n7|8|9");

            boolean isCross = true;
            TicTacToe game = new TicTacToe();
            while (game.movesLeft > 0) {
                if (isCross == playesToken) {
                    System.out.print("\nMake your move (1-9): ");
                    char cell = readLine(reader).charAt(0);
                    while (cell < '0' || cell > '9' || !game.addToken(cell - '0', isCross)) {
                        System.out.print("Wrong move. Try again (1-9): ");
                        cell = readLine(reader).charAt(0);
                    }
                } else {
                    // computer moves
                    int cellId = game.aiMove(isCross);
                    System.out.println("\nComputer moves to " + cellId);
                    game.addToken(cellId, isCross);
                }
                game.printBoard();
                if (game.wins(isCross)) {
                    System.out.println();
                    System.out.println((isCross == playesToken ? "Player" : "Computer") + " wins!!!");
                    return;
                }
                isCross = !isCross;
            }
            System.out.println("\nDraw!");
        }
    }

    private static String readLine(BufferedReader reader) throws IOException {
        String res = null;
        while (res == null || res.isEmpty()) {
            res = reader.readLine();
        }
        return res;
    }

}
