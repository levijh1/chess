package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand {
    //TODO: switch to Integer class
    private final int gameID;

    public JoinObserverCommand(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
