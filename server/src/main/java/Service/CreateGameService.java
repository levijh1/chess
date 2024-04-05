package Service;

import chess.ChessGame;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import server.request.CreateGameRequest;
import server.response.CreateGameResponse;
import server.response.ErrorResponse;
import server.response.ParentResponse;

public class CreateGameService {
    public ParentResponse createGame(CreateGameRequest r, String authToken) throws DataAccessException {
        String gameName = r.gameName();
        int gameID;

        if (gameName == null) {
            return new ErrorResponse("Error: bad request", 400);
        }

        AuthTokenDao authTokenDao = new AuthTokenDao();
        GameDao gameDao = new GameDao();

        try {
            authTokenDao.getAuth(authToken);
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        gameID = gameDao.createGame(null, null, gameName, new ChessGame());

        return new CreateGameResponse(gameID);
    }
}
