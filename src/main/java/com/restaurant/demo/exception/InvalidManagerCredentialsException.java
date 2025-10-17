package com.restaurant.demo.exception;

public class InvalidManagerCredentialsException extends RuntimeException {

    public InvalidManagerCredentialsException(String message) {
        super(message);
    }

    public InvalidManagerCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidManagerCredentialsException forLogin() {
        return new InvalidManagerCredentialsException("Invalid email or password");
    }
}
