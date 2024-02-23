package serverTests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.Server;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.response.RegisterAndLoginResponse;
import server.response.ParentResponse;
import service.LoginService;
import service.RegisterService;

public class serverTests {
    Server server;
    Object expected;

    @Test
    public void serverRegistrationTest() {
        RegisterAndLoginResponse expectedRes = new RegisterAndLoginResponse("testUsername", "testToken");
        String expected = new Gson().toJson(expectedRes);

        server = new Server();
//        server.register
        RegisterService registerService = new RegisterService();
        ParentResponse actualRes = registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        String actual = new Gson().toJson(actualRes);

        // Check all fields except for authToken
        JsonObject expectedJsonObject = JsonParser.parseString(expected).getAsJsonObject();
        JsonObject actualJsonObject = JsonParser.parseString(actual).getAsJsonObject();
        expectedJsonObject.remove("authToken");
        actualJsonObject.remove("authToken");

        Assertions.assertEquals(expectedJsonObject, actualJsonObject);
    }

    @Test
    public void serverLoginTest() {
        RegisterAndLoginResponse expectedRes = new RegisterAndLoginResponse("testUsername", "testToken");
        String expected = new Gson().toJson(expectedRes);

        RegisterService registerService = new RegisterService();
        registerService.register(new RegisterRequest("testUsername", "testPassword", "testEmail"));
        LoginService loginService = new LoginService();
        ParentResponse actualRes = loginService.login(new LoginRequest("testUsername", "testPassword"));
        String actual = new Gson().toJson(actualRes);

        // Check all fields except for authToken
        JsonObject expectedJsonObject = JsonParser.parseString(expected).getAsJsonObject();
        JsonObject actualJsonObject = JsonParser.parseString(actual).getAsJsonObject();
        expectedJsonObject.remove("authToken");
        actualJsonObject.remove("authToken");

        Assertions.assertEquals(expectedJsonObject, actualJsonObject);
    }

    @Test
    public void serverClearTest() {


    }
}
