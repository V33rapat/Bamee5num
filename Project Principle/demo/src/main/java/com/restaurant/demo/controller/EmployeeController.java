package com.restaurant.demo.controller;

import com.restaurant.demo.dto.EmployeeLoginDto;
import com.restaurant.demo.dto.OrderResponseDto;
import com.restaurant.demo.dto.OrderStatusUpdateDto;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.service.EmployeeAuthService;
import com.restaurant.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Employee Operations
 * Handles employee authentication and order management
 */
@RestController
@RequestMapping("/api/employees")
@Validated
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true", maxAge = 3600)
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeAuthService employeeAuthService;

    @Autowired
    private OrderService orderService;

    /**
     * Authenticate employee login
     * Stores employee ID and username in HTTP session
     * 
     * @param loginDto EmployeeLoginDto containing username and password
     * @param session HTTP session for storing employee authentication
     * @return ResponseEntity containing employee details
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginEmployee(
            @Valid @RequestBody EmployeeLoginDto loginDto, 
            HttpSession session) {
        
        logger.info("Employee login attempt for username: {}, sessionId: {}", 
                loginDto.getUsername(), session.getId());
        
        try {
            // Authenticate employee
            Employee employee = employeeAuthService.authenticateEmployee(loginDto)
                    .orElseThrow(() -> new RuntimeException("Invalid username or password"));
            
            // Store employee information in session
            session.setAttribute("employeeId", employee.getId());
            session.setAttribute("employeeUsername", employee.getUsername());
            session.setAttribute("employeeName", employee.getName());
            session.setAttribute("employeePosition", employee.getPosition());
            session.setAttribute("employeeAuthenticated", true);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("employeeId", employee.getId());
            response.put("username", employee.getUsername());
            response.put("name", employee.getName());
            response.put("position", employee.getPosition());
            response.put("message", "Login successful");
            
            logger.info("Employee login successful - employeeId: {}, username: {}, sessionId: {}", 
                    employee.getId(), employee.getUsername(), session.getId());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            logger.warn("Employee login failed for username: {} - {}", 
                    loginDto.getUsername(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid username or password");
            
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Get all orders with optional status filter
     * 
     * @param status Optional status filter (Pending, In Progress, Finish, Cancelled)
     * @param session HTTP session for authentication check
     * @return ResponseEntity containing list of orders
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) String status,
            HttpSession session) {
        
        // Check if employee is authenticated
        Boolean isAuthenticated = (Boolean) session.getAttribute("employeeAuthenticated");
        if (isAuthenticated == null || !isAuthenticated) {
            logger.warn("Unauthorized access attempt to /api/employees/orders");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized. Please login first.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        
        logger.info("Fetching orders - status filter: {}, employeeId: {}", 
                status, session.getAttribute("employeeId"));
        
        try {
            List<OrderResponseDto> orders;
            
            if (status != null && !status.isEmpty()) {
                // Get orders filtered by status
                orders = orderService.getAllOrdersByStatus(status);
                logger.info("Found {} orders with status: {}", orders.size(), status);
            } else {
                // Get all orders (you may need to implement this or get all statuses)
                // For now, get all pending orders by default
                orders = orderService.getAllOrdersByStatus("Pending");
                logger.info("Found {} pending orders", orders.size());
            }
            
            return new ResponseEntity<>(orders, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid status parameter: {}", status);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get specific order details by customer ID (order ID)
     * 
     * @param orderId The customer ID (used as order identifier)
     * @param session HTTP session for authentication check
     * @return ResponseEntity containing order details
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(
            @PathVariable @NotNull(message = "Order ID is required") @Positive(message = "Order ID must be positive") Long orderId,
            HttpSession session) {
        
        // Check if employee is authenticated
        Boolean isAuthenticated = (Boolean) session.getAttribute("employeeAuthenticated");
        if (isAuthenticated == null || !isAuthenticated) {
            logger.warn("Unauthorized access attempt to /api/employees/orders/{}", orderId);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized. Please login first.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        
        logger.info("Fetching order details for orderId (customerId): {}, employeeId: {}", 
                orderId, session.getAttribute("employeeId"));
        
        try {
            // Get order by customer ID (pending orders)
            OrderResponseDto order = orderService.getPendingOrdersByCustomerId(orderId);
            
            logger.info("Order details fetched for orderId: {}", orderId);
            
            return new ResponseEntity<>(order, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            logger.warn("Order not found for orderId: {} - {}", orderId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update order status
     * 
     * @param orderId The customer ID (used as order identifier)
     * @param updateDto OrderStatusUpdateDto containing new status
     * @param session HTTP session for authentication check
     * @return ResponseEntity containing updated order details
     */
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable @NotNull(message = "Order ID is required") @Positive(message = "Order ID must be positive") Long orderId,
            @Valid @RequestBody OrderStatusUpdateDto updateDto,
            HttpSession session) {
        
        // Check if employee is authenticated
        Boolean isAuthenticated = (Boolean) session.getAttribute("employeeAuthenticated");
        if (isAuthenticated == null || !isAuthenticated) {
            logger.warn("Unauthorized access attempt to update order status for orderId: {}", orderId);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized. Please login first.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        
        logger.info("Updating order status for orderId: {}, new status: {}, employeeId: {}", 
                orderId, updateDto.getNewStatus(), session.getAttribute("employeeId"));
        
        try {
            // Set the customer ID from path variable if not already set in DTO
            if (updateDto.getCustomerId() == null) {
                updateDto.setCustomerId(orderId);
            }
            
            // Verify that the orderId matches the customerId in the DTO
            if (!orderId.equals(updateDto.getCustomerId())) {
                throw new IllegalArgumentException("Order ID in path does not match customer ID in request body");
            }
            
            // Update order status
            OrderResponseDto updatedOrder = orderService.updateOrderStatus(updateDto);
            
            logger.info("Order status updated successfully for orderId: {}, new status: {}", 
                    orderId, updateDto.getNewStatus());
            
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            logger.warn("Failed to update order status for orderId: {} - {}", orderId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get count of pending orders for notification polling
     * 
     * @param session HTTP session for authentication check
     * @return ResponseEntity containing count of pending orders
     */
    @GetMapping("/orders/pending/count")
    public ResponseEntity<?> getPendingOrderCount(HttpSession session) {
        
        // Check if employee is authenticated
        Boolean isAuthenticated = (Boolean) session.getAttribute("employeeAuthenticated");
        if (isAuthenticated == null || !isAuthenticated) {
            logger.warn("Unauthorized access attempt to /api/employees/orders/pending/count");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized. Please login first.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        
        logger.info("Fetching pending order count, employeeId: {}", session.getAttribute("employeeId"));
        
        try {
            Long pendingCount = orderService.getOrderCountByStatus("Pending");
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", pendingCount);
            response.put("status", "Pending");
            
            logger.info("Pending order count: {}", pendingCount);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error fetching pending order count: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch pending order count");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logout employee and invalidate session
     * 
     * @param session HTTP session to invalidate
     * @return ResponseEntity with logout confirmation
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        
        logger.info("Employee logout - employeeId: {}, sessionId: {}", 
                session.getAttribute("employeeId"), session.getId());
        
        // Invalidate session
        session.invalidate();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
