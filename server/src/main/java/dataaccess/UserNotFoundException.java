package dataaccess;

import exception.DataAccessException;

public class UserNotFoundException extends DataAccessException {
    public int httpCode = 401;
    public UserNotFoundException(String message) {
        super(message);
    }
}
