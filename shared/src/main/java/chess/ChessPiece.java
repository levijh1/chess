package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    private Collection<ChessMove> movesInOneDirection(ChessBoard board, ChessPosition myPosition, int moveX, int moveY) {
        List<ChessMove> possibleMoves = new LinkedList<>();

        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getCol();

        ChessPosition endPosition;
        for (int i = 1; i <= 7; i++) {
            endPosition = new ChessPosition(currentRow + moveX * i, currentCol + moveY * i);
            if (!endPosition.isOnBoard()) { //Stop if moving off the board
                break;
            }
            possibleMoves.add(new ChessMove(myPosition, endPosition, null));
            if (board.getPiece(endPosition) != null) { //Stop if path is obstructed by another piece
                break;
            }
        }

        return possibleMoves;
    }

    public ChessPiece clone() {
        return new ChessPiece(pieceColor, type);
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Set<ChessMove> possibleMoves = new HashSet<>();

        PieceType evaluatedPieceType = board.getPiece(myPosition).getPieceType();

        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getCol();

        ChessMove captureMove;
        ChessPosition endPosition;
        switch (evaluatedPieceType) {
            case KING:
                possibleMoves.addAll(kingMoves(board, myPosition, currentRow, currentCol));
                break;
            case QUEEN:
                possibleMoves.addAll(queenMoves(board, myPosition, currentRow, currentCol));
                break;
            case BISHOP:
                possibleMoves.addAll(bishopMoves(board, myPosition, currentRow, currentCol));
                break;
            case KNIGHT:
                possibleMoves.addAll(knightMoves(board, myPosition, currentRow, currentCol));
                break;
            case ROOK:
                possibleMoves.addAll(movesInOneDirection(board, myPosition, 1, 0));
                possibleMoves.addAll(movesInOneDirection(board, myPosition, -1, 0));
                possibleMoves.addAll(movesInOneDirection(board, myPosition, 0, 1));
                possibleMoves.addAll(movesInOneDirection(board, myPosition, 0, -1));
                break;
            case PAWN:
                if (this.pieceColor == ChessGame.TeamColor.WHITE) { //Piece is white
                    //Promotion
                    if (currentRow == 7) {
                        ChessMove promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol), PieceType.QUEEN);
                        possibleMoves.add(promotionMove);
                        promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol), PieceType.BISHOP);
                        possibleMoves.add(promotionMove);
                        promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol), PieceType.KNIGHT);
                        possibleMoves.add(promotionMove);
                        promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol), PieceType.ROOK);
                        possibleMoves.add(promotionMove);

                        //Capturing a piece and promoting
                        captureMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol + 1), null);
                        if (captureMove.isMoveOnBoard()) {
                            if (captureMove.isMoveOnDifferentColor(board, this)) {
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol + 1), PieceType.QUEEN);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol + 1), PieceType.BISHOP);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol + 1), PieceType.KNIGHT);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol + 1), PieceType.ROOK);
                                possibleMoves.add(promotionMove);
                            }
                        }

                        captureMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol - 1), null);
                        if (captureMove.isMoveOnBoard()) {
                            if (captureMove.isMoveOnDifferentColor(board, this)) {
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol - 1), PieceType.QUEEN);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol - 1), PieceType.BISHOP);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol - 1), PieceType.KNIGHT);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol - 1), PieceType.ROOK);
                                possibleMoves.add(promotionMove);
                            }
                        }
                    } else {
                        endPosition = new ChessPosition(currentRow + 1, currentCol);
                        possibleMoves.add(new ChessMove(myPosition, endPosition, null));


                        //Initial movement
                        if (currentRow == 2) {
                            endPosition = new ChessPosition(currentRow + 2, currentCol);
                            if (board.getPiece(new ChessPosition(currentRow + 1, currentCol)) == null) {
                                possibleMoves.add(new ChessMove(myPosition, endPosition, null));
                            }
                        }

                        possibleMoves.removeIf(chessMove -> chessMove.isMoveOnDifferentColor(board, this));

                        //Capturing a piece
                        captureMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol + 1), null);
                        if (captureMove.isMoveOnBoard()) {
                            if (captureMove.isMoveOnDifferentColor(board, this)) {
                                possibleMoves.add(captureMove);
                            }
                        }

                        captureMove = new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol - 1), null);
                        if (captureMove.isMoveOnBoard()) {
                            if (captureMove.isMoveOnDifferentColor(board, this)) {
                                possibleMoves.add(captureMove);
                            }
                        }
                    }

                } else { //Piece is black
                    //Promotion
                    if (currentRow == 2) {
                        ChessMove promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol), PieceType.QUEEN);
                        possibleMoves.add(promotionMove);
                        promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol), PieceType.BISHOP);
                        possibleMoves.add(promotionMove);
                        promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol), PieceType.KNIGHT);
                        possibleMoves.add(promotionMove);
                        promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol), PieceType.ROOK);
                        possibleMoves.add(promotionMove);

                        //Capturing a piece and promoting
                        captureMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol + 1), null);
                        if (captureMove.isMoveOnBoard()) {
                            if (captureMove.isMoveOnDifferentColor(board, this)) {
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol + 1), PieceType.QUEEN);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol + 1), PieceType.BISHOP);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol + 1), PieceType.KNIGHT);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol + 1), PieceType.ROOK);
                                possibleMoves.add(promotionMove);
                            }
                        }

                        captureMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol - 1), null);
                        if (captureMove.isMoveOnBoard()) {
                            if (captureMove.isMoveOnDifferentColor(board, this)) {
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol - 1), PieceType.QUEEN);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol - 1), PieceType.BISHOP);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol - 1), PieceType.KNIGHT);
                                possibleMoves.add(promotionMove);
                                promotionMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol - 1), PieceType.ROOK);
                                possibleMoves.add(promotionMove);
                            }
                        }
                    } else {
                        endPosition = new ChessPosition(currentRow - 1, currentCol);
                        possibleMoves.add(new ChessMove(myPosition, endPosition, null));

                        //Initial movement
                        if (currentRow == 7) {
                            endPosition = new ChessPosition(currentRow - 2, currentCol);
                            if (board.getPiece(new ChessPosition(currentRow - 1, currentCol)) == null) {
                                possibleMoves.add(new ChessMove(myPosition, endPosition, null));
                            }
                        }

                        possibleMoves.removeIf(chessMove -> chessMove.isMoveOnDifferentColor(board, this));

                        //Capturing a piece
                        captureMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol + 1), null);
                        if (captureMove.isMoveOnBoard()) {
                            if (captureMove.isMoveOnDifferentColor(board, this)) {
                                possibleMoves.add(captureMove);
                            }
                        }

                        captureMove = new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol - 1), null);
                        if (captureMove.isMoveOnBoard()) {
                            if (captureMove.isMoveOnDifferentColor(board, this)) {
                                possibleMoves.add(captureMove);
                            }
                        }
                    }
                }
                break;
        }
        possibleMoves.removeIf(chessMove -> !chessMove.isMoveOnBoard());
        possibleMoves.removeIf(chessMove -> chessMove.isMoveOnSameColor(board, this));


        return possibleMoves;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, int currentRow, int currentCol) {
        Set<ChessMove> possibleMoves = new HashSet<>();

        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol - 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol + 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow, currentCol - 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow, currentCol + 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol + 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol - 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol), null));

        return possibleMoves;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, int currentRow, int currentCol) {
        Set<ChessMove> possibleMoves = new HashSet<>();

        possibleMoves.addAll(movesInOneDirection(board, myPosition, 1, 0));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, -1, 0));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, 0, 1));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, 0, -1));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, 1, 1));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, 1, -1));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, -1, 1));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, -1, -1));

        return possibleMoves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, int currentRow, int currentCol) {
        Set<ChessMove> possibleMoves = new HashSet<>();

        possibleMoves.addAll(movesInOneDirection(board, myPosition, 1, 1));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, 1, -1));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, -1, 1));
        possibleMoves.addAll(movesInOneDirection(board, myPosition, -1, -1));

        return possibleMoves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, int currentRow, int currentCol) {
        Set<ChessMove> possibleMoves = new HashSet<>();

        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + 2, currentCol + 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + 2, currentCol - 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow - 2, currentCol + 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow - 2, currentCol - 1), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol + 2), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow + 1, currentCol - 2), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol + 2), null));
        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(currentRow - 1, currentCol - 2), null));

        return possibleMoves;
    }
}
