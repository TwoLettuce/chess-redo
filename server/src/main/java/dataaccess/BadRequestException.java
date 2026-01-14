package dataaccess;

public class BadRequestException extends RuntimeException {
    public int httpCode = 400;
    public BadRequestException(String message) {
        super(message);
    }
}
