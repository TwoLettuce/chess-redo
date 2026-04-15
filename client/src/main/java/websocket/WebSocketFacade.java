package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    Gson gson = new Gson();
    NotificationHandler notifier;
    URI serverURI;

    public WebSocketFacade(String url, NotificationHandler notifier) throws UnableToConnectException {
        this.notifier = notifier;

        url = url.replace("http", "ws");
        try {
            serverURI = new URI(url + "/ws");
        } catch (URISyntaxException e) {
            throw new UnableToConnectException("Invalid URL");
        }
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            session = container.connectToServer(this, serverURI);
        } catch (IOException | DeploymentException e) {
            throw new UnableToConnectException("Unable to establish connection with server.");
        }

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @OnMessage
            public void onMessage (String message){
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()){
                    case NOTIFICATION -> notifier.notify(gson.fromJson(message, Notification.class));
                    case LOAD_GAME -> notifier.notify(gson.fromJson(message, LoadGameMessage.class));
                    case ERROR -> notifier.notify(gson.fromJson(message, ErrorMessage.class));
                    default -> notifier.notify(new ErrorMessage("unrecognized server message"));
                }
            }
        });
    }

    public void connect(UserGameCommand connectCommand){
        sendMessage(gson.toJson(connectCommand));
    }

    private void sendMessage(String message){
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void leave(UserGameCommand leaveCommand){
        sendMessage(gson.toJson(leaveCommand));
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void makeMove(MakeMoveCommand command){
        sendMessage(gson.toJson(command));
    }


}
