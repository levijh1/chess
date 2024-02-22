package server.response;

public class parentResponse {
    private String errorMessage;
    private int statusCode;

    public parentResponse(String errorMessage, int statusCode) {
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }
}
