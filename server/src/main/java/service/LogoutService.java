package service;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.UserDao;
import model.UserData;
import server.request.GenericRequest;
import server.request.LoginRequest;
import server.response.ErrorResponse;
import server.response.RegisterAndLoginResponse;
import server.response.ParentResponse;

import java.util.Objects;

public class LogoutService {
    public ParentResponse logout(GenericRequest r) {
        String authToken = r.authToken();
        String username;

        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        //verify that they are valid user
        try {
            username = authTokenDao.getAuth(authToken);
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: unathorized", 401);
        }

        try {
            authTokenDao.deleteAuth(authToken);
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: you are not already signed in", 500);
        }

        return new ParentResponse();
    }
}
