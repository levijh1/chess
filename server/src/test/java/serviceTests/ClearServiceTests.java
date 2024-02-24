package serviceTests;

import chess.ChessGame;
import dataAccess.AuthTokenDao;
import dataAccess.GameDao;
import dataAccess.UserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ClearService;

import java.util.ArrayList;

public class ClearServiceTests {
    @Test
    public void successfulClear() {
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
        ArrayList<UserData> actualUsers = UserDao.getUsers();
        ArrayList<GameData> actualGames = GameDao.getGames();
        ArrayList<AuthData> actualAuthTokens = AuthTokenDao.getAuthTokens();

        Assertions.assertEquals(expectedUsers, actualUsers);
        Assertions.assertEquals(expectedGames, actualGames);
        Assertions.assertEquals(expectedAuthTokens, actualAuthTokens);
    }
}
