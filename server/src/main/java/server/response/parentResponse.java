package server.response;

public class parentResponse {
    private String errorMessage = null;
    private int statusCode = 200;

    public parentResponse(String errorMessage, int statusCode) {
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }
}
