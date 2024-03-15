package server.response;

import java.util.Objects;

public class ParentResponse {
    transient int statusCode = 200;

    public int getStatusCode() {
        return statusCode;
    }

    public String getAuthToken() throws Exception {
        throw new Exception("No getAuthToken method declared for this class");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentResponse that = (ParentResponse) o;
        return statusCode == that.statusCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode);
    }

    @Override
    public String toString() {
        return "ParentResponse{" +
                "statusCode=" + statusCode +
                '}';
    }
}
