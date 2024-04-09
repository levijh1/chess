package dataAccess;

import java.util.List;
import java.util.UUID;

import static dataAccess.DatabaseManager.*;

public class AuthTokenDao {
    public String createAuth(String username) throws DataAccessException {
        UUID tokenUUID = UUID.randomUUID();
        String authToken = tokenUUID.toString();

        String sql = "INSERT INTO AuthTokens (username, authToken)" +
                "VALUES (?, ?)";

        executeUpdate(sql, username, authToken);

        return authToken;
    }

    public String getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM AuthTokens WHERE authToken = ?";
        try {
            return (String) executeQueryPlayerName(sql, "username", authToken);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM AuthTokens WHERE authToken = ?";
        executeUpdate(sql, authToken);
    }

    public static List<Object> getAuthTokens() throws DataAccessException {
        String sql = "SELECT * FROM AuthTokens";
        return executeQuery(sql, "AuthData");
    }

    public void clearAuthTokens() throws DataAccessException {
        String sql = "DELETE FROM AuthTokens";
        executeUpdate(sql);
    }
}
