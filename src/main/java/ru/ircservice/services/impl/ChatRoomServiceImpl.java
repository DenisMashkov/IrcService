package ru.ircservice.services.impl;

import ru.ircservice.model.ChatRoom;
import ru.ircservice.model.Session;
import ru.ircservice.services.ChatRoomService;
import ru.ircservice.services.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class ChatRoomServiceImpl implements ChatRoomService {

    private final UserService userService;

    public ChatRoomServiceImpl(UserService userService) {
        this.userService = userService;
    }

    private Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Session, ChatRoom> sessionsWithChatRoom = new ConcurrentHashMap<>();
    private ConcurrentHashMap<ChatRoom, Set<Session>> chatRoomsSessions = new ConcurrentHashMap<>();

    @Override
    public ChatRoom getChatRoom(String roomName) {
        return chatRooms.compute(roomName, (key, value) ->
                value != null ? value : createChatRoom(roomName)
        );
    }

    @Override
    public ChatRoom getChatRoom(Session session) {
        if (!sessionsWithChatRoom.containsKey(session)) {
            throw new RuntimeException("Session not found");
        }

        return sessionsWithChatRoom.get(session);
    }

    @Override
    public Set<Session> getRoomSessions(ChatRoom chatRoom) {
        if (!chatRoomsSessions.containsKey(chatRoom)) {
            throw new RuntimeException("Room not found");
        }

        return chatRoomsSessions.get(chatRoom);
    }

    private ChatRoom createChatRoom(String roomName) {
        return new ChatRoom(
                roomName
        );
    }

    @Override
    public void bindSessionToRoom(Session session, ChatRoom chatRoom) {

        synchronized (session) {

            sessionsWithChatRoom.put(session, chatRoom);
            chatRoomsSessions.compute(chatRoom, (key, value) -> {
                Set<Session> sessions =
                        // todo need think about synchronizedSet
                        value == null ? Collections.synchronizedSet(new HashSet<>()) : value;

                if (sessions.size() < 10) {
                    sessions.add(session);
                    return sessions;
                } else {
                    throw new RuntimeException("Chat room is full");
                }
            });
        }

    }

    @Override
    public void leaveCurrentRoom(Session session) {
        ChatRoom chatRoom = getChatRoom(session);
        Set<Session> sessions = getRoomSessions(chatRoom);

        synchronized (session) {

            sessionsWithChatRoom.remove(session);
            sessions.remove(session);

        }
    }

    @Override
    public List<String> showUsersInCurrentRoom(Session session) {
        ChatRoom chatRoom = getChatRoom(session);
        Set<Session> sessions = getRoomSessions(chatRoom);

        return sessions
                .stream()
                .map(it -> {
                    try {
                        return userService.getUser(session).getUserName();
                    } catch (RuntimeException e) {
                        // log e
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
