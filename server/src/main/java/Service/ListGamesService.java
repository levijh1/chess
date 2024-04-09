package Service;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.GameData;
import server.request.GenericRequest;
import server.response.ErrorResponse;
import server.response.ListGamesResponse;
import server.response.ParentResponse;

import java.util.List;

public class ListGamesService {
    public ParentResponse listGames(GenericRequest r, String authToken) throws DataAccessException {
        List<GameData> gameList;

        AuthTokenDao authTokenDao = new AuthTokenDao();

        try {
//            authTokenDao.getAuth(authToken);
            if (authTokenDao.getAuth(authToken) == null) {
                return new ErrorResponse("Error: unauthorized", 401);
            }
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        gameList = GameDao.getGames();
        return new ListGamesResponse(gameList);
    }
}
