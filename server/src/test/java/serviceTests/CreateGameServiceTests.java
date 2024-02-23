package serviceTests;

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
import service.ClearService;
import service.CreateGameService;
import service.LoginService;
import service.RegisterService;

public class CreateGameServiceTests {
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
    public void successfulCreateGame() {
        expected = new CreateGameResponse(1);
        int gameID;

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        CreateGameResponse actual = (CreateGameResponse) createGameService.createGame(new CreateGameRequest(authToken, "game1"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void unauthorizedCreateGame() {
        expected = new ErrorResponse("Error: unauthorized", 401);
        int gameID;

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        ParentResponse actual = createGameService.createGame(new CreateGameRequest("invalid Auth Token", "game1"));

        Assertions.assertEquals(expected, actual);
    }
}
