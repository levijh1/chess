package Service;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import request.PlayerColor;
import request.JoinGameRequest;
import response.ErrorResponse;
import response.ParentResponse;

import java.util.Objects;

public class JoinGameService {
    public ParentResponse joinGame(JoinGameRequest r, String authToken) {
        PlayerColor playerColor = r.playerColor();
        int gameID = r.gameID();
        String username;

        AuthTokenDao authTokenDao = new AuthTokenDao();
        GameDao gameDao = new GameDao();

        try {
            username = authTokenDao.getAuth(authToken);
            if (username == null) {
                return new ErrorResponse("Error: unauthorized", 401);
            }
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
                String databaseName = gameDao.getPlayerName(gameID, playerColor);
                if (!Objects.equals(databaseName, username)) {
                    gameDao.updateGamePlayers(gameID, username, playerColor);
                }
            }
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: already taken", 403);
        }

        return new ParentResponse();
    }
}
