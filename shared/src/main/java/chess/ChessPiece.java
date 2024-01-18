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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> possibleMoves;
        possibleMoves = new LinkedList<>();

        PieceType evaluatedPieceType = board.getPiece(myPosition).getPieceType();

        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getCol();

        switch (evaluatedPieceType) {
            case KING:
                break;
            case QUEEN:
                break;
            case BISHOP:
//                for (int i = 1; i <= 8-currentRow or i <= 8-currentCol; i++) {
//                    ChessPosition endPosition = new ChessPosition(currentRow+i, currentCol+i);
//                    if (board.getPiece(endPosition).getTeamColor() == this.pieceColor)
//                        possibleMoves.add(new ChessMove(myPosition, endPosition, null));
//                    }
//                }
//                possibleMoves.add();
                break;
            case KNIGHT:
                break;
            case ROOK:
                break;
            case PAWN:
                ChessPosition endPosition;
                if (this.pieceColor== ChessGame.TeamColor.WHITE){ //Piece is white
                    endPosition = new ChessPosition(currentRow+1, currentCol);
                    possibleMoves.add(new ChessMove(myPosition, endPosition, null));

                    if (currentRow==2){
                        endPosition = new ChessPosition(currentRow+2, currentCol);
                        possibleMoves.add(new ChessMove(myPosition, endPosition, null));
                    }
                } else{ //Piece is black
                    endPosition = new ChessPosition(currentRow-1, currentCol);
                    possibleMoves.add(new ChessMove(myPosition, endPosition, null));

                    if (currentRow==7){
                        endPosition = new ChessPosition(currentRow+2, currentCol);
                        possibleMoves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
                break;
        }
        possibleMoves.removeIf(chessMove -> !chessMove.isMoveOnBoard());
        possibleMoves.removeIf(chessMove -> chessMove.isMoveOnSameColor(board, this));

        return possibleMoves;
    }
}
