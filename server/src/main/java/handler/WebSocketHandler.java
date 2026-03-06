package handler;

import io.javalin.websocket.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    public void handleConnect(WsConnectContext ctx){
        ctx.enableAutomaticPings();
    }

    public void handleMessage(WsMessageContext ctx){
        ctx.send("Here's a message!");
    }

    public void handleClose(WsCloseContext ctx){
        System.out.println("Connection closed");
    }
}
