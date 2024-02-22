package serviceTests;

import dataAccess.UserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.RegisterRequest;
import server.response.RegisterAndLoginResponse;
import service.RegisterService;

public class RegisterServiceTests {
    private RegisterService service;
    private RegisterAndLoginResponse expected;

    @BeforeEach
    public void setUp() {
        service = new RegisterService();
        UserDao userDao = new UserDao();
        userDao.clearUsers();
    }


    @Test
    public void BasicRegisterTest() {
        expected = new RegisterAndLoginResponse("testUsername", "someAuthToken");

        RegisterAndLoginResponse actual = service.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void UsernameAlreadyTakenTest() {
        expected = new RegisterAndLoginResponse("Error: already taken", 403);

        service.register(new RegisterRequest("testUsername", "testPassword1", "testEmail"));
        RegisterAndLoginResponse actual = service.register(new RegisterRequest("testUsername", "testPassword2", "testEmail"));

        Assertions.assertEquals(expected, actual);
    }
}
