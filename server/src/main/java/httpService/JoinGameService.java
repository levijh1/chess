package httpService;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import server.WebsocketHandler;
import server.request.JoinGameRequest;
import server.response.ErrorResponse;
import server.response.ParentResponse;

public class JoinGameService {
    public ParentResponse joinGame(JoinGameRequest r, String authToken) {
        server.request.PlayerColor playerColor = r.playerColor();
        int gameID = r.gameID();
        String username;

        AuthTokenDao authTokenDao = new AuthTokenDao();
        GameDao gameDao = new GameDao();

        try {
            username = authTokenDao.getAuth(authToken);
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        try {
            gameDao.getGameData(gameID);
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: bad request", 400);
        }

        try {
            if (playerColor != null) {
                gameDao.updateGame(gameID, username, playerColor);
            }
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: already taken", 403);
        }

        return new ParentResponse();
    }
}
