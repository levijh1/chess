package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import server.request.PlayerColor;

import java.util.List;

import static dataAccess.DatabaseManager.*;

public class GameDao {
    public int createGame(String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {
        String sql = "INSERT INTO Games(whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String jsonGame = new Gson().toJson(game);
        return executeUpdate(sql, whiteUsername, blackUsername, gameName, jsonGame);
    }

    public GameData getGameData(int gameId) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE gameId = ?";
        List<GameData> resultList = executeQueryGameData(sql, "GameData", gameId);

        try {
            return resultList.getFirst();
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public static List<GameData> getGames() throws DataAccessException {
        String sql = "SELECT * FROM Games";
        return executeQueryGameData(sql, "GameData");
    }

    public void clearGames() throws DataAccessException {
        String sql = "DELETE FROM Games";
        executeUpdate(sql);
    }

    public void updateGame(int gameId, String username, PlayerColor playerColor) throws DataAccessException {
        String sql = null;
        List<GameData> resultList = null;
        if (playerColor == PlayerColor.WHITE) {
            sql = "SELECT whiteUsername FROM Games WHERE gameId = ?";
            resultList = executeQueryGameData(sql, "whiteUsername", gameId);
            sql = "UPDATE Games SET whiteUsername = ? WHERE gameId = ?";
        }
        if (playerColor == PlayerColor.BLACK) {
            sql = "SELECT blackUsername FROM Games WHERE gameId = ?";
            resultList = executeQueryGameData(sql, "blackUsername", gameId);
            sql = "UPDATE Games SET blackUsername = ? WHERE gameId = ?";
        }

        assert resultList != null;
        if (resultList.getFirst() == null) {
            executeUpdate(sql, username, gameId);
        } else {
            throw new DataAccessException("Username already occupied in game");
        }


    }
}
