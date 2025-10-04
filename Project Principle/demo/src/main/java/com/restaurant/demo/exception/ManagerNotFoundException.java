package com.restaurant.demo.exception;

public class ManagerNotFoundException extends RuntimeException {

    public ManagerNotFoundException(String message) {
        super(message);
    }

    public ManagerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ManagerNotFoundException withEmail(String email) {
        return new ManagerNotFoundException("Manager not found with email: " + email);
    }

    public static ManagerNotFoundException withId(Long id) {
        return new ManagerNotFoundException("Manager not found with ID: " + id);
    }
}
