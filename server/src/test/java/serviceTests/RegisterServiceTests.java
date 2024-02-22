package serviceTests;

import dataAccess.UserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.RegisterRequest;
import server.response.RegisterResponse;
import service.RegisterService;

public class RegisterServiceTests {
    private RegisterService service;
    private RegisterResponse expected;

    @BeforeEach
    public void setUp() {
        service = new RegisterService();
        UserDao userDao = new UserDao();
        userDao.clearUsers();
    }


    @Test
    public void BasicRegisterTest() {
        expected = new RegisterResponse("testUsername", "someAuthToken");

        RegisterResponse actual = service.register(new RegisterRequest("testUsername", "testPassword"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void UsernameAlreadyTakenTest() {
        expected = new RegisterResponse("Error: already taken", 403);

        service.register(new RegisterRequest("testUsername", "testPassword1"));
        RegisterResponse actual = service.register(new RegisterRequest("testUsername", "testPassword2"));

        Assertions.assertEquals(expected, actual);
    }
}
