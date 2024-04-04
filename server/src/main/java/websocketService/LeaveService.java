package websocketService;

import dataAccess.DataAccessException;
import dataAccess.GameDao;
import server.response.ParentResponse;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.LeaveCommand;

public class LeaveService {
    public ServerMessage leave(String userName, LeaveCommand command) throws DataAccessException {
        //TODO: If tests don't work make sure you are only leaving from one team at a time
        int GameId = command.getGameID();

        GameDao gameDao = new GameDao();

        try {
            GameDao.removePlayer(GameId, userName);
            return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, userName + " has left the game");
        } catch (Exception ex) {
            return new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Bad leave");
        }
    }
}
