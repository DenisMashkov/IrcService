package ru.ircservice.services.impl;

import ru.ircservice.model.ChatRoom;
import ru.ircservice.model.Session;
import ru.ircservice.model.User;
import ru.ircservice.services.ChatRoomService;
import ru.ircservice.services.IrcService;
import ru.ircservice.services.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.ircservice.utils.PasswordUtils.hashPassword;

/**
 * Please implement skipped methods.
 * IrcServer is a heart of our IrcChatServer project and is responsible for chat room creation,
 * users' and sessions' management in highly load and concurrent environment.
 * Service must keep all created objects (sessions, users, chat room) in memory with no persistence.
 * Please pay extra attention to concurrency and thread safety. Try not to abuse thread locks a lot.
 */

public class IrcServiceImpl implements IrcService {

    private final UserService userService;
    private final ChatRoomService chatRoomService;

    public IrcServiceImpl(UserService userService, ChatRoomService chatRoomService) {
        this.userService = userService;
        this.chatRoomService = chatRoomService;
    }

    @Override
    public Session loginUser(String userName, String password) {
        User user = userService.getUser(userName, password);

        if (!Objects.equals(hashPassword(password), user.getPasswordHash())) {
            throw new RuntimeException("Wrong password!");
        }

        return userService.getSession(user);
    }

    @Override
    public List<String> joinRoom(Session session, String roomName) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(roomName);
        chatRoomService.bindSessionToRoom(session, chatRoom);

        return chatRoom.getLastTenMessages();
    }

    @Override
    public void leaveCurrentRoom(Session session) {
        chatRoomService.leaveCurrentRoom(session);
    }

    @Override
    public List<String> showUsersInCurrentRoom(Session session) {
        return chatRoomService.showUsersInCurrentRoom(session);
    }

    @Override
    public void sendMessageToCurrentRoom(Session session, String msg) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(session);
        Set<Session> sessions = chatRoomService.getRoomSessions(chatRoom);

        sessions.forEach(it -> {
            try {
                User user = userService.getUser(it);

                if (!it.equals(session)) {
                    sendToUserChannel(user.getUserName(), msg);
                    chatRoom.addMessage(msg);
                }
                
            } catch (RuntimeException e) {
                // log e
            }
        });
    }

    /**
     * For the sake of simplicity lets assume that user's out channel is mocked with system.out
     *
     * @param userName recipient fo the message
     * @param msg      message
     */
    @Override
    public void sendToUserChannel(String userName, String msg) {
        System.out.println(String.format("Message %s sent to %s", msg, userName));
    }

}