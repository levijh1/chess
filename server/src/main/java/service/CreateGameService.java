package service;

import chess.ChessGame;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import server.request.CreateGameRequest;
import server.response.CreateGameResponse;
import server.response.ErrorResponse;
import server.response.ParentResponse;

public class CreateGameService {
    public ParentResponse createGame(CreateGameRequest r) {
        String authToken = r.authToken();
        String gameName = r.gameName();
        int gameID;

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
