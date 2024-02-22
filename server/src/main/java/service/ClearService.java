package service;

import dataAccess.AuthTokenDao;
import dataAccess.GameDao;
import dataAccess.UserDao;

public class ClearService {
    public void clear() {
        UserDao userDao = new UserDao();
        GameDao gameDao = new GameDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        userDao.clearUsers();
        gameDao.clearGames();
        authTokenDao.clearAuthTokens();
    }
}
