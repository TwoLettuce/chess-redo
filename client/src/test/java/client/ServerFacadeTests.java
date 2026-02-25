package client;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import model.request.LoginRequest;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private String auth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clearAndRegisterBasicUser() throws Exception {
        clearDB();
        auth = facade.register(new UserData("BasicUser", "pass", "basicdude@hey.net"));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    private void clearDB() throws Exception{
        facade.clear();
    }

    @Test
    public void registerUser() throws Exception {
        clearDB();
        Assertions.assertDoesNotThrow(
                ()-> facade.register(
                        new UserData("User", "password", "email")));
    }

    @Test
    public void registerInvalidUser() {
        Assertions.assertThrows(
                Exception.class,
                ()-> facade.register(
                        new UserData(null, null, null)));
    }


    @Test
    public void loginNormal() throws Exception {
        registerUser();
        LoginRequest loginRequest = new LoginRequest("User", "password");
        Assertions.assertDoesNotThrow(() -> facade.login(loginRequest));
    }


    @Test
    public void loginInvalidUser() throws Exception {
        registerUser();
        LoginRequest loginRequest = new LoginRequest("User", "pa$$word");
        Assertions.assertThrows(Exception.class, () -> facade.login(loginRequest));
    }

    @Test
    public void logoutNormal() throws Exception {
        Assertions.assertDoesNotThrow(() -> facade.logout(auth));
    }

    @Test
    public void logoutNotLoggedIn(){
        Assertions.assertThrows(Exception.class, () -> facade.logout("howdy cheeseburger"));
    }

    @Test
    public void createGameSuccessful() throws Exception {
        Assertions.assertDoesNotThrow(()->facade.createGame(auth, "hey"));
    }

    @Test
    public void createGameInvalid() throws Exception {
        Assertions.assertThrows(Exception.class, ()->facade.createGame(auth, null));
    }

    @Test
    public void listGames() throws Exception {
        Assertions.assertDoesNotThrow(() -> facade.listGames(auth));
        Assertions.assertEquals(List.of(), facade.listGames(auth));
        facade.createGame(auth, "testGame");
        Assertions.assertEquals(List.of(
                new GameData(
                        1,
                        null ,
                        null,
                        "testGame",
                        new ChessGame()))
        , facade.listGames(auth));
    }

    @Test
    public void listGamesWithoutAuth() throws Exception {
        Assertions.assertThrows(Exception.class, () -> facade.listGames("ur mom"));
    }



}
