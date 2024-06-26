package serviceTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import response.ErrorResponse;
import response.RegisterAndLoginResponse;
import response.ParentResponse;
import Service.ClearService;
import Service.RegisterService;
import Service.LoginService;

public class LoginServiceTests {
    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();
    private final ClearService clearService = new ClearService();

    @BeforeEach
    public void setUp() throws DataAccessException {
        clearService.clear();
    }

    @Test
    public void successfulLoginTest() throws DataAccessException {
        RegisterAndLoginResponse expected = new RegisterAndLoginResponse("testUsername", "someAuthToken");

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        ParentResponse actual = loginService.login(new LoginRequest("testUsername", "testPassword"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void usernameDoesNotExist() throws DataAccessException {
        ParentResponse expected = new ErrorResponse("Error: unauthorized", 401);

        registerService.register(new RegisterRequest("testUsername1", "testPassword1", "testEmail"));
        ParentResponse actual = loginService.login(new LoginRequest("testUsername2", "testPassword2"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void incorrectPassword() throws DataAccessException {
        ParentResponse expected = new ErrorResponse("Error: unauthorized", 401);

        registerService.register(new RegisterRequest("testUsername1", "testPassword1", "testEmail"));
        ParentResponse actual = loginService.login(new LoginRequest("testUsername1", "testPassword2"));

        Assertions.assertEquals(expected, actual);
    }

}
