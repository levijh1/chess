package serviceTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.*;
import server.response.CreateGameResponse;
import server.response.ErrorResponse;
import server.response.ParentResponse;
import server.response.RegisterAndLoginResponse;
import Service.*;

public class JoinGameServiceTests {
    private final JoinGameService joinGameService = new JoinGameService();
    private final CreateGameService createGameService = new CreateGameService();
    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();
    private final ClearService clearService = new ClearService();
    private ParentResponse expected;

    @BeforeEach
    public void setUp() throws DataAccessException {
        clearService.clear();
    }

    @Test
    public void successfulJoinGame() throws DataAccessException {
        expected = new ParentResponse();
        int gameID;

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        CreateGameResponse createGameResponse = (CreateGameResponse) createGameService.createGame(new CreateGameRequest("game1"), authToken);
        gameID = createGameResponse.getGameID();
        ParentResponse actual = joinGameService.joinGame(new JoinGameRequest(PlayerColor.WHITE, gameID), authToken);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void unauthorizedJoinGame() throws DataAccessException {
        expected = new ErrorResponse("Error: unauthorized", 401);
        int gameID;

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        CreateGameResponse createGameResponse = (CreateGameResponse) createGameService.createGame(new CreateGameRequest("game1"), authToken);
        gameID = createGameResponse.getGameID();
        ParentResponse actual = joinGameService.joinGame(new JoinGameRequest(PlayerColor.WHITE, gameID), "unauthorized auth token");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void gameIdDoesNotExist() throws DataAccessException {
        expected = new ErrorResponse("Error: bad request", 400);

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        ParentResponse actual = joinGameService.joinGame(new JoinGameRequest(PlayerColor.WHITE, 2), authToken);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void spotAlreadyTakenInGame() throws DataAccessException {
        expected = new ErrorResponse("Error: already taken", 403);
        int gameID;

        registerService.register(new RegisterRequest("testUsername1", "testPassword1", "testEmail1"));
        RegisterAndLoginResponse loginResponse1 = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername1", "testPassword1"));
        String authToken1 = loginResponse1.getAuthToken();
        CreateGameResponse createGameResponse = (CreateGameResponse) createGameService.createGame(new CreateGameRequest("game1"), authToken1);
        gameID = createGameResponse.getGameID();
        joinGameService.joinGame(new JoinGameRequest(PlayerColor.WHITE, gameID), authToken1);

        registerService.register(new RegisterRequest("testUsername2", "testPassword2", "testEmail2"));
        RegisterAndLoginResponse loginResponse2 = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername2", "testPassword2"));
        String authToken2 = loginResponse2.getAuthToken();
        ParentResponse actual = joinGameService.joinGame(new JoinGameRequest(PlayerColor.WHITE, gameID), authToken2);

        Assertions.assertEquals(expected, actual);
    }


}
