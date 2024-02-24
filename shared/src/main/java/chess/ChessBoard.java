package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] boardPieces = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getCol();

        boardPieces[row - 1][col - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getCol();

        return boardPieces[row - 1][col - 1];
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "boardPieces=" + Arrays.toString(boardPieces[0])
                + Arrays.toString(boardPieces[1])
                + Arrays.toString(boardPieces[2])
                + Arrays.toString(boardPieces[3])
                + Arrays.toString(boardPieces[4])
                + Arrays.toString(boardPieces[5])
                + Arrays.toString(boardPieces[6])
                + Arrays.toString(boardPieces[7])
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        ChessPiece thisPiece;
        ChessPiece thatPiece;
        for (int i = 0; i <= 7; ++i) {
            for (int j = 0; j <= 7; ++j) {
                thisPiece = this.boardPieces[i][j];
                thatPiece = that.boardPieces[i][j];
                System.out.print(thisPiece);
                System.out.print(thatPiece);
                if (thisPiece == null && thatPiece != null) {
                    return false;
                }
                if (thisPiece != null && thatPiece == null) {
                    return false;
                }
                if (thisPiece == null && thatPiece == null) {
                    continue;
                }
                if (thisPiece.getPieceType() != thatPiece.getPieceType() || thisPiece.getTeamColor() != thatPiece.getTeamColor()) {
                    return false;
                }

            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardPieces);
    }

    @Override
    public ChessBoard clone() {
        ChessBoard clonedBoard = new ChessBoard();

        // Deep copy of the ChessPiece array
        clonedBoard.boardPieces = new ChessPiece[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                ChessPiece originalPiece = this.boardPieces[i][j];
                if (originalPiece != null) {
                    ChessPiece clonedPiece = originalPiece.clone(); // Assuming ChessPiece implements Cloneable
                    clonedBoard.boardPieces[i][j] = clonedPiece;
                }
            }
        }

        return clonedBoard;
    }

    public void removePiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getCol();

        boardPieces[row - 1][col - 1] = null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        boardPieces = new ChessPiece[8][8];

        //Add Pawns
        for (int i = 1; i <= 8; ++i) {
            this.addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            this.addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        //Add rest of white pieces
        this.addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        //Add rest of black pieces
        this.addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }
}
