package server;

import com.google.gson.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.userCommands.LeaveCommand;
import websocketService.LeaveService;

import java.io.IOException;
import java.lang.reflect.Type;


@WebSocket
public class WebsocketServer {
    public static void main(String[] args) {
        Spark.port(8080);
        Spark.webSocket("/connect", WebsocketServer.class);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    //TODO: Make methods for each type of request
    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
//        session.getRemote().sendString("WebSock response: " + msg);
        UserGameCommand command = readJson(msg);

        String userName = getConnection(command.getAuthString(), session);
        if (userName != null) {
            switch (command.getCommandType()) {
//                case JOIN_PLAYER -> join(userName, command);
//                case JOIN_OBSERVER -> observe(userName, command);
//                case MAKE_MOVE -> move(userName, command));
                case LEAVE -> leave(userName, (LeaveCommand) command);
//                case RESIGN -> resign(userName, command);

                case JOIN_PLAYER -> {
                }
                case JOIN_OBSERVER -> {
                }
                case MAKE_MOVE -> {
                }
                case RESIGN -> {
                }
            }
        } else {
            sendResponse(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "unknown user"));
        }

        //TODO: add authToken and session to map
    }

    private void sendResponse(Session session, ServerMessage messageObject) throws IOException {
        String jsonString = new Gson().toJson(messageObject);
        session.getRemote().sendString(jsonString);
    }

    private String getConnection(String authToken, Session session) {
        //TODO:Find the corresponding username and verify that it is valid
        //This might already be in the other server
        return null;
    }

    private static UserGameCommand readJson(String jsonCommand) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(UserGameCommand.class, new UserGameCommandDeserializer());
        Gson gson = builder.create();

        UserGameCommand gameCommand = gson.fromJson(jsonCommand, UserGameCommand.class);
        return gameCommand;
    }

    private static class UserGameCommandDeserializer implements JsonDeserializer<UserGameCommand> {
        @Override
        public UserGameCommand deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String typeString = jsonObject.get("CommandType").getAsString();
            UserGameCommand.CommandType userGameCommandType = UserGameCommand.CommandType.valueOf(typeString);

            return switch (userGameCommandType) {
                case JOIN_PLAYER -> null;
                case JOIN_OBSERVER -> null;
                case MAKE_MOVE -> null;
                case LEAVE -> context.deserialize(jsonElement, LeaveCommand.class);
                case RESIGN -> null;
            };
        }
    }

    private void leave(String conn, LeaveCommand command) {
        new LeaveService();

    }

}