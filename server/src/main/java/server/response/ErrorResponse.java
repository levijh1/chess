package server.response;

import java.util.Objects;

public class ErrorResponse extends ParentResponse {
    private final String message;

    public ErrorResponse(String errorMessage, int statusCode) {
        this.message = errorMessage;
        this.statusCode = statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return statusCode == that.statusCode && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, statusCode);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errorMessage='" + message + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }

    public String getErrorMessage() {
        return message;
    }
}
