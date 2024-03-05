package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GameDaoTests {
    GameDao dao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dao = new GameDao();
        dao.clearGames();
    }

    @Test
    public void createGameTest() throws DataAccessException {
        int gameId = dao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());

        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(new GameData(gameId, "whiteTest", "blackTest", "Game1", new ChessGame()));

        List<Object> actual = GameDao.getGames();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getGameTest() throws DataAccessException {
        int gameId = dao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());

        GameData expected = new GameData(gameId, "whiteTest", "blackTest", "Game1", new ChessGame());
        GameData actual = dao.getGameData(gameId);

        Assertions.assertEquals(expected, actual);
    }
}

