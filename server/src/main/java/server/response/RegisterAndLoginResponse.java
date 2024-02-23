package server.response;

import java.util.Objects;

public class RegisterAndLoginResponse implements ParentResponse {
    //TODO: If this all works, try making a parent class to do the error stuff
    private String username = null;
    private String authToken = null;

    public RegisterAndLoginResponse(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterAndLoginResponse that = (RegisterAndLoginResponse) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authToken);
    }

    @Override
    public String toString() {
        return "RegisterAndLoginResponse{" +
                "username='" + username + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
