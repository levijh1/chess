package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthTokenDao {
    private static ArrayList<AuthData> authTokens = new ArrayList<>();


    public String createAuth(String username) {
        UUID tokenUUID = UUID.randomUUID();
        String authToken = tokenUUID.toString();

        authTokens.add(new AuthData(username, authToken));
        return authToken;
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

    //TODO: If you make some sort of way to access tokens, make sure to use DataAccessException

    public static ArrayList<AuthData> getAuthTokens() {
        return authTokens;
    }

    public void clearAuthTokens() {
        authTokens.clear();
    }
}
