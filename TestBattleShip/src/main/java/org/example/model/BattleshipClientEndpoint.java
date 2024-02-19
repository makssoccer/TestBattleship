package org.example.model;



import javax.websocket.*;
import java.io.IOException;
import java.util.Scanner;


@ClientEndpoint
public class BattleshipClientEndpoint {
    private  Session session;
    public  BattleshipField battleshipField;

//    private Scanner scanner = new Scanner(System.in);

    public  Session getSession() {
        return this.session;
    }

    public  void setSession(Session session) {
        this.session = session;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        battleshipField = new BattleshipField();
        System.out.println("Connected to server");
    }


    @OnMessage
    public void onMessage(String message) {
//        String userInput;
//        userInput = reader.readLine()) != null) {
//        sendMessage(userInput);
        System.out.println("Message from server: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Connection closed");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error occurred: " + throwable.getMessage());
    }

//    public void sendMessage(String message) {
//        session.getAsyncRemote().sendText(message);
//    }
    public  void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }
    public void sendBattleshipField () throws EncodeException, IOException {
        session.getBasicRemote().sendObject(battleshipField);
    }

}