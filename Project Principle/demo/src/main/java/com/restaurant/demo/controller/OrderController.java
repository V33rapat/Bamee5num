package com.restaurant.demo.controller;

import com.restaurant.demo.dto.OrderResponseDto;
import com.restaurant.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * REST Controller for Order Management Operations
 * Handles customer order placement and retrieval
 */
@RestController
@RequestMapping("/api/orders")
@Validated
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true", maxAge = 3600)
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * Place an order for a customer
     * Converts cart items to pending orders
     * 
     * @param customerId The ID of the customer placing the order
     * @return ResponseEntity containing OrderResponseDto with order details
     */
    @PostMapping("/customers/{customerId}/place-order")
    public ResponseEntity<OrderResponseDto> placeOrder(
            @PathVariable 
            @NotNull(message = "Customer ID is required") 
            @Positive(message = "Customer ID must be positive") Long customerId,
            @RequestParam(required = false) Long employeeId) { // employeeId ส่งมาหรือไม่ก็ได้

        logger.info("Placing order for customer ID: {}, employee ID: {}", customerId, employeeId);

        // เรียก service แบบสองพารามิเตอร์
        OrderResponseDto orderResponse = orderService.placeOrder(customerId, employeeId);

        logger.info("Order placed successfully for customer ID: {}, Total: {}", customerId, orderResponse.getTotalPrice());

        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
}


    /**
     * Get pending orders for a customer
     * 
     * @param customerId The ID of the customer
     * @return ResponseEntity containing list of pending orders
     */
    @GetMapping("/customers/{customerId}/pending-orders")
    public ResponseEntity<List<OrderResponseDto>> getPendingOrders(
            @PathVariable @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId) {
        
        logger.info("Fetching pending orders for customer ID: {}", customerId);
        
        List<OrderResponseDto> pendingOrders = orderService.getPendingOrdersByCustomerId(customerId);
        
        logger.info("Fetched {} pending orders for customer ID: {}", pendingOrders.size(), customerId);
        
        return new ResponseEntity<>(pendingOrders, HttpStatus.OK);
    }

    /**
     * Get all orders for a customer (all statuses)
     * 
     * @param customerId The ID of the customer
     * @return ResponseEntity containing list of all customer orders
     */
    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(
            @PathVariable @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId) {
        
        logger.info("Fetching all orders for customer ID: {}", customerId);
        
        List<OrderResponseDto> orders = orderService.getOrdersByCustomerId(customerId);
        
        logger.info("Fetched {} orders for customer ID: {}", orders.size(), customerId);
        
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
