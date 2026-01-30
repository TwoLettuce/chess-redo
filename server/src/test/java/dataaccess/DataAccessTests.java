package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataAccessTests {
    DataAccess dataAccess = new SQLDataAccess();

    public DataAccessTests() throws DataAccessException {
    }

    @AfterEach
    public void clear() throws InternalServerErrorException {
        dataAccess.clear();
    }

    @Test
    public void addUserTest(){
        UserData newUser = new UserData("newbie", "pass", "jerry@hotmail.com");
        Assertions.assertDoesNotThrow(()->dataAccess.addUser(newUser));
    }

    @Test
    public void addBadUser(){
        UserData newUser = new UserData("hi", "password", null);
        Assertions.assertThrows(Exception.class, ()-> dataAccess.addUser(newUser));
    }

    @Test
    public void getUserTest() throws BadRequestException {
        UserData newUser = new UserData("hi", "password", "hipassword@gmail.com");
        UserData differentUser = new UserData("hey", "pass", "email@hotmail.com");
        dataAccess.addUser(newUser);
        UserData result = dataAccess.getUser(newUser.username());
        Assertions.assertEquals(newUser.username(), result.username());
        Assertions.assertEquals(newUser.email(), result.email());
        Assertions.assertNotEquals(differentUser, result);
    }

    @Test
    public void getBadUser() {
        Assertions.assertNull(dataAccess.getUser("fake"));
    }

    @Test
    public void addAuthDataTest(){
        AuthData authData = new AuthData("user", "auoiwj-r923nn2n92nn vi-");
        Assertions.assertDoesNotThrow(()->dataAccess.addAuthData(authData));
    }

    @Test
    public void addBadAuthTest(){
        AuthData authData = new AuthData(null, null);
        Assertions.assertThrows(Exception.class, ()->dataAccess.addAuthData(authData));
    }

    @Test
    public void getAuthTest() throws BadRequestException {
        AuthData authData = new AuthData("user", "auoiwj-r923nn2n92nn vi-");
        dataAccess.addAuthData(authData);
        Assertions.assertEquals(authData, dataAccess.getAuthData(authData.authToken()));
    }

    @Test
    public void getBadAuthTest(){
        Assertions.assertNull(dataAccess.getAuthData("hey"));
    }

    @Test
    public void removeAuthTest() throws BadRequestException {
        dataAccess.addAuthData(new AuthData("username", "auth"));
        Assertions.assertDoesNotThrow(()->dataAccess.removeAuth("auth"));
    }

    @Test
    public void removeBadAuthTest(){
        Assertions.assertDoesNotThrow(()->dataAccess.removeAuth("badAuth"));
    }

    @Test
    public void clearTest() throws BadRequestException, InternalServerErrorException {
        UserData user = new UserData("hey there", "pass", "email");
        dataAccess.addUser(user);
        AuthData auth = new AuthData("hey there", "lajra;ojewnoinf9 1-2n");
        dataAccess.addAuthData(auth);
        dataAccess.newGame("hey");
        dataAccess.clear();
        Assertions.assertTrue(
                dataAccess.getUser(user.username())==null &&
                dataAccess.getAuthData(auth.authToken())==null &&
                Objects.equals(dataAccess.getGames(), new ArrayList<GameData>())
        );
    }

    @Test
    public void createGameTest() throws InternalServerErrorException {
        Assertions.assertEquals(1, dataAccess.newGame("first game"));
        Assertions.assertEquals(2, dataAccess.newGame("second game"));
    }

    @Test
    public void createGameNegativeTest() throws InternalServerErrorException {
        Assertions.assertNotEquals(-1, dataAccess.newGame("hey"));
    }

    @Test
    public void listGamesTest() throws InternalServerErrorException {
        ArrayList<GameData> games = new ArrayList<>();
        games.add(new GameData(1, null, null, "first game", new ChessGame()));
        games.add(new GameData(2, null, null, "second game", new ChessGame()));
        games.add(new GameData(3, null, null, "third game", new ChessGame()));
        dataAccess.newGame("first game");
        dataAccess.newGame("second game");
        dataAccess.newGame("third game");

        Assertions.assertEquals(games, dataAccess.getGames());
    }

    @Test
    public void listNoGames() throws InternalServerErrorException {
        Assertions.assertEquals(List.of(), dataAccess.getGames());
    }

    @Test
    public void getExistingGame() throws InternalServerErrorException {
        int id = dataAccess.newGame("gameName");
        Assertions.assertEquals(
                new GameData(id, null, null, "gameName",
                new ChessGame()), dataAccess.getGame(id)
        );
    }

    @Test
    public void getNonexistentGame() throws InternalServerErrorException {
        Assertions.assertNull(dataAccess.getGame(-1));
    }

    @Test
    public void updateGame() throws InvalidMoveException, BadRequestException, InternalServerErrorException {
        int id = dataAccess.newGame("game");
        var game = dataAccess.getGame(id);
        game.game().makeMove(new ChessMove(new ChessPosition(2,2), new ChessPosition(4,2), null));
        GameData newGameData = new GameData(id, "white", "black", "game", game.game());
        dataAccess.updateGame(id, newGameData);
        Assertions.assertEquals(newGameData, dataAccess.getGame(id));
    }

    @Test
    public void updateNonexistentGame(){
        GameData updatedGameData = new GameData(-50, null, null, null, null);
        Assertions.assertThrows(DataAccessException.class, ()->dataAccess.updateGame(updatedGameData.gameID(), updatedGameData));
    }
}
