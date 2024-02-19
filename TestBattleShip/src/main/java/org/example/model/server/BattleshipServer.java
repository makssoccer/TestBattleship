package org.example.model.server;


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.Map;

@WebSocket
public class BattleshipServer {

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        System.out.println("Client connected: " + session.getRemoteAddress().toString());

        SessionManager.addSession(session.getRemoteAddress().toString(), session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {

        UserSession userSession = SessionManager.getUserSession(session.getRemoteAddress().toString());

        if (userSession != null && userSession.getUsername() == null) {
            // Сохраняем имя пользователя
            userSession.setUsername(message);
            System.out.println("Username set for client " + session.getRemoteAddress().toString() + ": " + message);
            session.getRemote().sendString("How to fill out the playing field.");
        }

//        else if (message.equals("1") || message == "2") {
//           BattleshipField battleshipField = new BattleshipField();
//           userSession.setBattleshipField(battleshipField);
//           session.getRemote().sendString("create battleship field");
//           session.getRemote().sendString(String.valueOf(battleshipField.gameBoard));
//        }
        else {
            // Обрабатываем другие сообщения
            System.out.println("Handling other message from client " + session.getRemoteAddress().getHostName() + ": " + message);
        }
        System.out.println("Received message from client: " + message);
        // Здесь можно добавить логику обработки полученного сообщения

        Map<String, UserSession> map = SessionManager.sessions;
    }

//    @OnWebSocketMessage
//    public void onMessage(Session session, Object battleshipField) throws IOException {
//        UserSession userSession = SessionManager.getUserSession(session.getRemoteAddress().toString());
//        userSession.setBattleshipField((BattleshipField) battleshipField);
//    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Client disconnected: " + session.getRemoteAddress().getHostName());
        SessionManager.removeSession(session.getRemoteAddress().getHostName());
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("Error occurred for client " + session.getRemoteAddress().getHostName() + ": " + error.getMessage());
        SessionManager.removeSession(session.getRemoteAddress().getHostName());
    }
}
//    private static final Map<Session, String> players = new HashMap<>();
//
//    @OnWebSocketConnect
//    public void onConnect(Session session) throws Exception {
//        System.out.println("Connected: " + session.getRemoteAddress());
//        players.put(session, ""); // По умолчанию имя игрока пусто
//    }
//
//    @OnWebSocketClose
//    public void onClose(Session session, int statusCode, String reason) {
//        System.out.println("Closed: " + session.getRemoteAddress() + " " + statusCode + " " + reason);
//        players.remove(session);
//    }
//
//    @OnWebSocketMessage
//    public void onMessage(Session session, String message) {
//        System.out.println("Message from " + session.getRemoteAddress() + ": " + message);
//        // Здесь можно добавить логику обработки сообщений
//    }
//
//    public static void broadcast(String message) {
//        for (Session session : players.keySet()) {
//            try {
//                session.getRemote().sendString(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}