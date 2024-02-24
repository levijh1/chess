package serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.GenericRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.response.ErrorResponse;
import server.response.RegisterAndLoginResponse;
import server.response.ParentResponse;
import service.ClearService;
import service.LogoutService;
import service.RegisterService;
import service.LoginService;

public class LogoutServiceTests {
    private final LogoutService logoutService = new LogoutService();
    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();
    private final ClearService clearService = new ClearService();

    @BeforeEach
    public void setUp() {
        clearService.clear();
    }

    @Test
    public void successfulLogoutTest() {
        ParentResponse expected = new ParentResponse();

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        ParentResponse actual = logoutService.logout(new GenericRequest(), authToken);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void unauthorizedLogoutTest() {
        ParentResponse expected = new ErrorResponse("Error: unauthorized", 401);

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));

        ParentResponse actual = logoutService.logout(new GenericRequest(), "invalid Auth Token");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void notSignedInTest() {
        ParentResponse expected = new ErrorResponse("Error: unauthorized", 401);

        ParentResponse actual = logoutService.logout(new GenericRequest(), "invalid Auth Token");

        Assertions.assertEquals(expected, actual);
    }
}
