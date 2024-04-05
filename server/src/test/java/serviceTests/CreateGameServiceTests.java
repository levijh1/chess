package serviceTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.CreateGameRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.response.CreateGameResponse;
import server.response.ErrorResponse;
import server.response.ParentResponse;
import server.response.RegisterAndLoginResponse;
import Service.ClearService;
import Service.CreateGameService;
import Service.LoginService;
import Service.RegisterService;

public class CreateGameServiceTests {
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
    public void successfulCreateGame() throws DataAccessException {

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();

        CreateGameResponse actual = (CreateGameResponse) createGameService.createGame(new CreateGameRequest("game1"), authToken);

        expected = new CreateGameResponse(actual.getGameID());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void unauthorizedCreateGame() throws DataAccessException {
        expected = new ErrorResponse("Error: unauthorized", 401);
        int gameID;

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        ParentResponse actual = createGameService.createGame(new CreateGameRequest("game1"), "invalid authToken");

        Assertions.assertEquals(expected, actual);
    }
}
