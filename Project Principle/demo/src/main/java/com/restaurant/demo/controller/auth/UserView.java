package com.restaurant.demo.controller.auth;

import com.restaurant.demo.model.User;

public class UserView {

    private int id;
    private String username;
    private String fullName;
    private String name;
    private String role;
    private String createdAt;

    public static UserView from(User user) {
        UserView view = new UserView();
        view.setId(user.getId());
        view.setUsername(user.getUsername());
        view.setFullName(user.getFullName());
        view.setName(user.getName());
        view.setRole(user.getRole());
        view.setCreatedAt(user.getCreatedAt());
        return view;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
