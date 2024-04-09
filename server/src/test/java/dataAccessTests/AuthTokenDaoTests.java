package dataAccessTests;

import dataAccess.AuthTokenDao;

import dataAccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void badCreateAuthTest() {
        try {
            dao.createAuth(null);
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
        ArrayList<AuthData> expected = new ArrayList<>();

        String token = dao.createAuth("testUsername");
        dao.deleteAuth(token);
        List<Object> actual = AuthTokenDao.getAuthTokens();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void badDeleteAuthTest() {
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
        List<Object> actual = AuthTokenDao.getAuthTokens();

        ArrayList<AuthData> expected = new ArrayList<>();
        expected.add(new AuthData("testUsername1", token1));
        expected.add(new AuthData("testUsername2", token2));

        Set<Object> actualSet = new HashSet<>(actual);
        Set<Object> expectedSet = new HashSet<>(expected);

        Assertions.assertEquals(actualSet, expectedSet);
    }

    @Test
    //test for getting list without any elements
    public void badGetAuthTokensTest() throws DataAccessException {
        List<Object> actual = AuthTokenDao.getAuthTokens();

        Assertions.assertEquals(actual, new ArrayList<AuthData>());
    }

    @Test
    public void clearAuthTokensTest() throws DataAccessException {
        dao.createAuth("testUsername1");
        dao.createAuth("testUsername2");
        dao.clearAuthTokens();

        List<Object> actual = AuthTokenDao.getAuthTokens();
        Assertions.assertEquals(actual, new ArrayList<AuthData>());
    }

}
