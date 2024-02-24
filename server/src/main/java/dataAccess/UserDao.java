package dataAccess;

import model.UserData;

import java.util.ArrayList;

public class UserDao {
    static private final ArrayList<UserData> Users = new ArrayList<>();

    public void createUser(String username, String password, String email) {
        Users.add(new UserData(username, password, email));
    }

    public UserData getUser(String username) throws DataAccessException {
        for (UserData userData : Users) {
            if (userData.getUsername().equals(username)) {
                return userData;
            }
        }

        throw new DataAccessException("User does not exist");
    }

    public static ArrayList<UserData> getUsers() {
        return Users;
    }

    public void clearUsers() {
        Users.clear();
    }
}
