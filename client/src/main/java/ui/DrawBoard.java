package ui;

import chess.ChessBoard;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static ui.EscapeSequences.*;

public class DrawBoard {
    private static final List<String> columnLetters = Arrays.asList(" ", "a", "b", "c", "d", "e", "f", "g", "h", " ");

    public static void main(String[] args) {
        ChessBoard exampleBoard = new ChessBoard();
        exampleBoard.resetBoard();

        drawBoard(exampleBoard);
    }

    public static void drawBoard(ChessBoard board) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawLetterRow(out);
        drawInteriorRows(out, board);
        drawLetterRow(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawLetterRow(PrintStream out) {
        for (int boardCol = 0; boardCol < 10; ++boardCol) {
            setGrayWithBlackText(out);

            out.print(" ");
            out.print(columnLetters.get(boardCol));
            out.print(" ");

            setBlack(out);
        }

        out.println();
    }

    private static void drawNumberRowBox(PrintStream out, int rowNumber) {
        setGrayWithBlackText(out);

        out.print(" ");
        out.print(rowNumber);
        out.print(" ");

        setBlack(out);
    }

    private static void drawInteriorRows(PrintStream out, ChessBoard board) {
        for (int boardRow = 8; boardRow > 0; --boardRow) {
            drawNumberRowBox(out, boardRow);

            for (int boardCol = 0; boardCol < 8; ++boardCol) {
//                String piece = board.getPieceString(boardRow, boardCol);
                if ((boardCol + boardRow) % 2 == 0) {
                    drawBlueBox(out, " ");
                } else {
                    drawRedBox(out, " ");
                }
            }

            drawNumberRowBox(out, boardRow);

            setBlack(out);
            out.println();
        }
    }

    private static void drawBlueBox(PrintStream out, String piece) {
        out.print(SET_BG_COLOR_BLUE);

        out.print(" ");
        out.print(piece);
        out.print(" ");
    }

    private static void drawRedBox(PrintStream out, String piece) {
        out.print(SET_BG_COLOR_RED);

        out.print(" ");
        out.print(piece);
        out.print(" ");
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setGrayWithBlackText(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}