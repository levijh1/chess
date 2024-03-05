package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import server.request.PlayerColor;

import java.util.List;

import static dataAccess.DatabaseManager.executeQuery;
import static dataAccess.DatabaseManager.executeUpdate;

public class GameDao {
    public int createGame(String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {
        String sql = "INSERT INTO Games(whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String jsonGame = new Gson().toJson(game);
        return executeUpdate(sql, whiteUsername, blackUsername, gameName, jsonGame);
    }

    public GameData getGameData(int gameId) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE gameId = ?";
        List<Object> resultList = executeQuery(sql, "GameData", gameId);

        try {
            return (GameData) resultList.getFirst();
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public static List<Object> getGames() throws DataAccessException {
        String sql = "SELECT * FROM Games";
        return executeQuery(sql, "GameData");
    }

    public void clearGames() throws DataAccessException {
        String sql = "DELETE FROM Games";
        executeUpdate(sql);
    }

    public void updateGame(int gameId, String username, PlayerColor playerColor) throws DataAccessException {
        String sql;
        List<Object> resultList;
        if (playerColor == PlayerColor.WHITE) {
            sql = "SELECT whiteUsername FROM Games WHERE gameId = ?";
            resultList = executeQuery(sql, "whiteUsername", gameId);
            sql = "UPDATE Games SET whiteUsername = ? WHERE gameId = ?";
        } else {
            sql = "SELECT blackUsername FROM Games WHERE gameId = ?";
            resultList = executeQuery(sql, "blackUsername", gameId);
            sql = "UPDATE Games SET blackUsername = ? WHERE gameId = ?";
        }

        if (resultList.get(0) != null) {
            throw new DataAccessException("Username already occupied in game");
        }

        executeUpdate(sql, username, gameId);
    }
}
