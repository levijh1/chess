package dataAccess;

import model.UserData;

import java.util.ArrayList;
import java.util.List;

import static dataAccess.DatabaseManager.executeQuery;
import static dataAccess.DatabaseManager.executeUpdate;

public class UserDao {
    static private final ArrayList<UserData> Users = new ArrayList<>();

    public void createUser(String username, String password, String email) throws DataAccessException {
        String sql = "INSERT INTO Users(username, password, email) VALUES (?, ?, ?)";

        executeUpdate(sql, username, password, email);
//        Users.add(new UserData(username, password, email));
    }

    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        List<Object> resultList = executeQuery(sql, UserData.class, username);

        try {
            return (UserData) resultList.getFirst();
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
        //        for (UserData userData : Users) {
//            if (userData.getUsername().equals(username)) {
//                return userData;
//            }
//        }
//
//        throw new DataAccessException("User does not exist");
    }

    public static List<Object> getUsers() throws DataAccessException {
        String sql = "SELECT * FROM Users";
        return executeQuery(sql, UserData.class);
    }

    public void clearUsers() throws DataAccessException {
        String sql = "DELETE FROM Users";
        executeUpdate(sql);
    }
}
