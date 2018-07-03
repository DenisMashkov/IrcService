package ru.ircservice.services;

import ru.ircservice.model.Session;
import ru.ircservice.model.User;

public interface UserService {
    User getUser(String userName, String password);
    User getUser(Session session);

    Session getSession(User user);
}
