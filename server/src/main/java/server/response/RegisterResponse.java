package server.response;

public class RegisterResponse {
    private String username;
    private String authToken;
    private String errorMessage;
    private int statusCode;

    public RegisterResponse(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
        this.errorMessage = null;
        this.statusCode = 200;
    }

    public RegisterResponse(String errorMessage, int statusCode) {
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
        this.username = null;
        this.authToken = null;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
