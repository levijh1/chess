package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.UserDao;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class UserDaoTests {
    UserDao dao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dao = new UserDao();
        dao.clearUsers();
    }

    @Test
    public void createUserTest() throws DataAccessException {

        dao.createUser("testUsername", "testPassword", "testEmail");

        ArrayList<UserData> expected = new ArrayList<>();
        expected.add(new UserData("testUsername", "testPassword", "testEmail"));

        List<Object> actual = UserDao.getUsers();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getUserTest() throws DataAccessException {
        dao.createUser("testUsername", "testPassword", "testEmail");

        UserData expected = new UserData("testUsername", "testPassword", "testEmail");

        UserData actual = dao.getUser("testUsername");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getUserFromLargerListTest() throws DataAccessException {
        dao.createUser("testUsername", "testPassword", "testEmail");
        dao.createUser("testUsername2", "testPassword2", "testEmail2");
        dao.createUser("testUsername3", "testPassword3", "testEmail3");
        dao.createUser("testUsername4", "testPassword4", "testEmail4");

        UserData expected = new UserData("testUsername3", "testPassword3", "testEmail3");
        UserData actual = dao.getUser("testUsername3");

        Assertions.assertEquals(expected, actual);
    }
}
