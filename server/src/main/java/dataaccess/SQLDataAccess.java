package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SQLDataAccess implements DataAccess {
    Gson gson = new Gson();
    String[] tableInitializationStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (username varchar(255) NOT NULL,
            password varchar(255) NOT NULL,
            email varchar(255) NOT NULL,
            INDEX (username));
            """,

            """
            CREATE TABLE IF NOT EXISTS authenticatedUsers (
            username varchar(255) NOT NULL,
            authToken varchar(255) NOT NULL,
            INDEX (authToken));
            """,

            """
            CREATE TABLE IF NOT EXISTS games (gameID int NOT NULL AUTO_INCREMENT,
             whiteUsername varchar(255),
             blackUsername varchar(255),
             gameName varchar(255) NOT NULL,
             game TEXT,
             PRIMARY KEY (gameID));
            """
    };

    String[] tableClearStatements = {
            """
            TRUNCATE TABLE users;
            """,
            """
            TRUNCATE TABLE authenticatedUsers
            """,
            """
            TRUNCATE TABLE games;
            """
    };

    public SQLDataAccess() throws DataAccessException {
        initializeDatabase();
    }

    private void initializeDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            for (var statement : tableInitializationStatements){
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: Could not configure database %s", ex.getMessage()));
        }
    }

    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()){
            try (var preppedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")){
                preppedStatement.setString(1, username);
                var result = preppedStatement.executeQuery();
                result.next();
                return new UserData(result.getString(1), result.getString(2), result.getString(3));
            }
        } catch (SQLException | DataAccessException ex){
            return null;
        }
    }

    @Override
    public void addUser(UserData userData) throws BadRequestException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")){
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, cryptographizePassword(userData.password()));
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException ex){
            throw new BadRequestException(String.format("Could not add user: %s", ex.getMessage()));
        }
    }

    @Override
    public void addAuthData(AuthData authData) {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO authenticatedUsers (username, authToken) VALUES (?, ?)")){
                preparedStatement.setString(1, authData.username());
                preparedStatement.setString(2, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException ex){
            throw new BadRequestException(String.format("Could not add authToken to database: %s", ex.getMessage()));
        }
    }

    @Override
    public AuthData getAuthData(String authToken) {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM authenticatedUsers WHERE authToken = ?")){
                preparedStatement.setString(1, authToken);
                var result = preparedStatement.executeQuery();
                result.next();
                return new AuthData(result.getString(1), authToken);
            }
        } catch (DataAccessException | SQLException ex){
            return null;
        }
    }

    @Override
    public void removeAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authenticatedUsers WHERE authToken = ?")){
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException ex){
            throw new BadRequestException(String.format("Database error: %s", ex.getMessage()));
        }
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()){
            for (String sqlStatement : tableClearStatements){
                try (var preppedStatement = conn.prepareStatement(sqlStatement)){
                    preppedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException ex){}
    }

    @Override
    public int newGame(String gameName) {
        try (var conn = DatabaseManager.getConnection()){
            try (var preppedStatement = conn.prepareStatement("INSERT INTO games (gameName, game) VALUES (?, ?)")){
                preppedStatement.setString(1, gameName);
                preppedStatement.setString(2, gson.toJson(new ChessGame()));
                preppedStatement.executeUpdate();
            }
            try (var preppedStatement = conn.prepareStatement("SELECT gameID FROM games WHERE gameName = ?")){
                preppedStatement.setString(1, gameName);
                var result = preppedStatement.executeQuery();
                result.next();
                return result.getInt(1);
            }
        } catch (DataAccessException | SQLException ex){
            return -1;
        }
    }

    @Override
    public Collection<GameData> getGames() {
        try (var conn = DatabaseManager.getConnection()){
            try (var preppedStatement = conn.prepareStatement("SELECT * FROM games")){
                var result = preppedStatement.executeQuery();
                ArrayList<GameData> games = new ArrayList<>();
                while (result.next()){
                    int gameID = result.getInt(1);
                    String whiteUsername = result.getString(2);
                    String blackUsername = result.getString(3);
                    String gameName = result.getString(4);
                    String chessGameAsJson = result.getString(5);
                    ChessGame chessGame = gson.fromJson(chessGameAsJson, ChessGame.class);
                    games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                }
                return games;
            }
        } catch (SQLException | DataAccessException ex){
            return List.of();
        }
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGameData) {

    }

    private String cryptographizePassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean decryptographizePassword(String password, String crypographizedPassword){
        return Objects.equals(crypographizedPassword, cryptographizePassword(password));
    }
}
