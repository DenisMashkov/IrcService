package ru.ircservice.model;

import java.util.Objects;

public class Session {
    private final String id;
    /**
     * @param id unique session id
     */
    public Session(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                '}';
    }
}
