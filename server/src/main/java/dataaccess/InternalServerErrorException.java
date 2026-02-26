package dataaccess;

import exception.DataAccessException;

public class InternalServerErrorException extends DataAccessException {
    public int httpCode = 500;
    public InternalServerErrorException(String message) {
        super(message);
    }
}
