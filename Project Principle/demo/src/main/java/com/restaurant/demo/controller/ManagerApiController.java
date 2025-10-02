package com.restaurant.demo.controller;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.model.Manager;
import com.restaurant.demo.model.User;
import com.restaurant.demo.service.CartService;
import com.restaurant.demo.service.employee.EmployeeService;
import com.restaurant.demo.service.employee.dto.EmployeeCredentials;
import com.restaurant.demo.service.employee.dto.EmployeeRegistrationRequest;
import com.restaurant.demo.service.employee.dto.EmployeeRegistrationResult;
import com.restaurant.demo.service.employee.dto.EmployeeUpdateRequest;
import com.restaurant.demo.service.manager.ManagerContext;
import com.restaurant.demo.service.manager.SalesReportService;
import org.springframework.http.HttpStatus;
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

// URL จะอยู่ภายใต้ /api/...
@RequestMapping("/api")
public class ManagerApiController {

    private final ManagerContext managerContext;
    private final EmployeeService employeeService;
    private final CartService cartService;
    private final SalesReportService salesReportService;

    public ManagerApiController(ManagerContext managerContext,
                                EmployeeService employeeService,
                                CartService cartService,
                                SalesReportService salesReportService) {
        this.managerContext = managerContext;
        this.employeeService = employeeService;
        this.cartService = cartService;
        this.salesReportService = salesReportService;
    }

    @GetMapping("/currentUser")
    public User getCurrentUser() {
        return managerContext.getCurrentManager();
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return employeeService.getEmployees();
    }

    @PostMapping("/employees")
    public ResponseEntity<EmployeeRegistrationResult> createEmployee(@RequestBody EmployeeRegistrationRequest request) {
        try {
            EmployeeRegistrationResult created = employeeService.registerEmployee(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/employees/{id}/credentials")
    public ResponseEntity<EmployeeCredentials> getEmployeeCredentials(@PathVariable int id) {
        return employeeService.getCredentialsFor(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable int id, @RequestBody EmployeeUpdateRequest request) {
        return employeeService.updateEmployee(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int id) {
        boolean removed = employeeService.deleteEmployee(id);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/carts")
    public List<CartItem> getAllCarts() {
        return cartService.getAllCartItems();
    }

    @GetMapping("/carts/{userId}")
    public List<CartItem> getCartForUser(@PathVariable int userId) {
        return cartService.getCartByCustomerId(userId);
    }

    @GetMapping("/reports/sales")
    public Manager.SalesReport getSalesReport() {
        return salesReportService.getDailySalesReport();
    }
}
