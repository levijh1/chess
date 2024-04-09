package server;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.*;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import server.request.PlayerColor;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;


@WebSocket
public class WebsocketHandler {
    private static GameSessionDictionary gameSessions = new GameSessionDictionary();

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        UserGameCommand command = readJson(msg);

        String userName = getConnection(command.getAuthString(), session);
        if (userName != null) {
            switch (command.getCommandType()) {
                case LEAVE -> leave(userName, (LeaveCommand) command, session);
                case JOIN_PLAYER -> joinPlayer(userName, (JoinPlayerCommand) command, session);
                case JOIN_OBSERVER -> joinObserver(userName, (JoinObserverCommand) command, session);
                case MAKE_MOVE -> makeMove(userName, (MakeMoveCommand) command, session);
                case RESIGN -> resign(userName, (ResignCommand) command, session);
            }
        } else {
            sendResponse(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "unauthorized"));
        }
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
                case JOIN_OBSERVER -> context.deserialize(jsonElement, JoinObserverCommand.class);
                case MAKE_MOVE -> context.deserialize(jsonElement, MakeMoveCommand.class);
                case LEAVE -> context.deserialize(jsonElement, LeaveCommand.class);
                case RESIGN -> context.deserialize(jsonElement, ResignCommand.class);
            };
        }
    }

    public void leave(String userName, LeaveCommand command, Session session) throws DataAccessException, IOException {
        ServerMessage message;
        int gameId = command.getGameID();

        gameSessions.removeSessionFromGame(gameId, session);


        try {
            GameDao.removePlayer(gameId, userName);
            message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, userName + " has left the game");

            for (Session otherSession : gameSessions.getSessionsForGame(gameId)) {
                if (otherSession != session) {
                    sendResponse(otherSession, message);
                }
            }
        } catch (Exception ex) {
            message = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Bad leave");
            ex.printStackTrace();
            sendResponse(session, message);
        }
    }

    private void joinPlayer(String userName, JoinPlayerCommand command, Session session) throws IOException, DataAccessException {
        GameDao gameDao = new GameDao();

        int gameId = command.getGameID();
        ChessGame.TeamColor teamColor = command.getTeamColor();
        PlayerColor playerColor = null;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            playerColor = PlayerColor.WHITE;
        }
        if (teamColor == ChessGame.TeamColor.BLACK) {
            playerColor = PlayerColor.BLACK;
        }

        if (!Objects.equals(gameDao.getPlayerName(gameId, playerColor), userName)) {
            sendResponse(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Join over http was not successful"));
            System.out.print("Error in join player");
            return;
        }

        gameSessions.addSessionToGame(gameId, session);

        for (Session otherSession : gameSessions.getSessionsForGame(gameId)) {
            if (otherSession != session) {
                String message = userName + " has joined this game as the " + teamColor.toString() + " player";
                sendResponse(otherSession, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
            }
        }

        ChessBoard board = gameDao.getGameData(gameId).getGame().getBoard();
        sendResponse(session, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, board));
    }

    private void joinObserver(String userName, JoinObserverCommand command, Session session) throws IOException, DataAccessException {
        GameDao gameDao = new GameDao();
        int gameId = command.getGameID();

        try {
            gameSessions.addSessionToGame(gameId, session);

            for (Session otherSession : gameSessions.getSessionsForGame(gameId)) {
                if (otherSession != session) {
                    String message = userName + " has joined this game as an observer";
                    sendResponse(otherSession, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
                }
            }

            ChessBoard board = gameDao.getGameData(gameId).getGame().getBoard();
            sendResponse(session, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, board));
        } catch (Exception ex) {
            sendResponse(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error in observing game. Possibly an invalid gameID"));
        }
    }

    private void resign(String userName, ResignCommand command, Session session) throws DataAccessException, IOException {
        int gameId = command.getGameID();
        GameDao gameDao = new GameDao();

        GameData gameData = gameDao.getGameData(gameId);
        ChessGame game = gameData.getGame();
        if (Objects.equals(game.getStatus(), "over")) {
            sendResponse(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "You cannot resign after game is already over"));
            return;
        }

        if (Objects.equals(userName, gameDao.getPlayerName(gameId, PlayerColor.WHITE)) || Objects.equals(userName, gameDao.getPlayerName(gameId, PlayerColor.BLACK))) {
            game.setStatus("over");
            gameDao.updateGame(gameId, game);

            for (Session sessionInGame : gameSessions.getSessionsForGame(gameId)) {
                String message = userName + " has resigned from the game. Game is over";
                sendResponse(sessionInGame, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
            }
        } else {
            sendResponse(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Observer may not resign"));
        }
    }

    private void makeMove(String userName, MakeMoveCommand command, Session session) throws DataAccessException, IOException, InvalidMoveException {
        int gameId = command.getGameID();
        ChessMove move = command.getMove();
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        GameDao gameDao = new GameDao();

        GameData gameData = gameDao.getGameData(gameId);
        ChessGame game = gameData.getGame();

        //Check if it is actually the turn of the person making the move
        String playerColor = game.getTeamTurn().toString();
        if (!Objects.equals(userName, gameDao.getPlayerName(gameId, PlayerColor.valueOf(playerColor.toUpperCase())))) {
            sendResponse(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "It is not the turn of the player making the move"));
            return;
        }

        if (!game.isMoveValid(move)) {
            sendResponse(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Move is not valid"));
            System.out.print("Error in make Move");
            return;
        } else {
            game.makeMove(move);
            gameDao.updateGame(gameId, game);
        }

        ChessBoard board = gameDao.getGameData(gameId).getGame().getBoard();
        for (Session sessionInGame : gameSessions.getSessionsForGame(gameId)) {
            sendResponse(sessionInGame, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, board));
        }

        for (Session otherSession : gameSessions.getSessionsForGame(gameId)) {
            if (otherSession != session) {
                String message = userName + " moved " + startPosition.positionToString() + " to " + endPosition.positionToString();
                sendResponse(otherSession, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
            }
        }

        if (game.currentPlayerInCheck()) {
            String message = game.getTeamTurn().toString() + " player is in check";
            for (Session sessionInGame : gameSessions.getSessionsForGame(gameId)) {
                sendResponse(sessionInGame, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
            }
        }

        if (game.currentPlayerInCheckmate()) {
            String message = game.getTeamTurn().toString() + " player is in checkmate. " + userName + " wins the game!";
            for (Session sessionInGame : gameSessions.getSessionsForGame(gameId)) {
                sendResponse(sessionInGame, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
            }
            game.setStatus("over");
        }
    }
}