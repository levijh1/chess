package dataAccess;

import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserDao {
    static ArrayList<UserData> Users = new ArrayList<>();

    public UserData getUser(String username) {
        for (UserData userData : Users) {
            if (userData.getUsername().equals(username)) {
                return userData;
            }
        }

        return null;
    }

    public void createUser(String username, String password, String email) {
        Users.add(new UserData(username, password, email));
    }
}
