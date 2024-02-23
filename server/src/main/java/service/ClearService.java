package service;

import dataAccess.AuthTokenDao;
import dataAccess.GameDao;
import dataAccess.UserDao;
import server.response.ParentResponse;

public class ClearService {
    public ParentResponse clear() {
        UserDao userDao = new UserDao();
        GameDao gameDao = new GameDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        userDao.clearUsers();
        gameDao.clearGames();
        authTokenDao.clearAuthTokens();

        return new ParentResponse();
    }
}
