package dataAccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class AuthTokenDao {
    private static final ArrayList<AuthData> authTokens = new ArrayList<>();


    public String createAuth(String username) throws DataAccessException {
        UUID tokenUUID = UUID.randomUUID();
        String authToken = tokenUUID.toString();

        Connection connection = DatabaseManager.getConnection();

        String sql = "INSERT INTO AuthTokens (username, authToken)" +
                "VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, authToken);

            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to update");
            }
        } catch (SQLException ex) {
            //error
        }

        return authToken;

//        UUID tokenUUID = UUID.randomUUID();
//        String authToken = tokenUUID.toString();
//
//        authTokens.add(new AuthData(username, authToken));
//        return authToken;
    }

    public String getAuth(String authToken) throws DataAccessException {
        for (AuthData authData : authTokens) {
            if (authData.getAuthToken().equals(authToken)) {
                return authData.getUsername();
            }
        }

        throw new DataAccessException("Auth token does not exist");
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        for (AuthData authData : authTokens) {
            if (authData.getAuthToken().equals(authToken)) {
                authTokens.remove(authData);
                return;
            }
        }

        throw new DataAccessException("Auth token does not exist");
    }

    public static ArrayList<AuthData> getAuthTokens() {
        return authTokens;
    }

    public void clearAuthTokens() {
        authTokens.clear();
    }
}
