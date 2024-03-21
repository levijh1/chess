package clientTests;

import client.ServerFacade;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import passoffTests.obfuscatedTestClasses.TestServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        TestServerFacade testServerFacade = new TestServerFacade("localhost", Integer.toString(port));
        testServerFacade.clear();
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
        //Authtoken2 not changed because register is not successful
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

        //attempt to login with wrong password
        String authToken2 = facade.login("player1", "password2");
        Assertions.assertNotEquals(authToken1, authToken2);
    }
}
