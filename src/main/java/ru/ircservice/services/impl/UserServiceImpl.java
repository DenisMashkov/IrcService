package ru.ircservice.services.impl;

import ru.ircservice.model.Session;
import ru.ircservice.model.User;
import ru.ircservice.services.UserService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static ru.ircservice.utils.PasswordUtils.hashPassword;

public class UserServiceImpl implements UserService {

    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<User, Session> usersSessions = new ConcurrentHashMap<>();
    private Map<Session, User> sessionsWithUsers = new ConcurrentHashMap<>();

    @Override
    public User getUser(String userName, String password) {
        return users.compute(userName, (key, value) ->
                value != null ? value : createUser(userName, password)
        );
    }

    @Override
    public User getUser(Session session) {
        if (!sessionsWithUsers.containsKey(session)) {
            throw new RuntimeException("Session not found");
        }

        return sessionsWithUsers.get(session);
    }

    @Override
    public Session getSession(User user) {
        if (usersSessions.containsKey(user)) {
            return usersSessions.get(user);
        } else {

            Session session;

            synchronized (user) {
                session = usersSessions.compute(user, (key, value) -> createSession());
                sessionsWithUsers.put(session, user);
            }

            return session;
        }
    }

    private Session createSession() {
        return new Session(
                UUID.randomUUID().toString()
        );
    }

    private User createUser(String userName, String password) {
        return new User(
                userName,
                hashPassword(password)
        );
    }
}
