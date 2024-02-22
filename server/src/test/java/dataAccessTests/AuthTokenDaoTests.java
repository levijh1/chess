package dataAccessTests;

import dataAccess.AuthTokenDao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthTokenDaoTests {
    AuthTokenDao dao = new AuthTokenDao();
//    String expected;

    @Test
    public void createAuthTest() {
        String actual = dao.createAuth("username");

        Assertions.assertNotNull(actual);
        Assertions.assertInstanceOf(String.class, actual);
    }


}
