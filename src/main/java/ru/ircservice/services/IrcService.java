package ru.ircservice.services;

import ru.ircservice.model.Session;

import java.util.List;

public interface IrcService {

    /**
     * Try to login user. All users are kept in memory with no persistence.
     * If user does not exist create it else compare passwords and try to login.
     * @param userName user name
     * @param password password
     * @return [[Session]] info if the user was logged
     *        or throw exception if error happened.
     *        [[Session]] object represents session token and must be supplied
     *        to all session aware methods such as join, leave or sendMessage
     * @throws RuntimeException if password does not match
     */
    Session loginUser(String userName, String password);

    /**
     * Try to join chat room with a maximum capacity of 10 users.
     * If chat room does not exist - create it first.
     * If client’s limit exceeded - fail with error, otherwise join chat room and
     * send conversation history (last 10 messages).
     * Service should be able to support many chat rooms.
     * @param session user's session
     * @param roomName room name
     * @return Last 10 sent to the room messages or throw RuntimeException
     *        if the room capacity exceeded
     *        or user has not been logged in.
     */
    List<String> joinRoom(Session session, String roomName);

    /**
     * Try to leave current chat room.
     * @param session user's session
     * @throws RuntimeException if there was no active room or user has not been logged in
     */
    void leaveCurrentRoom(Session session);

    /**
     * Try to get all usernames in the current chat room.
     * @return List with usernames or throw RuntimeException
     *        if there was no active room or user has not been logged in
     */
    List<String> showUsersInCurrentRoom(Session session);

    /**
     * Try to send string message to all users in the current room.
     * You must use sendToUserChannel to send message to user's channel.
     * @throws RuntimeException if there was no active room or user has not been logged in
     */
    void sendMessageToCurrentRoom(Session session, String msg);

    void sendToUserChannel(String userName, String msg);
}
