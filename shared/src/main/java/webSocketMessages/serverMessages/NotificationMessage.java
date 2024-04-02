package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {
    private String notificationMessage;

    public String getMessage() {
        return notificationMessage;
    }

    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        notificationMessage = message;
    }
}
