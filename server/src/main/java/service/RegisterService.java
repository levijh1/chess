package service;

import dataAccess.*;
import model.UserData;
import server.request.RegisterRequest;
import server.response.RegisterAndLoginResponse;

public class RegisterService {
    public RegisterAndLoginResponse register(RegisterRequest r) {
        String password = r.getPassword();
        String username = r.getUsername();
        String email = r.getEmail();

        if (password == null || username == null || email == null) {
            return new RegisterAndLoginResponse("Error: bad request", 400);
        }

        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();


        UserData userData = userDao.getUser(username); //verify that user doesn't already exist
        if (userData != null) {
            return new RegisterAndLoginResponse("Error: already taken", 403);
        }

        userDao.createUser(username, password, email);
        String generatedToken = authTokenDao.createAuth(username);

        return new RegisterAndLoginResponse(username, generatedToken);
    }
}
