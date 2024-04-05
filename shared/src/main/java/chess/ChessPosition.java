package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public ChessPosition(String chessCoordinates) {
        this(chessCoordinates.charAt(1) - '1' + 1, chessCoordinates.charAt(0) - 'a' + 1);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getCol() {
        return col;
    }

    /**
     * @return true if the position is a valid location on the board
     */
    public boolean isOnBoard() {
        return this.row > 0 && this.row <= 8 && this.col > 0 && this.col <= 8;
    }

    public String positionToString() {
        char chessColumn = (char) ('a' + col - 1);
        char chessRow = (char) ('1' + row - 1);
        return String.valueOf(chessColumn) + String.valueOf(chessRow);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
