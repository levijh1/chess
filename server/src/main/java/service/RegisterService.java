package service;

import dataAccess.UserDao;
import model.AuthData;
import server.request.RegisterRequest;
import server.response.RegisterResponse;

public class RegisterService {
    public RegisterResponse register(RegisterRequest r) {
        String password = r.getPassword();
        String username = r.getUsername();
        String email = r.getEmail();

        UserDao userAccess = new UserDao();

        userAccess.getUser(username); //verify that user doesn't already exist
        userAccess.createUser(username, password, email);
        String generatedToken = userAccess.createAuth(username);

        return new RegisterResponse(username, generatedToken);
    }
}
