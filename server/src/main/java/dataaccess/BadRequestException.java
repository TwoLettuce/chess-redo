package dataaccess;

public class BadRequestException extends DataAccessException {
    public int httpCode = 400;
    public BadRequestException(String message) {
        super(message);
    }
}
