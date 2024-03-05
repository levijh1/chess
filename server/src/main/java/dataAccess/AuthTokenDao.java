package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dataAccess.DatabaseManager.executeQuery;
import static dataAccess.DatabaseManager.executeUpdate;

public class AuthTokenDao {
    private static final ArrayList<AuthData> authTokens = new ArrayList<>();


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
        List<Object> resultList = executeQuery(sql, "username", String.class, authToken);
        try {
            return (String) resultList.getFirst();
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
        return executeQuery(sql, "authToken", AuthData.class);
    }

    public void clearAuthTokens() throws DataAccessException {
        String sql = "DELETE FROM AuthTokens";
        executeUpdate(sql);
    }
}
