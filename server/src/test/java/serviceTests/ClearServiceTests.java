package serviceTests;

import chess.ChessGame;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import dataAccess.UserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import Service.ClearService;

import java.util.ArrayList;
import java.util.List;

public class ClearServiceTests {
    @Test
    public void successfulClear() throws DataAccessException {
        ClearService service = new ClearService();

        UserDao userDao = new UserDao();
        GameDao gameDao = new GameDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        ArrayList<UserData> expectedUsers = new ArrayList<UserData>();
        ArrayList<GameData> expectedGames = new ArrayList<GameData>();
        ArrayList<AuthData> expectedAuthTokens = new ArrayList<AuthData>();

        userDao.createUser("testUsername1", "testPassword1", "testEmail1");
        userDao.createUser("testUsername2", "testPassword2", "testEmail2");

        gameDao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());
        gameDao.createGame("whiteTest2", "blackTest2", "Game2", new ChessGame());

        authTokenDao.createAuth("testUsername1");
        authTokenDao.createAuth("testUsername2");

        service.clear();
        List<Object> actualUsers = UserDao.getUsers();
        List<GameData> actualGames = GameDao.getGames();
        List<Object> actualAuthTokens = AuthTokenDao.getAuthTokens();

        Assertions.assertEquals(expectedUsers, actualUsers);
        Assertions.assertEquals(expectedGames, actualGames);
        Assertions.assertEquals(expectedAuthTokens, actualAuthTokens);
    }
}
