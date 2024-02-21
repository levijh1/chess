package server.response;

public class RegisterResponse {
    private String username;
    private String authToken;

    public RegisterResponse(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }
}
