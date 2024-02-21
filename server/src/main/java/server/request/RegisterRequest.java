package server.request;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
