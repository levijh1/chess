package dataAccessTests;

import dataAccess.AuthTokenDao;

import dataAccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AuthTokenDaoTests {
    AuthTokenDao dao = new AuthTokenDao();

    @BeforeEach
    public void setUp() {
        dao = new AuthTokenDao();
        dao.clearAuthTokens();
    }

    @Test
    public void createAuthTest() {
        String actual = dao.createAuth("username");

        Assertions.assertNotNull(actual);
        Assertions.assertInstanceOf(String.class, actual);
    }

    @Test
    public void getAuthTest() throws DataAccessException {
        String expected = "testUsername";

        String token = dao.createAuth("testUsername");
        String actual = dao.getAuth(token);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteAuthTest() throws DataAccessException {
        ArrayList<AuthData> expected = new ArrayList<AuthData>();

        String token = dao.createAuth("testUsername");
        dao.deleteAuth(token);
        ArrayList<AuthData> actual = dao.getAuthTokens();

        Assertions.assertEquals(expected, actual);
    }


}
