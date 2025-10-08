package com.restaurant.demo.controller;

import com.restaurant.demo.dto.EmployeeRegistrationDto;
import com.restaurant.demo.dto.MenuItemRequest;
import com.restaurant.demo.dto.MenuItemResponse;
import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.model.Manager;
import com.restaurant.demo.model.MenuItem;
import com.restaurant.demo.model.User;
import com.restaurant.demo.service.CartService;
import com.restaurant.demo.service.ManagerService;
import com.restaurant.demo.service.MenuItemService;
import com.restaurant.demo.service.OrderService;
import com.restaurant.demo.service.ReportService;
import com.restaurant.demo.service.employee.EmployeeService;
import com.restaurant.demo.service.employee.dto.EmployeeCredentials;
import com.restaurant.demo.service.employee.dto.EmployeeRegistrationRequest;
import com.restaurant.demo.service.employee.dto.EmployeeRegistrationResult;
import com.restaurant.demo.service.employee.dto.EmployeeUpdateRequest;
import com.restaurant.demo.service.manager.ManagerContext;
import com.restaurant.demo.service.manager.SalesReportService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Add the import for ReportSummary
import com.restaurant.demo.dto.ReportSummary;

@RestController
// URL จะอยู่ภายใต้ /api/...
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true", maxAge = 3600)
public class ManagerApiController {

    private final ManagerContext managerContext;
    private final EmployeeService employeeService;
    private final CartService cartService;
    private final SalesReportService salesReportService;
    private final MenuItemService menuItemService;
    private final ManagerService managerService;
    private final OrderService orderService;
    private final ReportService reportService;

    // Constructor-based dependency injection
    // (Spring จะสร้าง instance ของคลาสนี้และฉีด service ที่ต้องการ
    public ManagerApiController(ManagerContext managerContext,
                                EmployeeService employeeService,
                                CartService cartService,
                                SalesReportService salesReportService,
                                MenuItemService menuItemService,
                                ManagerService managerService,
                                OrderService orderService,
                                ReportService reportService) {
        this.managerContext = managerContext;
        this.employeeService = employeeService;
        this.cartService = cartService;
        this.salesReportService = salesReportService;
        this.menuItemService = menuItemService;
        this.managerService = managerService;
        this.orderService = orderService;
        this.reportService = reportService;
    }

    /**
     * Helper method to check if the current user is a manager (not an employee)
     * Prevents employees from accessing manager-only endpoints
     * 
     * @param session HTTP session
     * @return true if user is a manager, false otherwise
     */
    private boolean isManager(HttpSession session) {
        // Check if manager is authenticated
        Boolean managerAuthenticated = (Boolean) session.getAttribute("managerAuthenticated");
        
        // Check if employee is authenticated (employees should NOT access manager endpoints)
        Boolean employeeAuthenticated = (Boolean) session.getAttribute("employeeAuthenticated");
        
        // Return true only if manager is authenticated AND employee is NOT authenticated
        return (managerAuthenticated != null && managerAuthenticated) && 
               (employeeAuthenticated == null || !employeeAuthenticated);
    }

    @GetMapping("/currentUser")
    public User getCurrentUser() {
        return managerContext.getCurrentManager();
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        // Use database-backed method instead of in-memory storage
        return managerService.getAllEmployees();
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
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        // Use database-backed method instead of in-memory storage
        boolean removed = managerService.deleteEmployee(id);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ===== Employee Registration Endpoint (Task 6.10) =====
    
    /**
     * Register a new employee (Manager functionality)
     * Uses EmployeeRegistrationDto with validation
     * 
     * @param dto EmployeeRegistrationDto containing employee registration details
     * @param session HTTP session for role-based access control
     * @return ResponseEntity containing the registered employee details
     */
    @PostMapping("/managers/employees")
    public ResponseEntity<?> registerEmployee(@Valid @RequestBody EmployeeRegistrationDto dto, HttpSession session) {
        // Role-based access control: Only managers can register employees
        if (!isManager(session)) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Unauthorized. Only managers can register employees.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        
        try {
            Employee employee = managerService.registerEmployee(dto);
            
            // Prepare response with employee details (without password)
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("employeeId", employee.getId());
            response.put("name", employee.getName());
            response.put("position", employee.getPosition());
            response.put("username", employee.getUsername());
            response.put("message", "Employee registered successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (RuntimeException e) {
            // Handle username already exists or other errors
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/carts")
    public List<CartItem> getAllCarts() {
        return cartService.getAllCartItems();
    }

    @GetMapping("/carts/{userId}")
    public List<CartItem> getCartForUser(@PathVariable int userId) {
        return cartService.getCartItems(Long.valueOf(userId));
    }

    /**
     * GET /api/reports/sales - Get daily sales report
     * Returns revenue calculated from completed orders (status = "Finish")
     * This uses Order data, not CartItem data, to ensure accurate revenue reporting
     * even after cart items are deleted upon order placement.
     */
    @GetMapping("/reports/sales")
    public Manager.SalesReport getSalesReport() {
        return salesReportService.getDailySalesReport();
    }

    @GetMapping({"/monthly", "/reports/monthly"})
    public ResponseEntity<?> getMonthlyReport(
            @RequestParam(required = false) Integer month,
            @RequestParam Integer year) {

        ReportSummary summary = reportService.getMonthlyReport(month, year);
        return ResponseEntity.ok(summary);
    }
    // ===== Menu Management Endpoints =====

    // Task 3.1: POST /api/manager/menu-items - Create new menu item
    @PostMapping("/manager/menu-items")
    public ResponseEntity<?> createMenuItem(@Valid @RequestBody MenuItemRequest request, HttpSession session) {
        // Role-based access control: Only managers can create menu items
        if (!isManager(session)) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Unauthorized. Only managers can create menu items.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        
        MenuItemResponse response = menuItemService.createMenuItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Task 3.2: PUT /api/manager/menu-items/{id} - Update existing menu item
    @PutMapping("/manager/menu-items/{id}")
    public ResponseEntity<?> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request,
            HttpSession session) {
        // Role-based access control: Only managers can update menu items
        if (!isManager(session)) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Unauthorized. Only managers can update menu items.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        
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
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id, HttpSession session) {
        // Role-based access control: Only managers can delete menu items
        if (!isManager(session)) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Unauthorized. Only managers can delete menu items.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    // Task 8.9: GET /api/managers/order-stats - Get order statistics for manager dashboard
    @GetMapping("/managers/order-stats")
    public ResponseEntity<?> getOrderStats(HttpSession session) {
        // Role-based access control: Only managers can access order stats
        if (!isManager(session)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized. Only managers can access order statistics.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        try {
            Long pendingCount = orderService.getOrderCountByStatus("Pending");
            Long inProgressCount = orderService.getOrderCountByStatus("In Progress");
            Long finishCount = orderService.getOrderCountByStatus("Finish");
            Long cancelledCount = orderService.getOrderCountByStatus("Cancelled");

            Map<String, Object> stats = new HashMap<>();
            stats.put("pendingOrders", pendingCount != null ? pendingCount : 0);
            stats.put("inProgressOrders", inProgressCount != null ? inProgressCount : 0);
            stats.put("completedOrders", finishCount != null ? finishCount : 0);
            stats.put("cancelledOrders", cancelledCount != null ? cancelledCount : 0);
            stats.put("totalOrders", 
                (pendingCount != null ? pendingCount : 0) + 
                (inProgressCount != null ? inProgressCount : 0) + 
                (finishCount != null ? finishCount : 0) + 
                (cancelledCount != null ? cancelledCount : 0));

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch order statistics");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
