package service;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import server.request.GenericRequest;
import server.response.ErrorResponse;
import server.response.ParentResponse;

public class LogoutService {
    public ParentResponse logout(GenericRequest r, String authToken) {
        AuthTokenDao authTokenDao = new AuthTokenDao();

        //verify that they are valid user
        try {
            authTokenDao.getAuth(authToken);
            authTokenDao.deleteAuth(authToken);
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        return new ParentResponse();
    }
}
