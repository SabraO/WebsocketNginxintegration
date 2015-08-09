package org.test.ws;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/echo")
public class EchoServer {

    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection");
        try {
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("Server: Message from " + session.getId() + ": " + message);
        try {
            session.getBasicRemote().sendText(message);
            for (Session userSession : session.getOpenSessions()) {
                System.out.println("Server: Sending to " + userSession.getId());
                if(userSession.isOpen() && userSession.getId()!=session.getId()){
                    userSession.getAsyncRemote().sendText(message);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session){
        System.out.println("Session " + session.getId() + " has ended");
    }

}
