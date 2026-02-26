package dataaccess;

import exception.DataAccessException;

public class GameNotFoundException extends DataAccessException {
    public int httpCode = 400;
    public GameNotFoundException(String message) {
        super(message);
    }
}
