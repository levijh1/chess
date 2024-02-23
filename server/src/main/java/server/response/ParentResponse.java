package server.response;

public class ParentResponse {
    transient int statusCode = 200;

    public int getStatusCode() {
        return statusCode;
    }
}
