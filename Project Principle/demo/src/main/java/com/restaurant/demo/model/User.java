package com.restaurant.demo.model;

public class User {

    private int id;
    private String username;
    private String fullName;
    private String name; 
    private String createdAt; // ISO string or date string
    private String role; //"customer", "employee", "manager"

    public User() {}

    public User(int id, String username, String fullName, String name, String role, String createdAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.name = name;
        this.role = role;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
