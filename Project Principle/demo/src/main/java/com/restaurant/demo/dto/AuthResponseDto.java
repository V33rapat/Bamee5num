package com.restaurant.demo.dto;

import java.time.LocalDateTime;

public class AuthResponseDto {

    private boolean success;
    private String message;
    private Long customerId;
    private String username;
    private String email;
    private String name;
    private String phone;
    private LocalDateTime timestamp;

    // Constructors
    public AuthResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public AuthResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public AuthResponseDto(boolean success, String message, Long customerId, String username, String email, String name, String phone) {
        this.success = success;
        this.message = message;
        this.customerId = customerId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.timestamp = LocalDateTime.now();
    }

    // Static factory methods for common responses
    public static AuthResponseDto success(String message) {
        return new AuthResponseDto(true, message);
    }

    public static AuthResponseDto success(String message, Long customerId, String username, String email, String name, String phone) {
        return new AuthResponseDto(true, message, customerId, username, email, name, phone);
    }

    public static AuthResponseDto error(String message) {
        return new AuthResponseDto(false, message);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
