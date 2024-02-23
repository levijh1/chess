package server.response;

import java.util.Objects;

public class ErrorResponse extends ParentResponse {
    private final String errorMessage;

    public ErrorResponse(String errorMessage, int statusCode) {
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return statusCode == that.statusCode && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorMessage, statusCode);
    }

    @Override
    public String toString() {
        return "genericResponse{" +
                "errorMessage='" + errorMessage + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
