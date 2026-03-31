package handler;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.InternalServerErrorException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import server.GameConnection;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final UserService userService;
    private final GameService gameService;
    HashMap<Integer, GameConnection> connections = new HashMap<>();
    Gson gson = new Gson();

    public WebSocketHandler(UserService userService, GameService gameService){
        this.userService = userService;
        this.gameService = gameService;
    }

    public void handleConnect(WsConnectContext ctx){
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    public void handleMessage(WsMessageContext ctx) {
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> {
                try {
                    connectToGame(ctx, command);
                } catch (IOException | InternalServerErrorException e) {
                    System.out.println("Error connecting to game");
                }
            }
            case MAKE_MOVE -> {
                MakeMoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                try {
                    makeMove(ctx, moveCommand);
                } catch (Exception ex) {
                    System.out.println("Error making move");
                }
            }
        }
    }

    private void makeMove(WsMessageContext ctx, MakeMoveCommand command) throws InternalServerErrorException, IOException {
        GameData gameData = gameService.getGame(command.getGameID());
        String username = userService.getUsername(command.getAuthToken());
        if (gameData == null || username == null){
            ErrorMessage error = new ErrorMessage("Error: Game/User not found");
            sendMessage(ctx.session, gson.toJson(error));
            return;
        }

        try {
            gameData.game().makeMove(command.getMove());
        } catch (InvalidMoveException ex){
            ErrorMessage error = new ErrorMessage("Error: Invalid move");
            sendMessage(ctx.session, gson.toJson(error));
            return;
        }

        gameService.updateGame(gameData);
        Notification moveMade = new Notification(username + " has made the move " + moveToString(command.getMove()) + ".");
        connections.get(gameData.gameID()).broadcastMessage(ctx.session, moveMade);

        Notification statusUpdate = checkGameStatus(gameData.game());
        if (statusUpdate != null){
            connections.get(gameData.gameID()).broadcastMessage(null, statusUpdate);
        }

        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
        connections.get(gameData.gameID()).broadcastMessage(null, loadGameMessage);
    }




    private void connectToGame(WsMessageContext ctx, UserGameCommand command) throws IOException, InternalServerErrorException {
        GameData gameData = gameService.getGame(command.getGameID());
        String username = userService.getUsername(command.getAuthToken());
        if (gameData == null || username == null){
            ErrorMessage error = new ErrorMessage("Error: Game/User not found");
            sendMessage(ctx.session, gson.toJson(error));
            return;
        }

        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
        sendMessage(ctx.session, gson.toJson(loadGameMessage));

        if (connections.containsKey(command.getGameID())) {
            String color;
            if (Objects.equals(username, gameData.whiteUsername())){
                color = "white.";
            } else if (Objects.equals(username, gameData.blackUsername())){
                color = "black.";
            } else {
                color = "an observer.";
            }
            Notification notification = new Notification(username + " has joined as " + color);
            connections.get(command.getGameID()).broadcastMessage(null, notification);
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

    private Notification checkGameStatus(ChessGame game) {
        ChessGame.TeamColor[] colors = {ChessGame.TeamColor.WHITE, ChessGame.TeamColor.BLACK};
        Notification notification = null;
        for (var color : colors){
            if (game.isInCheckmate(color)){
                if (color == ChessGame.TeamColor.WHITE){
                    notification = new Notification(color + " is in checkmate. Black wins!");
                } else {
                    notification = new Notification(color + " is in checkmate. White wins!");
                }
            } else if (game.isInCheck(color)){
                notification = new Notification(color + " is in check.");
            } else if (game.isInStalemate(color)){
                notification = new Notification("Draw! The game ends in stalemate!");
            }
        }
        return notification;
    }

    private String moveToString(ChessMove move) {
        char[] arr = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        return String.valueOf(arr[move.getStartPosition().getColumn()-1]) + move.getStartPosition().getRow() +
                "->" +
                arr[move.getEndPosition().getColumn()-1] + move.getEndPosition().getRow();
    }

    private void sendMessage(Session session, String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
