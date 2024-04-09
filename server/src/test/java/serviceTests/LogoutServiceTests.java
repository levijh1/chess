package serviceTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.GenericRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.ErrorResponse;
import response.RegisterAndLoginResponse;
import response.ParentResponse;
import Service.ClearService;
import Service.LogoutService;
import Service.RegisterService;
import Service.LoginService;

public class LogoutServiceTests {
    private final LogoutService logoutService = new LogoutService();
    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();
    private final ClearService clearService = new ClearService();

    @BeforeEach
    public void setUp() throws DataAccessException {
        clearService.clear();
    }

    @Test
    public void successfulLogoutTest() throws DataAccessException {
        ParentResponse expected = new ParentResponse();

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse loginResponse = (RegisterAndLoginResponse) loginService.login(new LoginRequest("testUsername", "testPassword"));
        String authToken = loginResponse.getAuthToken();
        ParentResponse actual = logoutService.logout(new GenericRequest(), authToken);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void unauthorizedLogoutTest() throws DataAccessException {
        ParentResponse expected = new ErrorResponse("Error: unauthorized", 401);

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        loginService.login(new LoginRequest("testUsername", "testPassword"));

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
