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
    public ParentResponse logout(GenericRequest r, String authToken) {
        String username;

        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        //verify that they are valid user
        try {
            username = authTokenDao.getAuth(authToken);
            authTokenDao.deleteAuth(authToken);
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        return new ParentResponse();
    }
}
