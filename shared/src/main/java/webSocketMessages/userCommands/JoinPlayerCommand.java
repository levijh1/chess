package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {
    private int gameID;
    private ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = teamColor;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getTeamColor() {
        return playerColor;
    }

    ;
}
