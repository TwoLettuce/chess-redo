package dataaccess;

public class GameNotFoundException extends RuntimeException {
    public int httpCode = 400;
    public GameNotFoundException(String message) {
        super(message);
    }
}
