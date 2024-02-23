package server;

import com.google.gson.Gson;
import server.request.*;
import server.response.*;
import service.*;
import spark.*;

import java.lang.reflect.InvocationTargetException;

public class Server {
    //TODO: Figure out how to make the handlers all private and test from JSON input

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        //Register handlers for each endpoint using the method reference syntax
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object joinGame(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return handler(request, response, "joinGame", GenericRequest.class, JoinGameService.class);
    }

    private Object createGame(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return handler(request, response, "createGame", GenericRequest.class, CreateGameService.class);
    }

    private Object listGames(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return handler(request, response, "listGames", GenericRequest.class, ListGamesService.class);
    }

    private Object logout(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return handler(request, response, "logout", GenericRequest.class, LogoutService.class);
    }

    public Object login(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return handler(request, response, "login", LoginRequest.class, LoginService.class);
    }

    public Object register(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return handler(request, response, "register", RegisterRequest.class, RegisterService.class);
    }
//        RegisterRequest registerRequest = getRequestBody(request, RegisterRequest.class);
//
//        RegisterService registerService = new RegisterService();
//        RegisterResponse res = registerService.register(registerRequest);
//
//        response.status(res.getStatusCode()); //TODO: This might still allow the status code to be output as part of the json response
//
//        return new Gson().toJson(res);
//    }

    private Object clear(Request request, Response response) {
        ClearService clearService = new ClearService();
        ParentResponse res = clearService.clear();

        response.status(res.getStatusCode());
        return new Gson().toJson(res);
    }

    private <TRequest, TResponse, TService> Object handler(Request request, Response response,
                                                           String endPointName,
                                                           Class<TRequest> requestClass,
                                                           Class<TService> serviceClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        TRequest requestBody = getRequestBody(request, requestClass);

        TService service = serviceClass.getDeclaredConstructor().newInstance();

        TResponse res = (TResponse) serviceClass.getMethod(endPointName, requestClass).invoke(serviceClass, requestClass);

        response.status((Integer) ParentResponse.class.getMethod("getStatusCode").invoke(res));

        return new Gson().toJson(res);
    }


    private static <T> T getRequestBody(Request request, Class<T> requestClass) {
        return new Gson().fromJson(request.body(), requestClass);
    }

    public void stop() {
        Spark.stop();
    }

}
