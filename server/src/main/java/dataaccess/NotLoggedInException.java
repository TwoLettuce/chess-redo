package dataaccess;

public class NotLoggedInException extends DataAccessException {
    public int httpCode = 401;
    public NotLoggedInException(String message) {
        super(message);
    }
}
