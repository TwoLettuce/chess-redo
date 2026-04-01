package websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;

public interface NotificationHandler {
    void notify(Notification message);
    void notify(ErrorMessage message);
    void notify(LoadGameMessage message);
}
