package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public String getMessage() {
        return message;
    }

    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }
}
