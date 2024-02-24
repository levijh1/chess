package server;

import com.google.gson.Gson;
import server.request.*;
import server.response.*;
import service.*;
import spark.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Server {
    public static void main(String[] args) {
        new ClearService().clear();
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
        return authTokenHandler(request, response, "joinGame", JoinGameRequest.class, JoinGameService.class);
    }

    private Object createGame(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return authTokenHandler(request, response, "createGame", CreateGameRequest.class, CreateGameService.class);
    }

    private Object listGames(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return authTokenHandler(request, response, "listGames", GenericRequest.class, ListGamesService.class);
    }

    private Object logout(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return authTokenHandler(request, response, "logout", GenericRequest.class, LogoutService.class);
    }

    public Object login(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return handler(request, response, "login", LoginRequest.class, LoginService.class);
    }

    public Object register(Request request, Response response) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return handler(request, response, "register", RegisterRequest.class, RegisterService.class);
    }

    private Object clear(Request request, Response response) {
        ClearService clearService = new ClearService();
        ParentResponse res = clearService.clear();

        response.status(res.getStatusCode());
        return new Gson().toJson(res);
    }


    private <TRequest, TResponse, TService> Object authTokenHandler(Request request, Response response,
                                                                    String endPointName,
                                                                    Class<TRequest> requestClass,
                                                                    Class<TService> serviceClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ParentResponse res;
        TRequest requestBody = getRequestBody(request, requestClass);
        String authToken = request.headers("Authorization");


        TService service = serviceClass.getDeclaredConstructor().newInstance();

        Method method = serviceClass.getMethod(endPointName, requestClass, String.class);

        res = (ParentResponse) method.invoke(service, requestBody, authToken);

        response.status((Integer) ParentResponse.class.getMethod("getStatusCode").invoke(res));

        return new Gson().toJson(res);
    }

    private <TRequest, TResponse, TService> Object handler(Request request, Response response,
                                                           String endPointName,
                                                           Class<TRequest> requestClass,
                                                           Class<TService> serviceClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ParentResponse res;
        TRequest requestBody = getRequestBody(request, requestClass);

        TService service = serviceClass.getDeclaredConstructor().newInstance();

        Method method = serviceClass.getMethod(endPointName, requestClass);

        res = (ParentResponse) method.invoke(service, requestBody);

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
