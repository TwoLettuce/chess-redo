package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import model.UserData;
import request.LoginRequest;
import service.DataService;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Server {

    private final Javalin javalin;
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final DataService dataService = new DataService(dataAccess);
    private final UserService userService = new UserService(dataAccess);
    private final GameService gameService = new GameService(dataAccess);
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalin.delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::list)
                .post("/game", this::create)
                .put("/game", this::join);
    }

    private void clear(Context ctx){
        dataService.clear();
    }

    private void register(Context ctx){
        UserData newUser = gson.fromJson(ctx.body(), UserData.class);
        if (newUser.username() == null || newUser.password() == null || newUser.email() == null){
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        }
        try {
            AuthData authData = userService.registerUser(newUser);
            ctx.result(gson.toJson(authData));
        } catch (AlreadyTakenException ex) {
            ctx.status(ex.httpCode);
            ctx.json(gson.toJson(Map.of("message", ex.getMessage())));
        }
    }

    private void login(Context ctx){
        LoginRequest loginRequest = gson.fromJson(ctx.body(), LoginRequest.class);
        if (loginRequest.username() == null || loginRequest.password() == null){
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        }
        try {
            AuthData authData = userService.login(loginRequest);
            ctx.result(gson.toJson(authData));
        } catch (UserNotFoundException ex) {
            ctx.status(ex.httpCode);
            ctx.json(gson.toJson(Map.of("message", ex.getMessage())));
        }
    }

    private void logout(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            userService.logout(authToken);
        } catch (NotLoggedInException ex) {
            ctx.status(ex.httpCode);
            ctx.json(gson.toJson(Map.of("message", ex.getMessage())));
        }
    }

    private void list(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            Collection<GameData> games = gameService.listGames(authToken);
            ctx.result(gson.toJson(games));
        } catch (NotLoggedInException ex) {
            ctx.status(ex.httpCode);
            ctx.json(gson.toJson(Map.of("message", ex.getMessage())));
        }
    }

    private void create(Context ctx){
        String authToken = ctx.header("authorization");
        String gameName = (String) gson.fromJson(ctx.body(), HashMap.class).get("gameName");
        if (gameName == null){
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        }
        try {
            int gameID = gameService.createGame(authToken, gameName);
            ctx.result(gson.toJson(Map.of("gameID", gameID)));
        } catch (NotLoggedInException ex) {
            ctx.status(ex.httpCode);
            ctx.json(gson.toJson(Map.of("message", ex.getMessage())));
        }
    }

    private void join(Context ctx){
        String authToken = ctx.header("authorization");
        JoinRequest joinRequest = gson.fromJson(ctx.body(), JoinRequest.class);
        try {
            gameService.joinGame(authToken, joinRequest);
        } catch (NotLoggedInException ex) {
            ctx.status(ex.httpCode);
            ctx.json(gson.toJson(Map.of("message", ex.getMessage())));
        } catch (AlreadyTakenException ex) {
            ctx.status(ex.httpCode);
            ctx.json(gson.toJson(Map.of("message", ex.getMessage())));
        } catch (GameNotFoundException ex) {
            ctx.status(ex.httpCode);
            ctx.json(gson.toJson(Map.of("message", ex.getMessage())));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
