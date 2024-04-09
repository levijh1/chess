package webSocketMessages.serverMessages;

import chess.ChessBoard;

public class LoadGameMessage extends ServerMessage {
    private final ChessBoard game;

    public ChessBoard getGame() {
        return game;
    }

    public LoadGameMessage(ServerMessage.ServerMessageType type, ChessBoard chessBoard) {
        super(type);
        this.game = chessBoard;
    }
}
