package service;

import dataaccess.DataAccess;
import dataaccess.NotLoggedInException;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public int createGame(String authToken, String gameName) throws NotLoggedInException {
        if (dataAccess.retrieveAuthData(authToken) == null){
            throw new NotLoggedInException("Error: unauthorized");
        }
        return dataAccess.newGame(gameName);
    }

    public Collection<GameData> listGames(String authToken) throws NotLoggedInException {
        if (dataAccess.retrieveAuthData(authToken) == null){
            throw new NotLoggedInException("Error: unauthorized");
        }
        return dataAccess.getGames();
    }

    public void joinGame(String authToken, int gameID, String color) throws NotLoggedInException {
        if (dataAccess.retrieveAuthData(authToken) == null){
            throw new NotLoggedInException("Error: unauthorized");
        } else if (dataAccess.getGame(gameID) == null) {
            throw new GameNotFoundException("Error: bad request:");
        }
    }
}
