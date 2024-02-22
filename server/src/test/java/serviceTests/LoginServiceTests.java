package serviceTests;

import dataAccess.UserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.response.RegisterAndLoginResponse;
import service.ClearService;
import service.RegisterService;
import service.LoginService;

public class LoginServiceTests {
    private LoginService loginService = new LoginService();
    private RegisterService registerService = new RegisterService();
    private ClearService clearService = new ClearService();
    private RegisterAndLoginResponse expected;

    @BeforeEach
    public void setUp() {
        clearService.clear();
    }

    @Test
    public void basicLoginTest() {
        RegisterAndLoginResponse expected = new RegisterAndLoginResponse("testUsername", "someAuthToken");

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        RegisterAndLoginResponse actual = loginService.login(new LoginRequest("testUsername", "testPassword"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void usernameDoesNotExist() {
        RegisterAndLoginResponse expected = new RegisterAndLoginResponse("Error: user doesn't exist", 500);

        registerService.register(new RegisterRequest("testUsername1", "testPassword1", "testEmail"));
        RegisterAndLoginResponse actual = loginService.login(new LoginRequest("testUsername2", "testPassword2"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void incorrectPassword() {
        RegisterAndLoginResponse expected = new RegisterAndLoginResponse("Error: unauthorized", 401);

        registerService.register(new RegisterRequest("testUsername1", "testPassword1", "testEmail"));
        RegisterAndLoginResponse actual = loginService.login(new LoginRequest("testUsername1", "testPassword2"));

        Assertions.assertEquals(expected, actual);
    }

}
