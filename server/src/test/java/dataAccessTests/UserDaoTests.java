package dataAccessTests;

import dataAccess.UserDao;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class UserDaoTests {
    UserDao dao;

    @BeforeEach
    public void setUp() {
        dao = new UserDao();
        dao.clearUsers();
    }

    @AfterEach
    public void cleanUp() {
        dao = new UserDao();
    }

    @Test
    public void createUserTest() {

        dao.createUser("testUsername", "testPassword", "testEmail");

        ArrayList<UserData> expected = new ArrayList<>();
        expected.add(new UserData("testUsername", "testPassword", "testEmail"));

        ArrayList<UserData> actual = dao.getUsers();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getUserTest() {
        dao.createUser("testUsername", "testPassword", "testEmail");

        UserData expected = new UserData("testUsername", "testPassword", "testEmail");
        UserData actual = dao.getUser("testUsername");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getUserFromLargerListTest() {
        dao.createUser("testUsername", "testPassword", "testEmail");
        dao.createUser("testUsername2", "testPassword2", "testEmail2");
        dao.createUser("testUsername3", "testPassword3", "testEmail3");
        dao.createUser("testUsernam4", "testPassword4", "testEmail4");

        UserData expected = new UserData("testUsername3", "testPassword3", "testEmail3");
        UserData actual = dao.getUser("testUsername3");

        Assertions.assertEquals(expected, actual);
    }
}
