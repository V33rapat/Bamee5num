package com.restaurant.demo.service.employee.dto;

public class EmployeeCredentials {
    private final String username;
    private final String loginCode;

    public EmployeeCredentials(String username, String loginCode) {
        this.username = username;
        this.loginCode = loginCode;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginCode() {
        return loginCode;
    }
}
