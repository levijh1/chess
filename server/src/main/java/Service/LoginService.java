package Service;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.PasswordHasher;
import dataAccess.UserDao;
import server.request.LoginRequest;
import server.response.ErrorResponse;
import server.response.RegisterAndLoginResponse;
import server.response.ParentResponse;

public class LoginService {
    public ParentResponse login(LoginRequest r) throws DataAccessException {
        String username = r.username();
        String password = r.password();


        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        try {
            userDao.getUser(username); //verify that user doesn't already exist
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        // Check that password matches
        PasswordHasher passwordHasher = new PasswordHasher();
        if (!passwordHasher.verifyPassword(username, password)) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        String generatedToken = authTokenDao.createAuth(username);

        return new RegisterAndLoginResponse(username, generatedToken);
    }
}
