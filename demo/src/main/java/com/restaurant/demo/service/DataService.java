package com.restaurant.demo.service;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.model.Manager;
import com.restaurant.demo.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DataService {

    private final CartService cartService;
    private final List<User> users = new ArrayList<>();
    private final List<Employee> employees = new ArrayList<>();
    private final Manager manager;

    public DataService(CartService cartService) {
        this.cartService = cartService;

        manager = new Manager(1L, "Admin");

        users.add(new User(1, "admin", "Admin User", "Admin", "manager", Instant.now().toString()));
        users.add(new User(2, "employee1", "Employee One", "Employee One", "employee", Instant.now().toString()));
        users.add(new User(3, "customer1", "Customer One", "Customer One", "customer", Instant.now().toString()));

        employees.add(new Employee(1L, "Employee One", "Chef"));
        employees.add(new Employee(2L, "Employee Two", "Cashier"));

        
    }

    public List<User> getUsers() {
        return users;
    }

    public Optional<User> findUserById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    public User getCurrentManager() {
        return users.stream()
                .filter(u -> "manager".equalsIgnoreCase(u.getRole()))
                .findFirst()
                .orElse(null);
    }

    public List<CartItem> getCartForUser(int userId) {
        return cartService.getCartItems(Long.valueOf(userId));
    }

    public List<CartItem> getAllCartItems() {
        return cartService.getAllCartItems();
    }

    public Manager.SalesReport getSalesReport() {
        List<CartItem> cartItems = cartService.getAllCartItems();
        return manager.viewSalesReport(cartItems, users);
    }

  

    public Employee addEmployee(Employee employee) {
        Long newId = employees.stream().mapToLong(Employee::getId).max().orElse(0L) + 1L;
        employee.setId(newId);
        employees.add(employee);
        return employee;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public Optional<Employee> findEmployeeById(int id) {
        return employees.stream().filter(e -> e.getId() == id).findFirst();
    }

    public Optional<Employee> updateEmployee(int id, Employee employee) {
        return findEmployeeById(id).map(existingEmployee -> {
            existingEmployee.setName(employee.getName());
            existingEmployee.setPosition(employee.getPosition());
            return existingEmployee;
        });
    }

    public boolean deleteEmployeeById(int id) {
        return employees.removeIf(e -> e.getId() == id);
    }
}