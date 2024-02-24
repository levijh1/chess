package dataAccess;

import chess.ChessGame;
import model.GameData;
import server.request.PlayerColor;

import java.util.ArrayList;
import java.util.Objects;

public class GameDao {
    private static final ArrayList<GameData> games = new ArrayList<>();
    private static int gameIdCounter = 1;

    public int createGame(String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        int gameID = gameIdCounter;
        gameIdCounter += 1;
        games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
        return gameID;
    }

    public GameData getGameData(int gameID) throws DataAccessException {
        for (GameData gameData : games) {
            if (gameData.getGameID() == gameID) {
                return gameData;
            }
        }

        throw new DataAccessException("Game does not exist");
    }

    public static ArrayList<GameData> getGames() {
        return games;
    }

    public void clearGames() {
        games.clear();
        gameIdCounter = 1;
    }

    public void updateGame(int gameID, String username, PlayerColor playerColor) throws DataAccessException {
        for (int i = 0; i < games.size(); i++) {
            GameData oldgameData = games.get(i);
            if (oldgameData.getGameID() == gameID) {
                if (Objects.equals(playerColor, PlayerColor.WHITE)) {
                    if (oldgameData.getWhiteUsername() != null) {
                        throw new DataAccessException("playerColor is already taken");
                    }
                    games.set(i, new GameData(oldgameData.gameID(), username, oldgameData.blackUsername(), oldgameData.gameName(), oldgameData.game()));
                }
                if (Objects.equals(playerColor, PlayerColor.BLACK)) {
                    if (oldgameData.getBlackUsername() != null) {
                        throw new DataAccessException("playerColor is already taken");
                    }
                    games.set(i, new GameData(oldgameData.gameID(), oldgameData.whiteUsername(), username, oldgameData.gameName(), oldgameData.game()));
                }
                return;
            }
        }

    }
}
