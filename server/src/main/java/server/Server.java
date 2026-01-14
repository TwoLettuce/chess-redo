package server;

import io.javalin.*;
import io.javalin.http.Context;


public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalin.create().delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::list)
                .post("/game", this::create)
                .put("/game", this::join);


    }

    private void clear(Context ctx){

    }

    private void register(Context ctx){

    }

    private void login(Context ctx){

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
