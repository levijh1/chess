package response;

import model.GameData;

import java.util.List;
import java.util.Objects;

public class ListGamesResponse extends ParentResponse {
    private final List<GameData> games;

    public ListGamesResponse(List<GameData> games) {
        this.games = games;
    }

    public List<GameData> getGames() {
        return games;
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
