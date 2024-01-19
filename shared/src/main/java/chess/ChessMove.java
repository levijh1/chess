package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    /**
     * Checks if the move will result in the piece staying on a playable space
     *
     * @return true if the move is valid and stays on the board and false if the move is out of bounds
     */
    public boolean isMoveOnBoard(){
        return endPosition.isOnBoard();
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param board the current playing board being used
     * @param piece the current piece that is doing the moving
     * @return true if the movement will result in the piece landing on a space that contains a piece of the same color
     */
    public boolean isMoveOnSameColor(ChessBoard board, ChessPiece piece) {
        if (board.getPiece(endPosition)==null) {
            return false;
        }
        return piece.getTeamColor() == board.getPiece(endPosition).getTeamColor();
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param board the current playing board being used
     * @param piece the current piece that is doing the moving
     * @return true if the movement will result in the piece landing on a space that contains a piece of the opposite color
     */
    public boolean isMoveOnDifferentColor(ChessBoard board, ChessPiece piece) {
        if (board.getPiece(endPosition)==null) {
            return false;
        }
        return piece.getTeamColor() != board.getPiece(endPosition).getTeamColor();
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", promotionPiece=" + promotionPiece +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
