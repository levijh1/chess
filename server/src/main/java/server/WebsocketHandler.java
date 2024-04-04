package server;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.*;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.userCommands.LeaveCommand;
import websocketService.LeaveService;

import java.io.IOException;
import java.lang.reflect.Type;


@WebSocket
public class WebsocketHandler {
    GameSessionDictionary gameSessions = new GameSessionDictionary();
    //TODO: Create a dictionary (gameID: list of sessions) Look at example in petshop (connectionManager)
    //loop through session.getRemote().sendString(jsonString);


    //TODO: Make methods for each type of request
    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        UserGameCommand command = readJson(msg);

        String userName = getConnection(command.getAuthString(), session);
        if (userName != null) {
            switch (command.getCommandType()) {
//                case JOIN_OBSERVER -> observe(userName, command);
//                case MAKE_MOVE -> move(userName, command));
                case LEAVE -> leave(userName, (LeaveCommand) command, session);
//                case RESIGN -> resign(userName, command);

                case JOIN_PLAYER -> joinPlayer(userName, (JoinPlayerCommand) command, session);
                case JOIN_OBSERVER -> {
                }
                case MAKE_MOVE -> {
                }
                case RESIGN -> {
                }
            }
        } else {
            sendResponse(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unauthorized"));
        }

        //TODO: add authToken and session to map
    }

    private void sendResponse(Session session, ServerMessage messageObject) throws IOException {
        String jsonString = new Gson().toJson(messageObject);
        session.getRemote().sendString(jsonString);
    }

    private String getConnection(String authToken, Session session) {
        AuthTokenDao authTokenDao = new AuthTokenDao();
        String username;

        try {
            username = authTokenDao.getAuth(authToken);
        } catch (DataAccessException ex) {
            username = null;
        }
        return username;
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

            String typeString = jsonObject.get("commandType").getAsString();
            UserGameCommand.CommandType userGameCommandType = UserGameCommand.CommandType.valueOf(typeString);

            return switch (userGameCommandType) {
                case JOIN_PLAYER -> context.deserialize(jsonElement, JoinPlayerCommand.class);
                case JOIN_OBSERVER -> null;
                case MAKE_MOVE -> null;
                case LEAVE -> context.deserialize(jsonElement, LeaveCommand.class);
                case RESIGN -> null;
            };
        }
    }

    public void leave(String userName, LeaveCommand command, Session session) throws DataAccessException, IOException {
        //TODO: If tests don't work make sure you are only leaving from one team at a time
        ServerMessage message;
        int gameId = command.getGameID();

        GameDao gameDao = new GameDao();

        try {
            GameDao.removePlayer(gameId, userName);
            message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, userName + " has left the game");

            for (Session otherSession : gameSessions.getSessionsForGame(gameId)) {
                if (otherSession != session) {
                    sendResponse(session, message);
                }
            }
        } catch (Exception ex) {
            message = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Bad leave");
            sendResponse(session, message);
        }
    }

    private void joinPlayer(String userName, JoinPlayerCommand command, Session session) throws IOException, DataAccessException {
        GameDao gameDao = new GameDao();

        int gameId = command.getGameID();
        ChessGame.TeamColor teamColor = command.getPlayerColor();

        gameSessions.addSessionToGame(gameId, session);

        for (Session otherSession : gameSessions.getSessionsForGame(gameId)) {
            if (otherSession != session) {
                String message = userName + " has joined this game as the " + teamColor.toString() + " player";
                sendResponse(session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
            }
        }

        ChessBoard board = gameDao.getGameData(gameId).getGame().getBoard();
        sendResponse(session, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, board));
    }

}