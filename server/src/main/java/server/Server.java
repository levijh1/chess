package server;

import com.google.gson.Gson;
import server.request.*;
import server.response.*;
import service.*;
import spark.*;

public class Server {
    //Variables

    public static void main(String[] args) {
        new Server().run(0);
    }

    public int run(int num) {
        //TODO: Figure out why JAVA needed a number input
        int port = 8080;
        //Specify the port you want hte server to listen on
        Spark.port(port);

        //Register handlers for each endpoint using the method reference syntax
//        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
//        Spark.post("/session", this::login);
//        Spark.delete("/session", this::logout);
//        Spark.get("/game", this::listGames);
//        Spark.post("/game", this::createGame);
//        Spark.put("/game", this::joinGame);

        return port;
    }

    //    private Object joinGame(Request request, Response response) {
//    }
//
//    private Object createGame(Request request, Response response) {
//    }
//
//    private Object listGames(Request request, Response response) {
//    }
//
//    private Object logout(Request request, Response response) {
//    }
//
//    private Object login(Request request, Response response) {
//    }

    private Object register(Request request, Response response) {
        //TODO: try to make a method for all of this to avoid duplicate code
        RegisterRequest registerRequest = getRequestBody(request, RegisterRequest.class);

        RegisterService registerService = new RegisterService();
        RegisterResponse res = registerService.register(registerRequest);

        response.status(res.getStatusCode()); //TODO: This might still allow the status code to be output as part of the json response

        return new Gson().toJson(res);
    }

//    private Object clear(Request request, Response response) {
//
//    }


    private static <T> T getRequestBody(Request request, Class<T> requestClass) {
        return new Gson().fromJson(request.body(), requestClass);
    }

    public void stop() {
        Spark.stop();
    }

}
