package com.restaurant.demo.exception;

public class ManagerAlreadyExistsException extends RuntimeException {

    public ManagerAlreadyExistsException(String message) {
        super(message);
    }

    public ManagerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ManagerAlreadyExistsException withEmail(String email) {
        return new ManagerAlreadyExistsException("Manager already exists with email: " + email);
    }

    public static ManagerAlreadyExistsException withUsername(String username) {
        return new ManagerAlreadyExistsException("Manager already exists with username: " + username);
    }
}
