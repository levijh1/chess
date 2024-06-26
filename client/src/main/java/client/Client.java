package client;

import chess.*;
import ui.DrawBoard;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.ServerMessage;


import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class Client implements ServerMessageObserver {
    ServerFacade serverFacade;
    private ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;

    public Client(int port) {
        String serverUrl = "http://localhost:" + port;
        serverFacade = new ServerFacade(serverUrl, this);
    }

    public static void main(String[] args) {
        System.out.println("♕ 240 Chess client.Client: ");
        new Client(8080).run();
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(SET_BG_COLOR_BLACK);
        out.println("Welcome to 240 chess!");
        out.print(SET_TEXT_COLOR_BLUE);
        serverFacade.help();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt(out);
            String line = scanner.nextLine();

            try {
                out.print(SET_TEXT_COLOR_BLUE);
                result = eval(line);
                if (result == null) {
                    result = "";
                }
                if (!Objects.equals(result, "quit")) {
                    if (!line.equals("help")) {
                        out.print("\n");
                        serverFacade.help();
                    }
                }
            } catch (Throwable e) {
                var msg = e.toString();
                out.print(msg);
            }
        }
        out.println();
    }

    private void printPrompt(PrintStream out) {
        out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "login" -> login(params[0], params[1]);
            case "register" -> register(params[0], params[1], params[2]);
            case "logout" -> logout();
            case "create" -> createGame(params[0]);
            case "list" -> listGames();
            case "join" -> {
                if (params.length >= 2) {
                    yield joinGame(params[0], params[1]);
                } else {
                    yield joinGame(params[0], null);
                }
            }
            case "observe" -> joinGame(params[0], null);
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "move" -> {
                if (params.length >= 3) {
                    yield movePiece(params[0], params[1], params[2]);
                } else {
                    yield movePiece(params[0], params[1], null);
                }
            }
            case "resign" -> resign();
            case "highlight" -> highlightPossibleMoves(params[0]);
            case "quit" -> "quit";
            default -> help();
        };
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }

        printPrompt(System.out);
    }

    private void displayNotification(String message) {
        System.out.println(message);
    }

    private void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

    private void loadGame(ChessBoard gameBoard) {
        System.out.print("\n");
        DrawBoard.drawBoard(gameBoard, currentColor, null, null);
    }

    public String help() {
        return serverFacade.help();
    }

    public String login(String username, String password) {
        return serverFacade.login(username, password);
    }

    public String register(String username, String password, String email) {
        return serverFacade.register(username, password, email);
    }

    public String logout() {
        return serverFacade.logout();
    }

    public String createGame(String gameName) {
        return serverFacade.createGame(gameName);
    }

    public String listGames() {
        return serverFacade.listGames();
    }

    public String joinGame(String gameNumberString, String playerColor) {
        if (Objects.equals(playerColor, "black")) {
            currentColor = ChessGame.TeamColor.BLACK;
        }
        return serverFacade.joinGame(gameNumberString, playerColor);
    }

    public String redraw() {
        return serverFacade.redraw();
    }

    public String leave() {
        currentColor = ChessGame.TeamColor.WHITE;
        return serverFacade.leave();
    }

    public String movePiece(String startingPositionString, String endingPositionString, String promotionPiece) {
        return serverFacade.movePiece(startingPositionString, endingPositionString, promotionPiece);
    }

    public String resign() {
        return serverFacade.resign();
    }

    public String highlightPossibleMoves(String pieceLocation) {
        return serverFacade.highlightPossibleMoves(pieceLocation);
    }

}


