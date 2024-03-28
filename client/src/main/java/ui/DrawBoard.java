package ui;

import chess.ChessBoard;
import chess.ChessGame;
import server.request.PlayerColor;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static ui.EscapeSequences.*;

public class DrawBoard {
    private static final List<String> whiteColumnLetters = Arrays.asList(" ", "a", "b", "c", "d", "e", "f", "g", "h", " ");
    private static final List<String> blackColumnLetters = Arrays.asList(" ", "h", "g", "f", "e", "d", "c", "b", "a", " ");

    public static void main(String[] args) {
        ChessBoard exampleBoard = new ChessBoard();
        exampleBoard.resetBoard();

        drawBothBoards(exampleBoard);
    }

    public static void drawBothBoards(ChessBoard board) {
        drawBoard(board, PlayerColor.BLACK);
        drawBlackRow();
        drawBoard(board, PlayerColor.WHITE);
    }

    public static void drawBoard(ChessBoard board, PlayerColor color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawLetterRow(out, color);
        drawInteriorRows(out, board, color);
        drawLetterRow(out, color);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawInteriorRows(PrintStream out, ChessBoard board, PlayerColor playerColor) {
        if (playerColor == PlayerColor.WHITE) {
            for (int boardRow = 8; boardRow > 0; --boardRow) {
                drawNumberRowBox(out, boardRow);
                for (int boardCol = 1; boardCol < 9; ++boardCol) {
                    drawBoardCoordinate(out, board, boardRow, boardCol);
                }
                drawNumberRowBox(out, boardRow);
                setBlack(out);
                out.println();
            }
        } else {
            for (int boardRow = 1; boardRow < 9; ++boardRow) {
                drawNumberRowBox(out, boardRow);
                for (int boardCol = 8; boardCol > 0; --boardCol) {
                    drawBoardCoordinate(out, board, boardRow, boardCol);
                }
                drawNumberRowBox(out, boardRow);
                setBlack(out);
                out.println();
            }
        }
    }

    private static void drawBoardCoordinate(PrintStream out, ChessBoard board, int boardRow, int boardCol) {
        String piece;
        ChessGame.TeamColor pieceColor;

        piece = board.getPieceString(boardRow, boardCol);
        if (piece != null) {
            pieceColor = board.getPieceColorString(boardRow, boardCol);
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                out.print(SET_TEXT_COLOR_WHITE);
            } else {
                out.print(SET_TEXT_COLOR_BLACK);
            }
        }
        if ((boardCol + boardRow) % 2 == 0) {
            drawBlueBox(out, piece);
        } else {
            drawRedBox(out, piece);
        }
    }

    private static void drawLetterRow(PrintStream out, PlayerColor color) {
        List<String> columnLetters;
        if (color == PlayerColor.WHITE) {
            columnLetters = whiteColumnLetters;
        } else {
            columnLetters = blackColumnLetters;
        }

        for (int boardCol = 0; boardCol < 10; ++boardCol) {
            setGrayWithBlackText(out);

            out.print(" ");
            out.print(columnLetters.get(boardCol));
            out.print(" ");

            setBlack(out);
        }

        out.println();
    }

    private static void drawBlackRow() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        for (int boardCol = 0; boardCol < 10; ++boardCol) {
            setBlack(out);
            out.print("   ");
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