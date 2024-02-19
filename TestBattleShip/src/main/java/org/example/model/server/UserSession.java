package org.example.model.server;

import org.eclipse.jetty.websocket.api.Session;
import org.example.model.BattleshipField;

import java.util.Objects;

public class UserSession {
    private String username;
    private Session session;

    public BattleshipField battleshipField;

    public  BattleshipField opponentBattleshipField;

    public UserSession(Session session) {
        this.session = session;
    }


    public String getUsername() {
        return username;
    }

    public BattleshipField getBattleshipField() {
        return battleshipField;
    }

    public BattleshipField getOpponentBattleshipField() {
        return opponentBattleshipField;
    }

    public void setOpponentBattleshipField(BattleshipField opponentBattleshipField) {
        this.opponentBattleshipField = opponentBattleshipField;
    }
    public void setBattleshipField(BattleshipField battleshipField) {
        this.battleshipField = battleshipField;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return session.equals(that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session);
    }
}