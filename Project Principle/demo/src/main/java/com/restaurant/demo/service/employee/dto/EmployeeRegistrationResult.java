package com.restaurant.demo.service.employee.dto;

import com.restaurant.demo.model.Employee;

public class EmployeeRegistrationResult {
    private final Employee employee;
    private final EmployeeCredentials credentials;

    public EmployeeRegistrationResult(Employee employee, EmployeeCredentials credentials) {
        this.employee = employee;
        this.credentials = credentials;
    }

    public Employee getEmployee() {
        return employee;
    }

    public EmployeeCredentials getCredentials() {
        return credentials;
    }
}
