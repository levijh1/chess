package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import request.*;
import response.*;
import ui.DrawBoard;
import webSocketMessages.userCommands.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class ServerFacade {
    private final HttpCommunicator httpCommunicator;
    private WebsocketCommunicator websocketCommunicator;
    private final String serverUrl;
    private String authToken = null;
    boolean gameJoined = false;
    HashMap<Integer, Integer> mostRecentGameNumbers = new HashMap<>();
    String loggedInUsername;
    private int enteredGameId;
    private ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;
    Client observer;

    public ServerFacade(String serverUrl, Client observer) {
        this.serverUrl = serverUrl;
        this.observer = observer;
        httpCommunicator = new HttpCommunicator();
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
                    \tmove <startPosition> <endPosition> [promotionPieceType|empty]- move piece on the board (i.e. move c7 c5)
                    \tresign - forfeit and end the game
                    \thighlight <position> - show all legal moves (i.e. highlight f2)
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
        if (authToken == null) {
            System.out.println("Game creation not successful");
            return null;
        }
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
        if (authToken == null) {
            System.out.println("List games not successful");
            return null;
        }
        try {
            GenericRequest genericRequest = new GenericRequest();
            try {
                ParentResponse response = httpCommunicator.sendRequest("GET", serverUrl + "/game", genericRequest, ListGamesResponse.class, authToken);
                List<GameData> games = response.getGames();
                int i = 0;
                mostRecentGameNumbers.clear();
                for (GameData game : games) {
                    i += 1;
                    System.out.print(SET_TEXT_COLOR_BLUE);
                    System.out.println("\nGame ID: " + i);
                    mostRecentGameNumbers.put(i, game.getGameID());
                    System.out.println("Game Name: " + game.getGameName());
                    System.out.println("White Team Username: " + game.getWhiteUsername());
                    System.out.println("Black Team Username: " + game.getBlackUsername());
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

    public String joinGame(String gameNumberString, String playerColor) {
        try {
            ChessGame.TeamColor playerColorEnum;
            int gameNumber = Integer.parseInt(gameNumberString);
            int gameId = mostRecentGameNumbers.get(gameNumber);
            JoinGameRequest joinGameRequest;
            try {
                playerColorEnum = ChessGame.TeamColor.valueOf(playerColor.toUpperCase());
                joinGameRequest = new JoinGameRequest(PlayerColor.valueOf(playerColor.toUpperCase()), gameId);
            } catch (Exception ex) {
                playerColorEnum = null;
                joinGameRequest = new JoinGameRequest(null, gameId);
            }
            ParentResponse response = httpCommunicator.sendRequest("PUT", serverUrl + "/game", joinGameRequest, ParentResponse.class, authToken);


            try {
                System.out.println(response.getMessage());
            } catch (Exception ex1) {
                enteredGameId = gameId;
                currentColor = playerColorEnum;

                System.out.print(SET_TEXT_COLOR_BLUE);
                System.out.println("Game joined successfully!");
                gameJoined = true;

            }

            websocketCommunicator = new WebsocketCommunicator(observer);
            if (playerColor == null) {
                websocketCommunicator.send(new JoinObserverCommand(authToken, gameId));
            } else {
                ChessGame.TeamColor teamColorEnum = ChessGame.TeamColor.valueOf(playerColor.toUpperCase());
                websocketCommunicator.send(new JoinPlayerCommand(authToken, gameId, teamColorEnum));
            }
            return "Success";

        } catch (Exception ex) {
            System.out.println("Joining or Observing game not successful");
            System.out.println("Try listing games first or checking your input values");
            System.out.println(ex.getMessage());
        }

        return null;
    }

    public String redraw() {
        GameData game = returnCurrentGame();
        assert game != null;
        DrawBoard.drawBoard(game.getGame().getBoard(), currentColor, null, null);

        return null;
    }

    public String leave() {
        try {
            websocketCommunicator.send(new LeaveCommand(authToken, enteredGameId));
            websocketCommunicator.closeSession();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ex.getMessage();
        }

        enteredGameId = -1;
        gameJoined = false;
        currentColor = ChessGame.TeamColor.WHITE;

        System.out.println("You successfully left the game");
        return null;
    }

    public String resign() {
        try {
            websocketCommunicator.send(new ResignCommand(authToken, enteredGameId));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ex.getMessage();
        }

        return null;
    }

    public String highlightPossibleMoves(String pieceLocation) {
        ChessPosition piecePosition = new ChessPosition(pieceLocation);

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

    public String movePiece(String startLocation, String endLocation, String promotionPieceString) {
        ChessPosition startPosition = new ChessPosition(startLocation);
        ChessPosition endPosition = new ChessPosition(endLocation);

        ChessPiece.PieceType promotionPieceType;
        if (promotionPieceString == null) {
            promotionPieceType = null;
        } else {
            promotionPieceType = ChessPiece.PieceType.valueOf(promotionPieceString.toUpperCase());
        }

        ChessMove newMove = new ChessMove(startPosition, endPosition, promotionPieceType);

        try {
            websocketCommunicator.send(new MakeMoveCommand(authToken, enteredGameId, newMove));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ex.getMessage();
        }
        return null;
    }

    public GameData returnCurrentGame() {
        try {
            ParentResponse response;
            GenericRequest genericRequest = new GenericRequest();
            response = httpCommunicator.sendRequest("GET", serverUrl + "/game", genericRequest, ListGamesResponse.class, authToken);
            List<GameData> games = response.getGames();
            mostRecentGameNumbers.clear();
            for (GameData game : games) {
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
