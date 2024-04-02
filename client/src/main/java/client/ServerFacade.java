package client;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import server.request.*;
import server.response.CreateGameResponse;
import server.response.ListGamesResponse;
import server.response.ParentResponse;
import server.response.RegisterAndLoginResponse;
import ui.DrawBoard;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.ServerMessage;


import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class ServerFacade implements ServerMessageObserver {
    private final HttpCommunicator httpCommunicator = new HttpCommunicator();
    private final WebsocketCommunicator websocketCommunicator = new WebsocketCommunicator(this);
    private final String serverUrl;
    private String authToken = null;
    boolean gameJoined = false;
    HashMap<Integer, Integer> mostRecentGameNumbers = new HashMap<>();


    private int enteredGameId;
    private String loggedInUsername;
    private PlayerColor currentColor = PlayerColor.WHITE;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        //TODO: Make three switch statements that are separated by if statements (one for each menu)
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
            case "move" -> movePiece();
            case "resign" -> resign();
            case "highlight" -> highlightPossibleMoves(params[0]);
            case "quit" -> "quit";
            default -> help();
        };
    }

    //    //TODO: Fix this and create methods that can deal with this
    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    private void displayNotification(String message) {
        System.out.println(message);
    }

    private void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

    private void loadGame(ChessBoard gameBoard) {
        DrawBoard.drawBoard(gameBoard, currentColor, null, null);
    }

    public String help() {
        if (authToken == null) {
            String preLoginMenu = """
                    \tregister <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    \tlogin <USERNAME> <PASSWORD> - to play chess
                    \tquit - playing chess
                    \thelp - with possible commands
                    """;
            System.out.print(preLoginMenu);
            return preLoginMenu;
        } else if (gameJoined) {
            String gameplayMenu = """
                    \tredraw - show the chess board again
                    \tleave - remove user from the game
                    \tmove <MOVE> - move piece on the board
                    \tresign - forfeit and end the game
                    \thighlight <PIECE LOCATION> - show all legal moves
                    \thelp - with possible commands
                    """;
            System.out.print(gameplayMenu);
            return gameplayMenu;
        } else {
            String postLoginMenu = """
                    \tcreate <GAME NAME> - create a game
                    \tlist - list all available games
                    \tjoin <GAME ID> [WHITE|BLACK|<empty>] - play in a game
                    \tobserve <GAME ID> - watch a game
                    \tlogout - when you are done
                    \tquit - playing chess
                    \thelp - with possible commands
                    """;
            System.out.print(postLoginMenu);
            return postLoginMenu;
        }
    }

    public String login(String username, String password) {
        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            ParentResponse response = httpCommunicator.sendRequest("POST", serverUrl + "/session", loginRequest, RegisterAndLoginResponse.class, authToken);

            try {
                this.authToken = response.getAuthToken();
                loggedInUsername = username;
                System.out.println("Successful login!");
            } catch (Exception ex) {
                System.out.println("login not successful. Check your password or make sure that username is already registered");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return this.authToken;
    }

    public String register(String username, String password, String email) {
        try {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            ParentResponse response = httpCommunicator.sendRequest("POST", serverUrl + "/user", registerRequest, RegisterAndLoginResponse.class, authToken);

            try {
                this.authToken = response.getAuthToken();
                loggedInUsername = username;
                System.out.println("Successful register!");
            } catch (Exception ex) {
                System.out.println("Register not successful.");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return this.authToken;
    }

    public String logout() {
        if (authToken == null) {
            System.out.println("You are already logged out");
        }
        try {
            GenericRequest genericRequest = new GenericRequest();
            try {
                httpCommunicator.sendRequest("DELETE", serverUrl + "/session", genericRequest, ParentResponse.class, authToken);
                this.authToken = null;
                System.out.println("Successful logout!");
            } catch (Exception ex) {
                System.out.println("logout not successful. Not already logged in");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return this.authToken;
    }

    public String createGame(String gameName) {
        try {
            CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
            ParentResponse response = httpCommunicator.sendRequest("POST", serverUrl + "/game", createGameRequest, CreateGameResponse.class, authToken);

            System.out.println("Game created successfully!");

            return String.valueOf(response.getGameID());
        } catch (Exception ex) {
            System.out.println("Game creation not successful");
            System.out.println(ex.getMessage());
            return null;
        }


    }

    public String listGames() {
        try {
            GenericRequest genericRequest = new GenericRequest();
            try {
                ParentResponse response = httpCommunicator.sendRequest("GET", serverUrl + "/game", genericRequest, ListGamesResponse.class, authToken);
                List<GameData> Games = response.getGames();
                int i = 0;
                mostRecentGameNumbers.clear();
                for (GameData game : Games) {
                    i += 1;
                    System.out.print(SET_TEXT_COLOR_BLUE);
                    System.out.println("\nGame ID: " + i);
                    mostRecentGameNumbers.put(i, game.getGameID());
                    System.out.println("Game Name: " + game.getGameName());
                    System.out.println("Black Team Username: " + game.getBlackUsername());
                    System.out.println("White Team Username: " + game.getWhiteUsername());
                }
                System.out.println("\nAll games listed");
                return mostRecentGameNumbers.toString();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    //TODO: have it use websocket as well to notify all players
    public String joinGame(String gameNumberString, String playerColor) {
        try {
            PlayerColor playerColorEnum;
            int gameNumber = Integer.parseInt(gameNumberString);
            int gameId = mostRecentGameNumbers.get(gameNumber);
            try {
                playerColorEnum = PlayerColor.valueOf(playerColor.toUpperCase());
            } catch (Exception ex) {
                playerColorEnum = null;
            }
            JoinGameRequest joinGameRequest = new JoinGameRequest(playerColorEnum, gameId);
            ParentResponse response = httpCommunicator.sendRequest("PUT", serverUrl + "/game", joinGameRequest, ParentResponse.class, authToken);


            try {
                System.out.println(response.getMessage());
            } catch (Exception ex1) {
                try {
                    enteredGameId = gameId;
                    currentColor = playerColorEnum;
                    GameData game = returnCurrentGame();
                    if (playerColorEnum == null) {
                        playerColorEnum = PlayerColor.WHITE;
                    }
                    DrawBoard.drawBoard(game.getGame().getBoard(), playerColorEnum, null, null);

                    System.out.print(SET_TEXT_COLOR_BLUE);
                    System.out.println("Game joined successfully!");
                    gameJoined = true;
                } catch (Exception ex2) {
                    return ex2.getMessage();
                }

                return "Success";
            }
        } catch (Exception ex) {
            System.out.println("Joining or Observing game not successful");
            System.out.println("Try listing games first or checking your input values");
            System.out.println(ex.getMessage());
        }

        return null;
    }

    private String redraw() {
        GameData game = returnCurrentGame();
        assert game != null;
        DrawBoard.drawBoard(game.getGame().getBoard(), currentColor, null, null);

        return null;
    }

    private String leave() {
        GameData game = returnCurrentGame();
        assert game != null;
        if (Objects.equals(game.getBlackUsername(), loggedInUsername)) {
            game.setBlackUsername(null);
        }
        if (Objects.equals(game.getWhiteUsername(), loggedInUsername)) {
            game.setWhiteUsername(null);
        }
        enteredGameId = -1;
        gameJoined = false;

        //TODO: Send Leave message over websocket
        return null;
    }

    private String movePiece() {

        //TODO: send movePiece message over websocket
        return null;
    }

    private String resign() {
        //TODO: send resign Message over Websocket
        return null;
    }

    private String highlightPossibleMoves(String pieceLocation) {
        //Parse string
        int column = pieceLocation.charAt(0) - 'a' + 1;
        int row = pieceLocation.charAt(1) - '1' + 1;
        ChessPosition piecePosition = new ChessPosition(row, column);

        try {
            GameData game = returnCurrentGame();
            assert game != null;
            Collection<ChessMove> possibleMoves = game.getGame().validMoves(piecePosition);

            //If there is no piece at that location
            if (possibleMoves == null) {
                DrawBoard.drawBoard(game.getGame().getBoard(), currentColor, null, null);
                return null;
            }

            ArrayList<ChessPosition> possibleEndLocations = new ArrayList<>();
            for (ChessMove move : possibleMoves) {
                possibleEndLocations.add(move.getEndPosition());
            }

            DrawBoard.drawBoard(game.getGame().getBoard(), currentColor, possibleEndLocations, piecePosition);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return null;
    }

    private GameData returnCurrentGame() {
        try {
            ParentResponse response;
            GenericRequest genericRequest = new GenericRequest();
            response = httpCommunicator.sendRequest("GET", serverUrl + "/game", genericRequest, ListGamesResponse.class, authToken);
            List<GameData> Games = response.getGames();
            mostRecentGameNumbers.clear();
            for (GameData game : Games) {
                if (game.getGameID() == enteredGameId) {
                    return game;
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}


