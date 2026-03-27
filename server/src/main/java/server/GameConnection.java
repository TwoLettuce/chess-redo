package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;

public class GameConnection {
    public ArrayList<Session> connectedUsers = new ArrayList<>();

    public void addUser(Session session){
        connectedUsers.add(session);
    }

    public void removeUser(Session session){
        connectedUsers.remove(session);
    }

    public void broadcastMessage(ServerMessage message) throws IOException {
        for (Session c : connectedUsers){
            c.getRemote().sendString(new Gson().toJson(message));
        }
    }
}
