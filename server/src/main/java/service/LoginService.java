package service;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.UserDao;
import model.UserData;
import server.request.LoginRequest;
import server.response.ErrorResponse;
import server.response.RegisterAndLoginResponse;
import server.response.ParentResponse;

import java.util.Objects;

public class LoginService {
    public ParentResponse login(LoginRequest r) throws DataAccessException {
        UserData userData;
        String username = r.username();
        String password = r.password();


        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        try {
            userData = userDao.getUser(username); //verify that user doesn't already exist
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        // Check that password matches
        if (!Objects.equals(userData.getPassword(), password)) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        String generatedToken = authTokenDao.createAuth(username);

        return new RegisterAndLoginResponse(username, generatedToken);
    }
}
