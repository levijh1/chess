package server.response;

import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class ListGamesResponse extends ParentResponse {
    private ArrayList<GameData> gameList;

    public ListGamesResponse(ArrayList<GameData> gameList) {
        this.gameList = gameList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListGamesResponse that = (ListGamesResponse) o;
        return Objects.equals(gameList, that.gameList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameList);
    }

    @Override
    public String toString() {
        return "ListGamesResponse{" +
                "gameList=" + gameList +
                '}';
    }
}
