package service;

import dataAccess.*;
import model.UserData;
import server.request.RegisterRequest;
import server.response.ErrorResponse;
import server.response.RegisterAndLoginResponse;
import server.response.ParentResponse;

public class RegisterService {
    public ParentResponse register(RegisterRequest r) {
        String password = r.password();
        String username = r.username();
        String email = r.email();

        if (password == null || username == null || email == null) {
            return new ErrorResponse("Error: bad request", 400);
        }

        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();


        try {
            userDao.getUser(username); //verify that user doesn't already exist
        } catch (DataAccessException ex) {
            userDao.createUser(username, password, email);
            String generatedToken = authTokenDao.createAuth(username);
            return new RegisterAndLoginResponse(username, generatedToken);
        }

        return new ErrorResponse("Error: already taken", 403);


    }
}
