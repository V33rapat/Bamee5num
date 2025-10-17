package com.restaurant.demo.exception;

public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(String message) {
        super(message);
    }

    public MenuItemNotFoundException(Long id) {
        super("Menu item not found with id: " + id);
    }
}
