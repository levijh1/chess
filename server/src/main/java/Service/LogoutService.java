package Service;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import request.GenericRequest;
import response.ErrorResponse;
import response.ParentResponse;

public class LogoutService {
    public ParentResponse logout(GenericRequest r, String authToken) {
        AuthTokenDao authTokenDao = new AuthTokenDao();

        //verify that they are valid user
        try {
            if (authTokenDao.getAuth(authToken) == null) {
                return new ErrorResponse("Error: unauthorized", 401);
            }
            authTokenDao.deleteAuth(authToken);
        } catch (DataAccessException ex) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        return new ParentResponse();
    }
}
