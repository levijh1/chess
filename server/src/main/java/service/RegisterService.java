package service;

import dataAccess.*;
import model.UserData;
import server.request.RegisterRequest;
import server.response.RegisterResponse;

public class RegisterService {
    public RegisterResponse register(RegisterRequest r) {
        String password = r.getPassword();
        String username = r.getUsername();
        String email = r.getEmail();

        UserDao userAccess = new UserDao();
        AuthTokenDao authAccess = new AuthTokenDao();


        UserData userData = userAccess.getUser(username); //verify that user doesn't already exist
        if (userData != null) {
            return new RegisterResponse("Error: already taken", 403);
        }

        userAccess.createUser(username, password, email);
        String generatedToken = authAccess.createAuth(username);

        return new RegisterResponse(username, generatedToken);
    }
}
