package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccess;
import dataaccess.GameNotFoundException;
import dataaccess.NotLoggedInException;
import model.GameData;

import java.util.Collection;
import java.util.Objects;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public int createGame(String authToken, String gameName) throws NotLoggedInException {
        if (dataAccess.getAuthData(authToken) == null){
            throw new NotLoggedInException("Error: unauthorized");
        }
        return dataAccess.newGame(gameName);
    }

    public Collection<GameData> listGames(String authToken) throws NotLoggedInException {
        if (dataAccess.getAuthData(authToken) == null){
            throw new NotLoggedInException("Error: unauthorized");
        }
        return dataAccess.getGames();
    }

    public void joinGame(String authToken, int gameID, String color) throws NotLoggedInException, AlreadyTakenException, GameNotFoundException {
        if (dataAccess.getAuthData(authToken) == null){
            throw new NotLoggedInException("Error: unauthorized");
        }
        GameData game = dataAccess.getGame(gameID);
        if (game == null || !(Objects.equals(color, "WHITE") || Objects.equals(color, "BLACK"))){
            throw new GameNotFoundException("Error: bad request");
        }

        if (Objects.equals(color, "WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            GameData updatedGameData =  new GameData(game.gameID(), dataAccess.getAuthData(authToken).username(),
                    game.blackUsername(), game.gameName(), game.game());
            dataAccess.updateGame(game.gameID(), updatedGameData);
        } else {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
        }



    }
}
