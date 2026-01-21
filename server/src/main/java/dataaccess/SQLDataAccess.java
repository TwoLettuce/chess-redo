package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SQLDataAccess implements DataAccess {

    String[] tableInitializationStatements;
    public SQLDataAccess() throws DataAccessException {
        initializeDatabase();
    }

    private void initializeDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            for (var statement : tableInitializationStatements){

            }
        } catch (SQLException ex) {

        }
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void addUser(UserData userData) {

    }

    @Override
    public void addAuthData(AuthData authData) {

    }

    @Override
    public AuthData getAuthData(String authToken) {
        return null;
    }

    @Override
    public void removeAuth(String authToken) {

    }

    @Override
    public void clear() {

    }

    @Override
    public int newGame(String gameName) {
        return 0;
    }

    @Override
    public Collection<GameData> getGames() {
        return List.of();
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGameData) {

    }
}
