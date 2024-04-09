package Service;

import dataAccess.*;
import request.RegisterRequest;
import response.ErrorResponse;
import response.RegisterAndLoginResponse;
import response.ParentResponse;

public class RegisterService {
    public ParentResponse register(RegisterRequest r) throws DataAccessException {
        String password = r.password();
        String username = r.username();
        String email = r.email();

        if (password == null || username == null || email == null) {
            return new ErrorResponse("Error: bad request", 400);
        }

        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        PasswordHasher passwordHasher = new PasswordHasher();
        String hashedPassword = passwordHasher.generateHashedPassword(password);


        try {
            userDao.getUser(username); //verify that user doesn't already exist
        } catch (DataAccessException ex) {
            userDao.createUser(username, hashedPassword, email);
            String generatedToken = authTokenDao.createAuth(username);
            return new RegisterAndLoginResponse(username, generatedToken);
        }

        return new ErrorResponse("Error: already taken", 403);


    }
}
