package ru.ircservice.model;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatRoom {
    private String name;
    private Deque<String> messages = new ConcurrentLinkedDeque<>();

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public List<String> getLastTenMessages() {
        Iterator<String> iterator = messages.descendingIterator();

        int counter = 0;

        List<String> lastMessages = new ArrayList<>();

        while (iterator.hasNext() && counter < 10) {
            lastMessages.add(iterator.next());
            counter++;
        }

        return lastMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(name, chatRoom.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "name='" + name + '\'' +
                '}';
    }
}
