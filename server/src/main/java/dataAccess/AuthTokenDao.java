package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthTokenDao {
    static ArrayList<AuthData> authTokens = new ArrayList<>();


    public String createAuth(String username) {
        UUID tokenUUID = UUID.randomUUID();
        String authToken = tokenUUID.toString();

        authTokens.add(new AuthData(username, authToken));
        return authToken;
    }
}
