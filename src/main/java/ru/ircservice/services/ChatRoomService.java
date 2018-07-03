package ru.ircservice.services;

import ru.ircservice.model.ChatRoom;
import ru.ircservice.model.Session;

import java.util.List;
import java.util.Set;

public interface ChatRoomService {
    ChatRoom getChatRoom(String roomName);
    ChatRoom getChatRoom(Session session);
    Set<Session> getRoomSessions(ChatRoom chatRoom);

    void bindSessionToRoom(Session session, ChatRoom chatRoom);
    void leaveCurrentRoom(Session session);
    List<String> showUsersInCurrentRoom(Session session);
}
