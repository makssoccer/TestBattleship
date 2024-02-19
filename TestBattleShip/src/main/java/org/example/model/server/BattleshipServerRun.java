package org.example.model.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;


public class BattleshipServerRun {
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        {
            Server server = new Server(SERVER_PORT);

            WebSocketHandler wsHandler = new WebSocketHandler() {
                @Override
                public void configure(WebSocketServletFactory factory) {
                    factory.register(BattleshipServer.class);
                }
            };

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            context.setHandler(wsHandler);

            server.setHandler(context);

            try {
                server.start();
                server.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
