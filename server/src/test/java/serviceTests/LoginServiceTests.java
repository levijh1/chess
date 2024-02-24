package serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.response.ErrorResponse;
import server.response.RegisterAndLoginResponse;
import server.response.ParentResponse;
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
    public void successfulLoginTest() {
        RegisterAndLoginResponse expected = new RegisterAndLoginResponse("testUsername", "someAuthToken");

        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        ParentResponse actual = loginService.login(new LoginRequest("testUsername", "testPassword"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void usernameDoesNotExist() {
        ParentResponse expected = new ErrorResponse("Error: unauthorized", 401);

        registerService.register(new RegisterRequest("testUsername1", "testPassword1", "testEmail"));
        ParentResponse actual = loginService.login(new LoginRequest("testUsername2", "testPassword2"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void incorrectPassword() {
        ParentResponse expected = new ErrorResponse("Error: unauthorized", 401);

        registerService.register(new RegisterRequest("testUsername1", "testPassword1", "testEmail"));
        ParentResponse actual = loginService.login(new LoginRequest("testUsername1", "testPassword2"));

        Assertions.assertEquals(expected, actual);
    }

}
