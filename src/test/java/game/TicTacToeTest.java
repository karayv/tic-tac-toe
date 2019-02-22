package game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import game.TicTacToe;

public class TicTacToeTest {
    // 1 is cross, 2 is zero

    @Test
    void addToken() throws Exception {
        TicTacToe ticTacToe = new TicTacToe();
        ticTacToe.board = 0b01_10_00_01_10_00_01_10_00;
        ticTacToe.printBoard();

        // X|O|-
        // X|O|-
        // X|O|-

        assertTrue(ticTacToe.addToken(9, true));
        assertTrue(ticTacToe.addToken(3, false));
        assertFalse(ticTacToe.addToken(7, true));
        assertFalse(ticTacToe.addToken(5, false));
    }

    @Test
    void wins() {
        TicTacToe ticTacToe = new TicTacToe();
        ticTacToe.board = 0b01_10_00_01_10_00_01_10_00;
        assertTrue(ticTacToe.wins(true));
        assertTrue(ticTacToe.wins(false));

        ticTacToe.board = 0b01_10_01_10_10_01_10_01_10;
        assertFalse(ticTacToe.wins(true));
        assertFalse(ticTacToe.wins(false));
    }

    // 1|2|3
    // 4|5|6
    // 7|8|9

    @CsvSource({ //
            "1,5,9", //
            "3,5,7", //
            "1,2,3", //
            "4,5,6", //
            "7,8,9", //
            "1,4,7", //
            "2,5,8", //
            "3,6,9" //
    }) //
    @ParameterizedTest
    void wins(int pos1, int pos2, int pos3) throws Exception {
        boolean isCross = false;
        for (int i = 0; i < 2; i++) {
            TicTacToe ticTacToe = new TicTacToe();
            ticTacToe.board = 0;

            assertTrue(ticTacToe.addToken(pos1, isCross));
            assertFalse(ticTacToe.wins(isCross));
            assertFalse(ticTacToe.wins(!isCross));

            assertTrue(ticTacToe.addToken(pos2, isCross));
            assertFalse(ticTacToe.wins(isCross));
            assertFalse(ticTacToe.wins(!isCross));

            assertTrue(ticTacToe.addToken(pos3, isCross));
            assertTrue(ticTacToe.wins(isCross));
            assertFalse(ticTacToe.wins(!isCross));

            isCross = !isCross;
        }
    }

    @CsvSource({ //
            "1,5,9", //
            "3,5,7", //
            "1,2,3", //
            "4,5,6", //
            "7,8,9", //
            "1,4,7", //
            "2,5,8", //
            "3,6,9" //
    }) //
    @ParameterizedTest
    void preventLosing(int pos1, int pos2, int pos3) {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(pos1, pos2, pos3));
        boolean isCross = false;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                TicTacToe game = new TicTacToe();
                game.board = 0;

                assertEquals(-1, game.winningMove(game.board, isCross));
                assertEquals(-1, game.winningMove(game.board, !isCross));

                game.addToken(list.get(0), isCross);

                assertEquals(-1, game.winningMove(game.board, isCross));
                assertEquals(-1, game.winningMove(game.board, !isCross));

                game.addToken(list.get(1), isCross);

                assertEquals((int) list.get(2), game.winningMove(game.board, isCross));
                assertEquals(-1, game.winningMove(game.board, !isCross));

                game.addToken(list.get(2), isCross);

                assertEquals(-1, game.winningMove(game.board, isCross));
                assertEquals(-1, game.winningMove(game.board, !isCross));

                // spin test data
                list.offer(list.poll());
            }
            isCross = !isCross;
        }
    }

    @Test
    void testAi() throws Exception {
        TicTacToe game = new TicTacToe();
        game.addToken(1, true);
        game.addToken(3, false);

        assertThat(game.aiMove(true)).isIn(4, 7, 9);
    }
}
