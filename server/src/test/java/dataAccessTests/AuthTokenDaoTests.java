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
        List<Object> actual = AuthTokenDao.getAuthTokens();

        Assertions.assertEquals(expected, actual);
    }


}
