package response;

import java.util.Objects;

public class RegisterAndLoginResponse extends ParentResponse {
    private final String username;
    private final String authToken;

    public String getAuthToken() {
        return authToken;
    }

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
