package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    ArrayList<UserData> users = new ArrayList<>();
    ArrayList<AuthData> authenticatedUsers = new ArrayList<>();
    ArrayList<GameData> games = new ArrayList<>();


    @Override
    public UserData getUser(String username) {
        for (UserData user : users){
            if (Objects.equals(user.username(), username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public void addUser(UserData userData) {
        users.add(userData);
    }

    @Override
    public void addAuthData(AuthData authData) {
        authenticatedUsers.add(authData);
    }

    @Override
    public AuthData retrieveAuthData(String authToken) {
        for (AuthData data : authenticatedUsers){
            if (Objects.equals(data.authToken(), authToken)){
                return data;
            }
        }
        return null;
    }

    @Override
    public void removeAuth(String authToken) {
        for (AuthData data : authenticatedUsers){
            if (Objects.equals(data.authToken(), authToken)){
                authenticatedUsers.remove(data);
                break;
            }
        }
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public int newGame(String gameName) {
        int gameID;
        if (games.isEmpty()){
            gameID = 1;
        } else {
            gameID = games.getLast().gameID() + 1;
        }
        games.add(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public Collection<GameData> getGames() {
        return games;
    }

    @Override
    public GameData getGame(int gameID) {
        for (GameData game : games){
            if (game.gameID() == gameID){
                return game;
            }
        }
        return null;
    }
}
