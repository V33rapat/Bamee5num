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
            @PathVariable @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId) {
        
        logger.info("Placing order for customer ID: {}", customerId);
        
        OrderResponseDto orderResponse = orderService.placeOrder(customerId);
        
        logger.info("Order placed successfully for customer ID: {}, Total: {}", customerId, orderResponse.getTotalPrice());
        
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    /**
     * Get pending orders for a customer
     * 
     * @param customerId The ID of the customer
     * @return ResponseEntity containing pending order details
     */
    @GetMapping("/customers/{customerId}/pending-orders")
    public ResponseEntity<OrderResponseDto> getPendingOrders(
            @PathVariable @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId) {
        
        logger.info("Fetching pending orders for customer ID: {}", customerId);
        
        OrderResponseDto pendingOrders = orderService.getPendingOrdersByCustomerId(customerId);
        
        logger.info("Fetched pending orders for customer ID: {}", customerId);
        
        return new ResponseEntity<>(pendingOrders, HttpStatus.OK);
    }
}
