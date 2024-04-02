package webSocketMessages.serverMessages;

import chess.ChessBoard;

public class LoadGameMessage extends ServerMessage {
    private final ChessBoard chessBoard;

    public ChessBoard getGame() {
        return chessBoard;
    }

    public LoadGameMessage(ServerMessage.ServerMessageType type, ChessBoard chessBoard) {
        super(type);
        this.chessBoard = chessBoard;
    }
}
