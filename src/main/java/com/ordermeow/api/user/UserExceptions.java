package com.ordermeow.api.user;

public class UserExceptions {

    public static class UserAlreadyExistsException extends RuntimeException {
        UserAlreadyExistsException(String username) {
            super("The username already exists: " + username);
        }
    }
}
