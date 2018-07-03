package ru.ircservice.utils;

public class PasswordUtils {

    private PasswordUtils() {}

    public static String hashPassword(String password) {
        return String.format("hash(%s)", password); // need normal hash function
    }

}
