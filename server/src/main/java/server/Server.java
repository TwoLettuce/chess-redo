package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.UserNotFoundException;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import service.DataService;
import service.GameService;
import service.UserService;

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
        try {
            AuthData authData = userService.login(loginRequest);
            ctx.result(gson.toJson(authData));
        } catch (UserNotFoundException ex) {
            ctx.status(ex.httpCode);
            ctx.json(gson.toJson(Map.of("message", ex.getMessage())));
        }
    }

    private void logout(Context ctx){

    }

    private void list(Context ctx){

    }

    private void create(Context ctx){

    }

    private void join(Context ctx){

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
