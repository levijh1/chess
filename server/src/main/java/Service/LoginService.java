package Service;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.PasswordHasher;
import dataAccess.UserDao;
import request.LoginRequest;
import response.ErrorResponse;
import response.RegisterAndLoginResponse;
import response.ParentResponse;

public class LoginService {
    public ParentResponse login(LoginRequest r) throws DataAccessException {
        String username = r.username();
        String password = r.password();


        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        try {
            if (userDao.getUser(username) == null) {
                return new ErrorResponse("Error: unauthorized", 401);
            }
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
