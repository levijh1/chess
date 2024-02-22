package service;

import dataAccess.AuthTokenDao;
import dataAccess.UserDao;
import model.UserData;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.response.RegisterAndLoginResponse;

import java.util.Objects;

public class LoginService {
    public RegisterAndLoginResponse login(LoginRequest r) {
        String username = r.getUsername();
        String password = r.getPassword();


        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        UserData userData = userDao.getUser(username); //verify that user doesn't already exist
        if (userData == null) {
            return new RegisterAndLoginResponse("Error: user doesn't exist", 500);
        }
        if (!Objects.equals(userData.getPassword(), password)) {
            return new RegisterAndLoginResponse("Error: unauthorized", 401);
        }

        String generatedToken = authTokenDao.createAuth(username);

        return new RegisterAndLoginResponse(username, generatedToken);
    }
}
