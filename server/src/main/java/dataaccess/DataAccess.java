package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    UserData getUser(String username) throws InternalServerErrorException;

    void addUser(UserData userData) throws BadRequestException, InternalServerErrorException;

    void addAuthData(AuthData authData) throws BadRequestException, InternalServerErrorException;

    AuthData getAuthData(String authToken) throws InternalServerErrorException;

    void removeAuth(String authToken) throws InternalServerErrorException;

    void clear() throws InternalServerErrorException;

    int newGame(String gameName) throws InternalServerErrorException;

    Collection<GameData> getGames() throws InternalServerErrorException;

    GameData getGame(int gameID) throws InternalServerErrorException;

    void updateGame(int gameID, GameData updatedGameData) throws BadRequestException, InternalServerErrorException;
}
