package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.PlayerColor;

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
    public void badCreateGameTest() throws DataAccessException {
        try {
            dao.createGame("whiteTest", "blackTest", null, new ChessGame());
        } catch (Exception ex) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void getGameDataTest() throws DataAccessException {
        int gameId = dao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());

        GameData expected = new GameData(gameId, "whiteTest", "blackTest", "Game1", new ChessGame());
        GameData actual = dao.getGameData(gameId);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void badGetGameDataTest() throws DataAccessException {
        int gameId = dao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());

        try {
            GameData actual = dao.getGameData(gameId + 1);
        } catch (Exception ex) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void getGamesTest() throws DataAccessException {
        int gameID1 = dao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());
        int gameID2 = dao.createGame("whiteTest", "blackTest", "Game2", new ChessGame());
        int gameID3 = dao.createGame("whiteTest", "blackTest", "Game3", new ChessGame());
        int gameID4 = dao.createGame("whiteTest", "blackTest", "Game4", new ChessGame());
        List<Object> actual = dao.getGames();

        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(new GameData(gameID1, "whiteTest", "blackTest", "Game1", new ChessGame()));
        expected.add(new GameData(gameID2, "whiteTest", "blackTest", "Game2", new ChessGame()));
        expected.add(new GameData(gameID3, "whiteTest", "blackTest", "Game3", new ChessGame()));
        expected.add(new GameData(gameID4, "whiteTest", "blackTest", "Game4", new ChessGame()));

        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void badGetGamesTest() throws DataAccessException {
        List<Object> actual = dao.getGames();

        Assertions.assertEquals(actual, new ArrayList<GameData>());
    }

    @Test
    public void clearGamesTest() throws DataAccessException {
        int gameID1 = dao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());
        int gameID2 = dao.createGame("whiteTest", "blackTest", "Game2", new ChessGame());
        int gameID3 = dao.createGame("whiteTest", "blackTest", "Game3", new ChessGame());
        int gameID4 = dao.createGame("whiteTest", "blackTest", "Game4", new ChessGame());
        dao.clearGames();
        List<Object> actual = dao.getGames();

        Assertions.assertEquals(actual, new ArrayList<GameData>());
    }

    @Test
    public void updateGameTest() throws DataAccessException {
        int gameID1 = dao.createGame(null, "blackTest", "Game1", new ChessGame());

        dao.updateGame(gameID1, "updatedUsername", PlayerColor.WHITE);
        GameData actual = dao.getGameData(gameID1);

        GameData expected = new GameData(gameID1, "updatedUsername", "blackTest", "Game1", new ChessGame());

        Assertions.assertEquals(actual, expected);
    }

    @Test
    //spot already taken
    public void badUpdateGameTest() throws DataAccessException {
        int gameID1 = dao.createGame("whiteTest", "blackTest", "Game1", new ChessGame());

        try {
            dao.updateGame(gameID1, "updatedUsername", PlayerColor.WHITE);
        } catch (Exception ex) {
            Assertions.assertTrue(true);
        }
    }
}

