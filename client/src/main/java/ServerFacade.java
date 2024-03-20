import chess.ChessGame;
import model.GameData;
import server.request.*;
import server.response.CreateGameResponse;
import server.response.ListGamesResponse;
import server.response.ParentResponse;
import server.response.RegisterAndLoginResponse;
import ui.DrawBoard;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ServerFacade {
    private final ClientCommunicator server = new ClientCommunicator();
    private final String serverUrl;
    private String authToken = null;
    HashMap<Integer, Integer> mostRecentGameNumbers = new HashMap<>();

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
            case "join" -> {
                if (params.length >= 2)
                    yield joinGame(params[0], params[1]);
                else
                    yield joinGame(params[0], null);
            }
            case "observe" -> joinGame(params[0], null);
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
                List<GameData> Games = response.getGames();
                int i = 0;
                mostRecentGameNumbers.clear();
                for (GameData game : Games) {
                    i += 1;
                    System.out.println("Game Number: " + i);
                    mostRecentGameNumbers.put(i, game.getGameID());
                    System.out.println("Game Name: " + game.getGameName());
                    System.out.println("Black Team Username: " + game.getWhiteUsername());
                    System.out.println("White Team Username: " + game.getWhiteUsername());
                    DrawBoard.drawBothBoards(game.getGame().getBoard());
                }
                return "All games listed";
            } catch (Exception ex) {
                return ex.getMessage();
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String joinGame(String gameNumberString, String playerColor) {
        try {
            PlayerColor playerColorEnum;
            int gameNumber = Integer.parseInt(gameNumberString);
            int gameId = mostRecentGameNumbers.get(gameNumber);
            try {
                playerColorEnum = Enum.valueOf(PlayerColor.class, playerColor);
            } catch (Exception ex) {
                playerColorEnum = null;
            }

            JoinGameRequest joinGameRequest = new JoinGameRequest(playerColorEnum, gameId);
            ParentResponse response = server.sendRequest("PUT", serverUrl + "/game", joinGameRequest, ParentResponse.class, authToken);

            try {
                return "Game joined successfully!";
            } catch (Exception ex) {
                return "Joining game not successful";
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
