package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameDaoTests {
    GameDao dao;

    @BeforeEach
    public void setUp() {
        dao = new GameDao();
        dao.clearGames();
    }

    @Test
    public void createGameTest() {
        dao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());

        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(new GameData(1, "whiteTest", "blackTest", "Game1", new ChessGame()));

        ArrayList<GameData> actual = GameDao.getGames();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getGameTest() throws DataAccessException {
        dao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());

        GameData expected = new GameData(1, "whiteTest", "blackTest", "Game1", new ChessGame());
        GameData actual = dao.getGameData(1);

        Assertions.assertEquals(expected, actual);
    }
}

