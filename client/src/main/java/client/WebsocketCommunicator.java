package client;

import com.google.gson.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class WebsocketCommunicator extends Endpoint {

    public static void main(String[] args) throws Exception {
        var ws = new WebsocketCommunicator(new ServerFacade(8080));
        Scanner scanner = new Scanner(System.in);

//        System.out.println("Enter a message you want to echo");
//        while (true) {
//            ws.send(scanner.nextLine());
//        }
    }

    public Session session;

    public WebsocketCommunicator(ServerMessageObserver observer) {
        try {
            URI uri = new URI("ws://localhost:8080/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String jsonMessage) {
                    try {
                        GsonBuilder builder = new GsonBuilder();
                        builder.registerTypeAdapter(ServerMessage.class, new ServerMessageDeserializer());
                        Gson gson = builder.create();

                        ServerMessage message = gson.fromJson(jsonMessage, ServerMessage.class);
                        observer.notify(message);
                    } catch (Exception ex) {
                        observer.notify(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage()));
                    }
                }
            });
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static class ServerMessageDeserializer implements JsonDeserializer<ServerMessage> {
        @Override
        public ServerMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String typeString = jsonObject.get("ServerMessageType").getAsString();
            ServerMessage.ServerMessageType serverMessageType = ServerMessage.ServerMessageType.valueOf(typeString);

            return switch (serverMessageType) {
                case ERROR -> context.deserialize(jsonElement, ErrorMessage.class);
                case NOTIFICATION -> context.deserialize(jsonElement, NotificationMessage.class);
                case LOAD_GAME -> context.deserialize(jsonElement, LoadGameMessage.class);
            };
        }
    }

    public void send(UserGameCommand command) throws Exception {
        String jsonCommand = new Gson().toJson(command);
        this.session.getBasicRemote().sendText(jsonCommand);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

}
