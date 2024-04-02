package client;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class WebsocketCommunicator extends Endpoint {

    public static void main(String[] args) throws Exception {
        var ws = new WebsocketCommunicator(new ServerFacade(8080));
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a message you want to echo");
        while (true) {
            ws.send(scanner.nextLine());
        }
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
                        Gson gson = null;
                        ServerMessage message =
                                gson.fromJson(jsonMessage, ServerMessage.class);
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

    //TODO: Create type adapter

    public void send(String msg) throws Exception {
        //TODO: send JSON of User command instead
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

}
