package com.restaurant.demo.controller;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.model.Manager;
import com.restaurant.demo.model.User;
import com.restaurant.demo.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ManagerApiController {

    private final DataService dataService;

    public ManagerApiController(DataService dataService) {
        this.dataService = dataService;
    }
    // Manager) เพื่อจัดการพนักงาน
    @GetMapping("/currentUser")
    public User getCurrentUser() {
        return dataService.getCurrentManager();
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return dataService.getEmployees();
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee created = dataService.addEmployee(employee);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable int id, @RequestBody Employee employee) {
        return dataService.updateEmployee(id, employee)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable int id) {
        boolean removed = dataService.deleteEmployeeById(id);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Manager ดูรายการ Carts
    @GetMapping("/carts")
    public List<CartItem> getAllCarts() {
        return dataService.getAllCartItems();
    }

    @GetMapping("/carts/{userId}")
    public List<CartItem> getCartForUser(@PathVariable int userId) {
        return dataService.getCartForUser(userId);
    }

    // Manager ดูSales Report
    @GetMapping("/reports/sales")
    public Manager.SalesReport getSalesReport() {
        return dataService.getSalesReport();
    }
}
