package server.response;

import java.util.Objects;

public class RegisterResponse {
    //TODO: If this all works, try making a parent class to do the error stuff
    private String username = null;
    private String authToken = null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterResponse that = (RegisterResponse) o;
        return statusCode == that.statusCode && Objects.equals(username, that.username) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authToken, errorMessage, statusCode);
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "username='" + username + '\'' +
                ", authToken='" + authToken + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
