package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.JoinRequest;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameServiceTests {
    private final MemoryDataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService(dataAccess);
    private final DataService dataService = new DataService(dataAccess);
    private final GameService gameService = new GameService(dataAccess);

    @BeforeEach
    @AfterEach
    public void clear() throws InternalServerErrorException {
        dataService.clear();
    }

    @Test
    public void createGame() throws AlreadyTakenException, BadRequestException, InternalServerErrorException {
        UserData sampleUser = new UserData("user1", "pass", "email");
        String authToken = userService.registerUser(sampleUser).authToken();
        Assertions.assertDoesNotThrow(() -> gameService.createGame(authToken, "new game"));
    }

    @Test
    public void createGameNotLoggedIn() {
        Assertions.assertThrows(NotLoggedInException.class, ()-> gameService.createGame("invalid auth", "game"));
    }

    @Test
    public void createHundredGames() throws AlreadyTakenException, NotLoggedInException, BadRequestException, InternalServerErrorException {
        UserData sampleUser = new UserData("user1", "pass", "email");
        String authToken = userService.registerUser(sampleUser).authToken();

        Collection<GameData> expected = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            int finalI = i;
            GameData newGame = new GameData(i+1, null, null, "game" + i, new ChessGame());
            Assertions.assertDoesNotThrow(() -> gameService.createGame(authToken, "game" + finalI));
            expected.add(newGame);
        }

        Assertions.assertEquals(expected, gameService.listGames(authToken));
    }


    @Test
    public void createAndListGame() throws AlreadyTakenException, NotLoggedInException, BadRequestException, InternalServerErrorException {
        UserData sampleUser = new UserData("user1", "pass", "email");
        String authToken = userService.registerUser(sampleUser).authToken();
        gameService.createGame(authToken, "new game");
        GameData game = new GameData(1, null, null, "new game", new ChessGame());
        ArrayList<GameData> expected = new ArrayList<>(List.of(game));
        ArrayList<GameData> actual = (ArrayList<GameData>) gameService.listGames(authToken);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void listNotLoggedIn() {
        Assertions.assertThrows(NotLoggedInException.class, ()-> gameService.listGames("invalid auth"));
    }

    @Test
    public void joinAsWhite() throws AlreadyTakenException, NotLoggedInException, BadRequestException, InternalServerErrorException {
        UserData sampleUser = new UserData("user1", "pass", "email");
        String authToken = userService.registerUser(sampleUser).authToken();
        gameService.createGame(authToken, "new game");
        Assertions.assertDoesNotThrow(()-> gameService.joinGame(authToken, new JoinRequest("WHITE", 1)));

        GameData game = new GameData(1, "user1", null, "new game", new ChessGame());
        ArrayList<GameData> expected = new ArrayList<>(List.of(game));
        Assertions.assertEquals(expected, gameService.listGames(authToken));
    }

    @Test
    public void joinAlreadyTaken()
            throws AlreadyTakenException,
            NotLoggedInException,
            GameNotFoundException,
            BadRequestException,
            InternalServerErrorException {
        UserData sampleUser = new UserData("user1", "pass", "email");
        UserData sadUser = new UserData("fred", "contraseña", "email");
        String auth1 = userService.registerUser(sampleUser).authToken();
        gameService.createGame(auth1, "new game");
        gameService.joinGame(auth1, new JoinRequest("WHITE", 1));

        String auth2 = userService.registerUser(sadUser).authToken();

        Assertions.assertThrows(AlreadyTakenException.class, ()-> gameService.joinGame(auth2, new JoinRequest("WHITE", 1)));
    }
}
