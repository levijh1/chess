package client;

import model.GameData;
import server.request.*;
import server.response.CreateGameResponse;
import server.response.ListGamesResponse;
import server.response.ParentResponse;
import server.response.RegisterAndLoginResponse;
import ui.DrawBoard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class ServerFacade {
    private final ClientCommunicator server = new ClientCommunicator();
    private final String serverUrl;
    private String authToken = null;
    HashMap<Integer, Integer> mostRecentGameNumbers = new HashMap<>();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
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
            case "quit" -> "quit";
            default -> help();
        };
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
            ParentResponse response = server.sendRequest("POST", serverUrl + "/session", loginRequest, RegisterAndLoginResponse.class, authToken);

            try {
                this.authToken = response.getAuthToken();
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
            ParentResponse response = server.sendRequest("POST", serverUrl + "/user", registerRequest, RegisterAndLoginResponse.class, authToken);

            try {
                this.authToken = response.getAuthToken();
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
                server.sendRequest("DELETE", serverUrl + "/session", genericRequest, ParentResponse.class, authToken);
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
            ParentResponse response = server.sendRequest("POST", serverUrl + "/game", createGameRequest, CreateGameResponse.class, authToken);

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
                ParentResponse response = server.sendRequest("GET", serverUrl + "/game", genericRequest, ListGamesResponse.class, authToken);
                List<GameData> Games = response.getGames();
                int i = 0;
                mostRecentGameNumbers.clear();
                for (GameData game : Games) {
                    i += 1;
                    System.out.print(SET_TEXT_COLOR_BLUE);
                    System.out.println("\nGame Number: " + i);
                    mostRecentGameNumbers.put(i, game.getGameID());
                    System.out.println("Game Name: " + game.getGameName());
                    System.out.println("Black Team Username: " + game.getBlackUsername());
                    System.out.println("White Team Username: " + game.getWhiteUsername());
                    DrawBoard.drawBothBoards(game.getGame().getBoard());
                }
                System.out.println("All games listed");
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
            PlayerColor playerColorEnum;
            int gameNumber = Integer.parseInt(gameNumberString);
            int gameId = mostRecentGameNumbers.get(gameNumber);
            try {
                playerColorEnum = PlayerColor.valueOf(playerColor.toUpperCase());
            } catch (Exception ex) {
                playerColorEnum = null;
            }
            JoinGameRequest joinGameRequest = new JoinGameRequest(playerColorEnum, gameId);
            ParentResponse response = server.sendRequest("PUT", serverUrl + "/game", joinGameRequest, ParentResponse.class, authToken);

            try {
                System.out.println(response.getMessage());
            } catch (Exception ex) {
                System.out.println("Game joined successfully!");
                return "Success";
            }
        } catch (Exception ex) {
            System.out.println("Joining or Observing game not successful");
            System.out.println("Try listing games first or checking your input values");
            System.out.println(ex.getMessage());
        }

        return null;
    }
}
