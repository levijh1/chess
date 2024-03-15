import chess.ChessGame;
import model.GameData;
import server.request.CreateGameRequest;
import server.request.GenericRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.response.CreateGameResponse;
import server.response.ListGamesResponse;
import server.response.ParentResponse;
import server.response.RegisterAndLoginResponse;
import ui.DrawBoard;

import java.util.Arrays;
import java.util.List;

public class ServerFacade {
    private final ClientCommunicator server = new ClientCommunicator();
    private final String serverUrl;
    private String authToken = null;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
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
            case "join" -> joinGame();
            case "observe" -> joinObserver();
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String help() {
        if (authToken == null) {
            return """
                    \tregister <USERNAME> <PASSWORD> <EMAIL> - to create an account       
                    \tlogin <USERNAME> <PASSWORD> - to play chess           
                    \tquit - playing chess            
                    \thelp - with possible commands
                    """;
        } else {
            return """
                    \tcreate <GAMENAME> - create a game
                    \tlist - list all available games
                    \tjoin <GAMEID> [WHITE|BLACK|<empty>] - play in a game
                    \tobserve <GAMEID> - watch a game
                    \tlogout - when you are done
                    \tquit - playing chess
                    \thelp - with possible commands
                    """;
        }
    }

    private String login(String username, String password) {
        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            ParentResponse response = server.sendRequest("POST", serverUrl + "/session", loginRequest, RegisterAndLoginResponse.class, authToken);

            try {
                this.authToken = response.getAuthToken();
                return "Successful login!";
            } catch (Exception ex) {
                return "login not successful. Check your password or make sure that username is already registered";
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String register(String username, String password, String email) {
        try {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            ParentResponse response = server.sendRequest("POST", serverUrl + "/user", registerRequest, RegisterAndLoginResponse.class, authToken);

            try {
                this.authToken = response.getAuthToken();
                return "Successfully registered and logged in!";
            } catch (Exception ex) {
                return "Register not successful. Try a different username.";
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String logout() {
        if (authToken == null) {
            return "You are already logged out";
        }
        try {
            GenericRequest genericRequest = new GenericRequest();
            try {
                server.sendRequest("DELETE", serverUrl + "/session", genericRequest, ParentResponse.class, authToken);
                this.authToken = null;
                return "Successful logout!";
            } catch (Exception ex) {
                return "logout not successful. Not already logged in";
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }

    }

    private String createGame(String gameName) {
        try {
            CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
            ParentResponse response = server.sendRequest("POST", serverUrl + "/game", createGameRequest, CreateGameResponse.class, authToken);

            try {
                return "Game created successfully!";
            } catch (Exception ex) {
                return "Game creation not successful";
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String listGames() {
        try {
            GenericRequest genericRequest = new GenericRequest();
            try {
                ParentResponse response = server.sendRequest("GET", serverUrl + "/game", genericRequest, ListGamesResponse.class, authToken);
                List<Object> Games = response.getGames();
                int i = 0;
                for (Object game : Games) {
                    GameData gameData = (GameData) game;
                    i += 1;
                    System.out.println("Game Number: " + i);
                    System.out.println("Game Name: " + gameData.getGameName());
                    System.out.println("Black Team Username: " + gameData.getWhiteUsername());
                    System.out.print("White Team Username: " + gameData.getWhiteUsername());
                    DrawBoard.drawBothBoards(gameData.getGame().getBoard());
                }
                return "All games listed";
            } catch (Exception ex) {
                return ex.getMessage();
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }

    }

    private String joinGame() {
        return null;

    }

    private String joinObserver() {
        return null;

    }
}