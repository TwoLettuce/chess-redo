package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.http.InternalServerErrorResponse;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            throw new InternalServerErrorException(String.format("Error: Could not configure database %s", ex.getMessage()));
        }
    }

    @Override
    public UserData getUser(String username) throws InternalServerErrorException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preppedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")){
                preppedStatement.setString(1, username);
                var result = preppedStatement.executeQuery();
                if (result.next()){
                    return new UserData(result.getString(1), result.getString(2), result.getString(3));
                } else {
                    return null;
                }
            }
        } catch (DataAccessException | SQLException ex){
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    @Override
    public void addUser(UserData userData) throws BadRequestException, InternalServerErrorException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")){
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException ex){
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    @Override
    public void addAuthData(AuthData authData) throws BadRequestException, InternalServerErrorException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO authenticatedUsers (username, authToken) VALUES (?, ?)")){
                preparedStatement.setString(1, authData.username());
                preparedStatement.setString(2, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException ex){
            throw new BadRequestException(String.format("Could not add authData to database: %s", ex.getMessage()));
        } catch (SQLException ex){
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws InternalServerErrorException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM authenticatedUsers WHERE authToken = ?")){
                preparedStatement.setString(1, authToken);
                var result = preparedStatement.executeQuery();
                if (result.next()){
                    return new AuthData(result.getString(1), authToken);
                }
                return null;
            }
        } catch (DataAccessException | SQLException ex){
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    @Override
    public void removeAuth(String authToken) throws InternalServerErrorException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authenticatedUsers WHERE authToken = ?")){
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException ex){
            throw new RuntimeException(String.format("Database error: %s", ex.getMessage()));
        } catch (SQLException ex){
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    @Override
    public void clear() throws InternalServerErrorException {
        try (var conn = DatabaseManager.getConnection()){
            for (String sqlStatement : tableClearStatements){
                try (var preppedStatement = conn.prepareStatement(sqlStatement)){
                    preppedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException ex){
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    @Override
    public int newGame(String gameName) throws InternalServerErrorException {
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
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    @Override
    public Collection<GameData> getGames() throws InternalServerErrorException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preppedStatement = conn.prepareStatement("SELECT * FROM games")){
                var result = preppedStatement.executeQuery();
                ArrayList<GameData> games = new ArrayList<>();
                while (result.next()){
                    games.add(buildGameDataFromResultSet(result));
                }
                return games;
            }
        } catch (DataAccessException | SQLException ex){
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    @Override
    public GameData getGame(int gameID) throws InternalServerErrorException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preppedStatement = conn.prepareStatement("SELECT * FROM games WHERE gameID = ?")) {
                preppedStatement.setInt(1, gameID);
                var result = preppedStatement.executeQuery();
                if (result.next()){
                    return buildGameDataFromResultSet(result);
                } else {
                    return null;
                }
            }
        } catch (DataAccessException ex){
            return null;
        } catch (SQLException ex){
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    @Override
    public void updateGame(int gameID, GameData updatedGameData) throws BadRequestException, InternalServerErrorException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preppedStatement = conn.prepareStatement("UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameid = ?")){
                preppedStatement.setString(1, updatedGameData.whiteUsername());
                preppedStatement.setString(2, updatedGameData.blackUsername());
                preppedStatement.setString(3, gson.toJson(updatedGameData.game()));
                preppedStatement.setInt(4, gameID);
                if (preppedStatement.executeUpdate()==0){
                    throw new DataAccessException("gameID not found");
                }
            }
        } catch (DataAccessException ex){
            throw new BadRequestException("Error: " + ex.getMessage());
        } catch (SQLException ex){
            throw new InternalServerErrorException("Error: Server took the L");
        }
    }

    private GameData buildGameDataFromResultSet(ResultSet result) throws SQLException {
        int gameID = result.getInt(1);
        String whiteUsername = result.getString(2);
        String blackUsername = result.getString(3);
        String gameName = result.getString(4);
        String chessGameAsJson = result.getString(5);
        ChessGame chessGame = gson.fromJson(chessGameAsJson, ChessGame.class);
        return  new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }
}
