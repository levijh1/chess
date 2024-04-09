package clientTests;

import client.Client;
import org.junit.jupiter.api.*;
import server.Server;
import passoffTests.obfuscatedTestClasses.TestServerFacade;

import java.util.HashMap;
import java.util.Map;


public class ServerFacadeTests {

    private static Server server;
    static Client facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new Client(port);
        TestServerFacade testServerFacade = new TestServerFacade("localhost", Integer.toString(port));
        testServerFacade.clear();
    }

    @BeforeEach
    public void init_before_each() {
        facade = new Client(8080);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerPositiveTest() {
        String authToken = facade.register("player1", "password", "p1@email.com");
        Assertions.assertTrue(authToken.length() > 10);
    }

    @Test
    public void registerNegativeTest() {
        String authToken1 = facade.register("player1", "password", "p1@email.com");
        //Auth token not changed because register is not successful
        String authToken2 = facade.register("player1", "password", "p1@email.com");

        Assertions.assertEquals(authToken1, authToken2);
    }

    @Test
    public void loginPositiveTest() {
        facade.register("player1", "password", "p1@email.com");
        String authToken = facade.login("player1", "password");
        Assertions.assertTrue(authToken.length() > 10);
    }

    @Test
    public void loginNegativeTest() {
        String authToken1 = facade.register("player1", "password1", "p1@email.com1");
        facade.register("player2", "password2", "p1@email.com2");

        //incorrect password
        String authToken2 = facade.login("player1", "password2");
        Assertions.assertNotEquals(authToken1, authToken2);
    }

    @Test
    public void helpPositiveTest() {
        String preLoginMenu = """
                \tregister <USERNAME> <PASSWORD> <EMAIL> - to create an account
                \tlogin <USERNAME> <PASSWORD> - to play chess
                \tquit - playing chess
                \thelp - with possible commands
                """;
        String postLoginMenu = """
                \tcreate <GAME NAME> - create a game
                \tlist - list all available games
                \tjoin <GAME ID> [WHITE|BLACK|<empty>] - play in a game
                \tobserve <GAME ID> - watch a game
                \tlogout - when you are done
                \tquit - playing chess
                \thelp - with possible commands
                """;

        facade.logout();
        Assertions.assertEquals(facade.help(), preLoginMenu);

        facade.register("helpUsername", "helpPassword", "helpEmail");
        Assertions.assertEquals(facade.help(), postLoginMenu);
    }

    @Test
    public void logoutPositiveTest() {
        facade.register("player1", "password", "p1@email.com");
        String authToken = facade.logout();
        Assertions.assertNull(authToken);
    }

    @Test
    public void logoutNegativeTest() {
        facade.logout();
        facade.register("player1", "password", "p1@email.com");
        String authToken = facade.login("player1", "password");
        Assertions.assertTrue(authToken.length() > 10);
    }

    @Test
    public void helpNegativeTest() {
        String preLoginMenu = """
                \tregister <USERNAME> <PASSWORD> <EMAIL> - to create an account
                \tlogin <USERNAME> <PASSWORD> - to play chess
                \tquit - playing chess
                \thelp - with possible commands
                """;

        facade.logout();
        Assertions.assertEquals(facade.help(), preLoginMenu);

        facade.login("testUsername", "testPassword"); //Invalid login because not registered yet
        Assertions.assertEquals(facade.help(), preLoginMenu);
    }

    @Test
    public void createGamePositiveTest() {
        facade.register("createGameUsername", "createGamePassword", "createGameEmail");
        String gameId = facade.createGame("gameName");

        Assertions.assertNotNull(gameId);
    }

    @Test
    public void createGameNegativeTest() {
        facade.logout();
        String gameId = facade.createGame("gameName");

        Assertions.assertNull(gameId);
    }

    @Test
    public void listGamesPositiveTest() {
        facade.register("listGamesUsername", "lisGamesPassword", "listGamesEmail");
        facade.createGame("listGamesGame");
        String gameList = facade.listGames();

        Assertions.assertNotNull(gameList);
    }

    @Test
    public void listGamesNegativeTest() {
        facade.register("listGamesUsername", "lisGamesPassword", "listGamesEmail");
        facade.createGame("listGamesGame");
        facade.logout();
        String gameList = facade.listGames();

        Assertions.assertNull(gameList);
    }

    @Test
    public void joinGamePositiveTest() {
        facade.logout();
        facade.register("joinGameUsername1", "joinGamePassword1", "joinGameEmail1");
        String gameID = facade.createGame("joinGameGame1");
        String mostRecentGameNumbers = facade.listGames();
        Map<String, String> reconstructedMap = new HashMap<>();
        String[] pairs = mostRecentGameNumbers.substring(1, mostRecentGameNumbers.length() - 1).split(", ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            reconstructedMap.put(keyValue[1], keyValue[0]);
        }

        String result = facade.joinGame(reconstructedMap.get(gameID), "WHITE");
        Assertions.assertEquals(result, "Success");
    }

    @Test
    public void observeGamePositiveTest() {

        facade.register("joinGameUsername2", "joinGamePassword2", "joinGameEmail2");
        String gameID = facade.createGame("joinGameGame2");
        String mostRecentGameNumbers = facade.listGames();
        Map<String, String> reconstructedMap = new HashMap<>();
        String[] pairs = mostRecentGameNumbers.substring(1, mostRecentGameNumbers.length() - 1).split(", ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            reconstructedMap.put(keyValue[1], keyValue[0]);
        }

        String result = facade.joinGame(reconstructedMap.get(gameID), null);
        Assertions.assertEquals("Success", result);
    }

    @Test
    public void joinGameNegativeTest() {
        facade.logout();
        facade.register("joinGameUsername3", "joinGamePassword3", "joinGameEmail3");
        facade.createGame("joinGameGame3");

        facade.logout();
        //attempting to join a game without being logged in
        String result = facade.joinGame("1", null);
        Assertions.assertNull(result);
    }
}
