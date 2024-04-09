package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public ErrorMessage(ServerMessage.ServerMessageType type, String message) {
        super(type);
        errorMessage = "Error: " + message;
    }
}
