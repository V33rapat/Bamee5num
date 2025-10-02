package com.restaurant.demo.service.employee;

import com.restaurant.demo.model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction over employee persistence. Keeps higher level services
 * decoupled from storage concerns.
 */
public interface EmployeeDirectory {
    List<Employee> findAll();

    Optional<Employee> findById(int id);

    Employee save(Employee employee);

    boolean deleteById(int id);

    int nextIdentity();
}
