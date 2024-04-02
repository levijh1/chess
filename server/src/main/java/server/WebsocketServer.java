package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import webSocketMessages.userCommands.UserGameCommand;


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
        session.getRemote().sendString("WebSock response: " + msg);
//        UserGameCommand command = readJson(msg);
//
//        var conn = getConnection(command.authToken, session);
//        if (conn != null) {
//            switch (command.commandType) {
//                case JOIN_PLAYER -> join(conn, msg);
//                case JOIN_OBSERVER -> observe(conn, msg);
//                case MAKE_MOVE -> move(conn, msg));
//                case LEAVE -> leave(conn, msg);
//                case RESIGN -> resign(conn, msg);
//
//                //session.getRemote().sendString("WebSocket response: " + message);
//            }
//        } else {
//            Connection.sendError(session.getRemote(), "unknown user");
//        }
    }

    private static UserGameCommand readJson(String request) {
        return new Gson().fromJson(request, UserGameCommand.class);
    }

}