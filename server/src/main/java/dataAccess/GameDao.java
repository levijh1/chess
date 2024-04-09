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

    public String getPlayerName(int gameId, PlayerColor playerColor) throws DataAccessException {
        String sql;
        if (playerColor == PlayerColor.WHITE) {
            sql = "SELECT whiteUsername FROM Games WHERE gameId = ?";
            return executeQueryPlayerName(sql, "whiteUsername", gameId);
        }
        if (playerColor == PlayerColor.BLACK) {
            sql = "SELECT blackUsername FROM Games WHERE gameId = ?";
            return executeQueryPlayerName(sql, "blackUsername", gameId);
        }
        return null;
    }

    public void updateGamePlayers(int gameId, String username, PlayerColor playerColor) throws DataAccessException {
        String sql = null;
        String databasePlayer = null;
        if (playerColor == PlayerColor.WHITE) {
            //TODO: substitute with getPlayerName to make code less repetitive
            sql = "SELECT whiteUsername FROM Games WHERE gameId = ?";
            databasePlayer = executeQueryPlayerName(sql, "whiteUsername", gameId);
            sql = "UPDATE Games SET whiteUsername = ? WHERE gameId = ?";
        }
        if (playerColor == PlayerColor.BLACK) {
            sql = "SELECT blackUsername FROM Games WHERE gameId = ?";
            databasePlayer = executeQueryPlayerName(sql, "blackUsername", gameId);
            sql = "UPDATE Games SET blackUsername = ? WHERE gameId = ?";
        }

        if (databasePlayer == null) {
            executeUpdate(sql, username, gameId);
        } else {
            throw new DataAccessException("Username already occupied in game");
        }
    }

    public static void removePlayer(int gameId, String userName) throws DataAccessException {
        String sql;
        List<GameData> resultList;

        sql = "UPDATE Games SET whiteUsername = ? WHERE gameId = ? AND whiteUsername = ?";
        executeUpdate(sql, null, gameId, userName);

        sql = "UPDATE Games SET whiteUsername = ? WHERE gameId = ? AND blackUsername = ?";
        executeUpdate(sql, null, gameId, userName);
    }

    public void updateGame(int gameId, ChessGame game) throws DataAccessException {
        String sql;
        sql = "UPDATE Games SET game = ? WHERE gameId = ?";

        String jsonGame = new Gson().toJson(game);
        executeUpdate(sql, jsonGame, gameId);
    }
}
