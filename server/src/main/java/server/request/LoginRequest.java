package server.request;

public class LoginRequest {
    private final String username;
    private final String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}