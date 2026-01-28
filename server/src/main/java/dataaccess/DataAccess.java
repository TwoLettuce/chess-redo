package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    UserData getUser(String username);

    void addUser(UserData userData) throws BadRequestException;

    void addAuthData(AuthData authData) throws BadRequestException;

    AuthData getAuthData(String authToken);

    void removeAuth(String authToken);

    void clear();

    int newGame(String gameName);

    Collection<GameData> getGames();

    GameData getGame(int gameID);

    void updateGame(int gameID, GameData updatedGameData);
}
