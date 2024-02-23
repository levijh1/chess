package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class GameDao {
    private static ArrayList<GameData> games = new ArrayList<>();
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

    public void updateGame() {

    }
}
