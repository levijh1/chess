package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.UserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.RegisterRequest;
import server.response.ErrorResponse;
import server.response.RegisterAndLoginResponse;
import server.response.ParentResponse;
import service.RegisterService;

public class RegisterServiceTests {
    private RegisterService service;
    private ParentResponse expected;

    @BeforeEach
    public void setUp() {
        service = new RegisterService();
        UserDao userDao = new UserDao();
        userDao.clearUsers();
    }


    @Test
    public void BasicRegisterTest() throws DataAccessException {
        expected = new RegisterAndLoginResponse("testUsername", "someAuthToken");

        ParentResponse actual = service.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void UsernameAlreadyTakenTest() throws DataAccessException {
        expected = new ErrorResponse("Error: already taken", 403);

        service.register(new RegisterRequest("testUsername", "testPassword1", "testEmail"));
        ParentResponse actual = service.register(new RegisterRequest("testUsername", "testPassword2", "testEmail"));

        Assertions.assertEquals(expected, actual);
    }
}
