package server.response;

import java.util.Objects;

public class CreateGameResponse extends ParentResponse {
    private int gameID;

    public int getGameID() {
        return gameID;
    }

    public CreateGameResponse(int gameID) {
        this.gameID = gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateGameResponse that = (CreateGameResponse) o;
        return gameID == that.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID);
    }

    @Override
    public String toString() {
        return "CreateGameResponse{" +
                "gameID=" + gameID +
                '}';
    }
}
