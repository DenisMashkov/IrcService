package ru.ircservice;

import ru.ircservice.model.Session;
import ru.ircservice.services.ChatRoomService;
import ru.ircservice.services.IrcService;
import ru.ircservice.services.UserService;
import ru.ircservice.services.impl.ChatRoomServiceImpl;
import ru.ircservice.services.impl.IrcServiceImpl;
import ru.ircservice.services.impl.UserServiceImpl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class IrcServer {

    private static final int threadCount = 5;

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        ChatRoomService chatRoomService = new ChatRoomServiceImpl(userService);

        IrcService ircService = new IrcServiceImpl(userService, chatRoomService);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(new Worker(ircService));
        }
    }

    private static class Worker implements Runnable {

        private final IrcService ircService;
        private final String userName = "User" + System.currentTimeMillis();
        private final String password = "password";
        private final String roomName = "room1";

        public Worker(IrcService ircService) {
            this.ircService = ircService;
        }

        @Override
        public void run() {
            Session session = ircService.loginUser(userName, password);

            System.out.println(String.format("User: %s Session: %s", userName, session));

            ircService.joinRoom(session, roomName);
            ircService.sendMessageToCurrentRoom(session, String.format("User: %s Message: %d", userName, System.currentTimeMillis()));

            List<String> messages = ircService.joinRoom(session, roomName);

            System.out.println(String.format("User: %s Size: %d", userName, messages.size()));

        }
    }
}
