package serverTests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.Server;
import server.request.RegisterRequest;
import server.response.RegisterResponse;
import service.RegisterService;

public class serverTests {
    Server server;
    Object expected;

    @Test
    public void serverRegistrationTest() {
        RegisterResponse expectedRes = new RegisterResponse("testUsername", "testToken");
        String expected = new Gson().toJson(expectedRes);

        RegisterService registerService = new RegisterService();
        RegisterResponse actualRes = registerService.register(new RegisterRequest("testUsername", "testPassword"));
        String actual = new Gson().toJson(actualRes);

        // Check all fields except for authToken
        JsonObject expectedJsonObject = JsonParser.parseString(expected).getAsJsonObject();
        JsonObject actualJsonObject = JsonParser.parseString(actual).getAsJsonObject();
        expectedJsonObject.remove("authToken");
        actualJsonObject.remove("authToken");

        Assertions.assertEquals(expectedJsonObject, actualJsonObject);
    }
}
