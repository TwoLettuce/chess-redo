package exception;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    public int httpCode;
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(int httpCode, String message){
        super(message);
        this.httpCode = httpCode;
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }
}
