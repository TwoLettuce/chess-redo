package websocket;

import jakarta.websocket.CloseReason;
import org.glassfish.tyrus.core.WebSocketException;

public class UnableToConnectException extends WebSocketException {
    public UnableToConnectException(String message) {
        super(message);
    }

    @Override
    public CloseReason getCloseReason() {
        return new CloseReason(new ConnectionErrorCode(500), getMessage());
    }

    private class ConnectionErrorCode implements CloseReason.CloseCode {
        int code;

        public ConnectionErrorCode(int code){
            this.code = code;
        }
        @Override
        public int getCode() {
            return code;
        }
    }
}
