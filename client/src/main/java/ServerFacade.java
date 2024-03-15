import server.request.RegisterRequest;

import java.io.IOException;
import java.util.Arrays;

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
            case "login" -> login();
            case "register" -> register(params[0], params[1], params[2]);
            case "logout" -> logout();
            case "create" -> createGame();
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

    private String login() {
        return null;
    }

    private String register(String username, String password, String email) {
        try {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            return server.doPost(serverUrl + "/user", registerRequest, null);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String logout() {
        return null;

    }

    private String createGame() {
        return null;

    }

    private String listGames() {
//        server.doGet(serverUrl, authToken);
        return null;

    }

    private String joinGame() {
        return null;

    }

    private String joinObserver() {
        return null;

    }
}
