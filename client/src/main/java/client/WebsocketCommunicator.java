package client;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WebsocketCommunicator {

    public static void main(String[] args) throws Exception {
        var ws = new WebsocketCommunicator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a message you want to echo");
        while (true) {
            ws.send(scanner.nextLine());
        }
    }

    public Session session;

    public WebsocketCommunicator() throws Exception {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    //TODO: Fix this to retrieve server messages from websocket
    public void onMessage(String message) {
        try {
            ServerMessage message =
                    gson.fromJson(message, ServerMessage.class);
            observer.notify(message);
        } catch (Exception ex) {
            observer.notify(new ErrorMessage(ex.getMessage()));
        }
    }

}
