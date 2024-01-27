package chess;

import java.util.Collection;
// import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;



    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        }

        ChessPiece evaluatedPiece = board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves = evaluatedPiece.pieceMoves(board, startPosition);

        for (ChessMove move : possibleMoves){
            try {
                //TODO: try to make a move
            } catch (InvalidMoveException ex) {
                //TODO: If move puts king in check, remove move from list of possible moves
            }
        }

        return possibleMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException, CloneNotSupportedException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPiece movingPiece = board.getPiece(startPosition);
        ChessBoard initialBoard = board.clone();

        if (!endPosition.isOnBoard()) {
            throw new InvalidMoveException("Move is not on board");
        }
        if (move.isMoveOnSameColor(board,movingPiece)){
            throw new InvalidMoveException("Move is onto a piece on the same team");
        }
        if (getTeamTurn()!=movingPiece.getTeamColor()){
            throw new InvalidMoveException("Moving team does not correspond to current team's turn");
        }

        board.addPiece(endPosition, movingPiece);
        board.removePiece(startPosition);

        if (isInCheck(teamTurn)){
            board = initialBoard;
            throw new InvalidMoveException("Move cannot put own King into check");
        }
        
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition KingPosition = null;
        kingsearch:{
            for (int i = 1; i<=8; ++i) {
                for (int j = 1; j<=8; ++j) {
                    ChessPiece evaluatedPiece = board.getPiece(new ChessPosition(i,j));
                    if (evaluatedPiece != null) {
                        if (evaluatedPiece.getPieceType() == ChessPiece.PieceType.KING && evaluatedPiece.getTeamColor() == teamColor) {
                            KingPosition = new ChessPosition(i,j);
                            break kingsearch;
                        }
                    }
                }
            }
        }

        Collection<ChessMove> possibleMoves;
        for (int i = 1; i<=8; ++i) {
            for (int j = 1; j<=8; ++j) {
                ChessPiece evaluatedPiece = board.getPiece(new ChessPosition(i,j));
                if (evaluatedPiece != null) {
                    if (evaluatedPiece.getTeamColor() != teamColor) {
                        possibleMoves = evaluatedPiece.pieceMoves(board, new ChessPosition(i,j));

                        for (ChessMove move : possibleMoves) {
                            ChessPosition endPosition = move.getEndPosition();
                            if (endPosition.equals(KingPosition)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {this.board = board;}

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {return board;}
}
