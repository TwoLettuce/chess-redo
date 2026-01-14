package dataaccess;

public class AlreadyTakenException extends DataAccessException {
    public int httpCode = 403;
    public AlreadyTakenException(String message) {
        super(message);
    }
}
