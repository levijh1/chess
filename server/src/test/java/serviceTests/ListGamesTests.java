package serviceTests;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.CreateGameRequest;
import server.request.GenericRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.response.*;
import service.*;

import java.util.ArrayList;

public class ListGamesTests {
    private ListGamesService listGamesService = new ListGamesService();
    private CreateGameService createGameService = new CreateGameService();
    private LoginService loginService = new LoginService();
    private RegisterService registerService = new RegisterService();
    private ClearService clearService = new ClearService();
    private ParentResponse expected;

    @BeforeEach
    public void setUp() {
        clearService.clear();
    }

    @Test
    public void successfulListGames() {
        ArrayList<GameData> expectedList = new ArrayList<GameData>();
        expectedList.add(new GameData(1, null, null, "testName1", new ChessGame()));
        expectedList.add(new GameData(2, null, null, "testName2", new ChessGame()));

        expected = new ListGamesResponse(expectedList);

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        createGameService.createGame(new CreateGameRequest(authToken, "testName1"));
        createGameService.createGame(new CreateGameRequest(authToken, "testName2"));

        ParentResponse actual = listGamesService.listGames(new GenericRequest(authToken));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void unauthorizedListGames() {
        expected = new ErrorResponse("Error: unauthorized", 401);

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        createGameService.createGame(new CreateGameRequest(authToken, "testName1"));
        createGameService.createGame(new CreateGameRequest(authToken, "testName2"));

        ParentResponse actual = listGamesService.listGames(new GenericRequest("Invalid Auth Token"));

        Assertions.assertEquals(expected, actual);
    }
}