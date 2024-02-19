package org.example.model.server;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    public static final Map<String, UserSession> sessions = new HashMap<>();

    public static void addSession(String sessionId, Session session) {
        sessions.put(sessionId, new UserSession(session));
    }

    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public static Session getSession(String sessionId) {
        UserSession userSession= sessions.get(sessionId);
        return userSession.getSession();
    }

    public static Map getMap() {

        return sessions;
    }
    public static UserSession getUserSession(String hostName) {
        return sessions.get(hostName);
    }

//    private static final Map<String, UserSession> sessions = new ConcurrentHashMap<>();
//
//    public static void addUserSession(String username, Session session) {
//        sessions.put(session.getRemoteAddress().getHostName(), new UserSession(username, session));
//    }
//
//    public static void removeUserSession(String sessionId) {
//        sessions.remove(sessionId);
//    }
//
//    public static UserSession getUserSession(String sessionId) {
//        return sessions.get(sessionId);
//    }
}