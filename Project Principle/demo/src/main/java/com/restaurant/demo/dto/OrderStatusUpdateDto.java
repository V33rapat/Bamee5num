package com.restaurant.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class OrderStatusUpdateDto {

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be a positive number")
    private Long customerId;

    @NotBlank(message = "New status is required")
    @Pattern(regexp = "^(Pending|In Progress|Cancelled|Finish)$", 
             message = "Status must be one of: Pending, In Progress, Cancelled, Finish")
    private String newStatus;

    // Default constructor
    public OrderStatusUpdateDto() {}

    // Constructor with all fields
    public OrderStatusUpdateDto(Long customerId, String newStatus) {
        this.customerId = customerId;
        this.newStatus = newStatus;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
