package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.InternalServerErrorException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import server.GameConnection;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final DataAccess dataAccess;
    HashMap<Integer, GameConnection> connections = new HashMap<>();
    Gson gson = new Gson();

    public WebSocketHandler(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public void handleConnect(WsConnectContext ctx){
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    public void handleMessage(WsMessageContext ctx){
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()){
            case CONNECT -> {
                try {
                    connectToGame(ctx, command);
                } catch (IOException | InternalServerErrorException e) {
                    System.out.println("Error connecting to game");
                }
            }
            case MAKE_MOVE -> {
                command = gson.fromJson(ctx.message(), MakeMoveCommand.class);

            }
        }
    }

    private void connectToGame(WsMessageContext ctx, UserGameCommand command) throws IOException, InternalServerErrorException {
        AuthData authData = dataAccess.getAuthData(command.getAuthToken());
        if (authData == null){
            ErrorMessage errorLoadingGame = new ErrorMessage(
                    ServerMessage.ServerMessageType.ERROR, "Error: Invalid AuthToken"
            );
            ctx.session.getRemote().sendString(gson.toJson(errorLoadingGame));
        }

        String username = authData.username();
        GameData gameData = dataAccess.getGame(command.getGameID());
        if (gameData == null){
            ErrorMessage errorLoadingGame = new ErrorMessage(
                    ServerMessage.ServerMessageType.ERROR, "Error: Invalid gameID: " + command.getGameID()
            );
            ctx.session.getRemote().sendString(gson.toJson(errorLoadingGame));
        }
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
        ctx.session.getRemote().sendString(gson.toJson(loadGameMessage));

        if (connections.containsKey(command.getGameID())) {
            String color;
            if (Objects.equals(username, gameData.whiteUsername())){
                color = "white.";
            } else if (Objects.equals(username, gameData.blackUsername())){
                color = "black.";
            } else {
                color = "an observer.";
            }
            Notification notification = new Notification(
                    ServerMessage.ServerMessageType.NOTIFICATION, username + "has joined as " + color
            );
            connections.get(command.getGameID()).broadcastMessage(notification);
            connections.get(command.getGameID()).addUser(ctx.session);
        } else {
            GameConnection connection = new GameConnection();
            connection.addUser(ctx.session);
            connections.put(command.getGameID(), connection);
        }
    }

    public void handleClose(@NotNull WsCloseContext ctx){
        for (int key : connections.keySet()){
            if (connections.get(key).connectedUsers.contains(ctx.session)){
                connections.get(key).removeUser(ctx.session);
                break;
            }
        }
        System.out.println("Connection closed");
    }
}
