package dataAccessTests;

import dataAccess.AuthTokenDao;

import dataAccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthTokenDaoTests {
    AuthTokenDao dao = new AuthTokenDao();

    @BeforeEach
    public void setUp() throws DataAccessException {
        dao = new AuthTokenDao();
        dao.clearAuthTokens();
    }

    @Test
    public void createAuthTest() throws DataAccessException {
        String actual = dao.createAuth("username");

        Assertions.assertNotNull(actual);
        Assertions.assertInstanceOf(String.class, actual);
    }

    @Test
    public void badCreateAuthTest() throws DataAccessException {
        try {
            String actual = dao.createAuth(null);
        } catch (Exception ex) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void getAuthTest() throws DataAccessException {
        String expected = "testUsername";

        String token = dao.createAuth("testUsername");
        String actual = dao.getAuth(token);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void badGetAuthTest() throws DataAccessException {
        String expected = "testUsername1";

        String token = dao.createAuth("testUsername2");
        String actual = dao.getAuth(token);

        Assertions.assertNotEquals(expected, actual);
    }

    @Test
    public void deleteAuthTest() throws DataAccessException {
        ArrayList<AuthData> expected = new ArrayList<AuthData>();

        String token = dao.createAuth("testUsername");
        dao.deleteAuth(token);
        List<Object> actual = AuthTokenDao.getAuthTokens();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void badDeleteAuthTest() throws DataAccessException {
        try {
            dao.createAuth("testUsername");
            dao.deleteAuth("badToken");
        } catch (Exception ex) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void getAuthTokensTest() throws DataAccessException {
        String token1 = dao.createAuth("testUsername1");
        String token2 = dao.createAuth("testUsername2");
        List<Object> actual = dao.getAuthTokens();

        ArrayList<AuthData> expected = new ArrayList<AuthData>();
        expected.add(new AuthData("testUsername1", token1));
        expected.add(new AuthData("testUsername2", token2));

        Assertions.assertEquals(actual, expected);
    }

    @Test
    //test for getting list without any elements
    public void badGetAuthTokensTest() throws DataAccessException {
        List<Object> actual = dao.getAuthTokens();

        Assertions.assertEquals(actual, new ArrayList<AuthData>());
    }

    @Test
    public void clearAuthTokensTest() throws DataAccessException {
        dao.createAuth("testUsername1");
        dao.createAuth("testUsername2");
        dao.clearAuthTokens();

        List<Object> actual = dao.getAuthTokens();
        Assertions.assertEquals(actual, new ArrayList<AuthData>());
    }

}
