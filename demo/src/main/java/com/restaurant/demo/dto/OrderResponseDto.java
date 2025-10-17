package com.restaurant.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {

    private Long customerId;
    private String customerName;
    private List<OrderItemDto> items;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public OrderResponseDto() {}

    // Constructor with all fields (backward compatible - orderId optional)
    public OrderResponseDto(Long customerId, String customerName, List<OrderItemDto> items,
                            BigDecimal totalPrice, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor with orderId for proper order tracking
    public OrderResponseDto(Long orderId, Long customerId, String customerName, List<OrderItemDto> items,
                            BigDecimal totalPrice, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Alias for frontend compatibility (employee-orders.js expects orderDate)
    public LocalDateTime getOrderDate() { 
        return this.createdAt; 
    }
    public void setOrderDate(LocalDateTime orderDate) { 
        this.createdAt = orderDate; 
    }

    // Added orderId field for proper order tracking
    private Long orderId;
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    // Inner DTO class for order items
    public static class OrderItemDto {
        private Long id;
        private String itemName;
        private BigDecimal itemPrice;  // ใช้ BigDecimal แทน double
        private Integer quantity;
        private BigDecimal subtotal;

        // Default constructor
        public OrderItemDto() {}

        // Constructor with all fields
        public OrderItemDto(Long id, String itemName, BigDecimal itemPrice, Integer quantity, BigDecimal subtotal) {
            this.id = id;
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.quantity = quantity;
            this.subtotal = subtotal;
        }

        // Getters & Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public BigDecimal getItemPrice() { return itemPrice; }
        public void setItemPrice(BigDecimal itemPrice) { this.itemPrice = itemPrice; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
}
