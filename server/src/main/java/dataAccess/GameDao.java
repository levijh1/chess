package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class GameDao {
    static ArrayList<GameData> games = new ArrayList<>();

    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
    }

    public GameData getGameData(int gameID) {
        for (GameData gameData : games) {
            if (gameData.getGameID() == gameID) {
                return gameData;
            }
        }

        return null;
    }

    public static ArrayList<GameData> getGames() {
        return games;
    }

    public void clearGames() {
        games.clear();
    }
}
