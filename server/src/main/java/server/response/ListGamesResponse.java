package server.response;

import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class ListGamesResponse extends ParentResponse {
    private ArrayList<GameData> games;

    public ListGamesResponse(ArrayList<GameData> games) {
        this.games = games;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListGamesResponse that = (ListGamesResponse) o;
        return Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(games);
    }

    @Override
    public String toString() {
        return "ListGamesResponse{" +
                "gameList=" + games +
                '}';
    }
}
