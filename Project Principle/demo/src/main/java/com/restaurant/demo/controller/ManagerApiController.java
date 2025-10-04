package com.restaurant.demo.controller;

import com.restaurant.demo.dto.MenuItemRequest;
import com.restaurant.demo.dto.MenuItemResponse;
import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.model.Manager;
import com.restaurant.demo.model.MenuItem;
import com.restaurant.demo.model.User;
import com.restaurant.demo.service.CartService;
import com.restaurant.demo.service.MenuItemService;
import com.restaurant.demo.service.employee.EmployeeService;
import com.restaurant.demo.service.employee.dto.EmployeeCredentials;
import com.restaurant.demo.service.employee.dto.EmployeeRegistrationRequest;
import com.restaurant.demo.service.employee.dto.EmployeeRegistrationResult;
import com.restaurant.demo.service.employee.dto.EmployeeUpdateRequest;
import com.restaurant.demo.service.manager.ManagerContext;
import com.restaurant.demo.service.manager.SalesReportService;
import jakarta.validation.Valid;
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
import java.util.stream.Collectors;

@RestController

// URL จะอยู่ภายใต้ /api/...
@RequestMapping("/api")
public class ManagerApiController {

    private final ManagerContext managerContext;
    private final EmployeeService employeeService;
    private final CartService cartService;
    private final SalesReportService salesReportService;
    private final MenuItemService menuItemService;

    public ManagerApiController(ManagerContext managerContext,
                                EmployeeService employeeService,
                                CartService cartService,
                                SalesReportService salesReportService,
                                MenuItemService menuItemService) {
        this.managerContext = managerContext;
        this.employeeService = employeeService;
        this.cartService = cartService;
        this.salesReportService = salesReportService;
        this.menuItemService = menuItemService;
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
        return cartService.getCartItems(Long.valueOf(userId));
    }

    @GetMapping("/reports/sales")
    public Manager.SalesReport getSalesReport() {
        return salesReportService.getDailySalesReport();
    }

    // ===== Menu Management Endpoints =====

    // Task 3.1: POST /api/manager/menu-items - Create new menu item
    @PostMapping("/manager/menu-items")
    public ResponseEntity<MenuItemResponse> createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse response = menuItemService.createMenuItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Task 3.2: PUT /api/manager/menu-items/{id} - Update existing menu item
    @PutMapping("/manager/menu-items/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse response = menuItemService.updateMenuItem(id, request);
        return ResponseEntity.ok(response);
    }

    // Task 3.3: GET /api/manager/menu-items/{id} - Get single menu item by ID
    @GetMapping("/manager/menu-items/{id}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(@PathVariable Long id) {
        return menuItemService.getMenuItemById(id)
                .map(menuItem -> ResponseEntity.ok(MenuItemResponse.fromEntity(menuItem)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Task 3.4: GET /api/manager/menu-items - Get all menu items (active and inactive)
    @GetMapping("/manager/menu-items")
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        List<MenuItemResponse> responses = menuItems.stream()
                .map(MenuItemResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Task 3.5: DELETE /api/manager/menu-items/{id} - Delete menu item
    @DeleteMapping("/manager/menu-items/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
