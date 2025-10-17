package com.restaurant.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderPlacementDto {

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be a positive number")
    private Long customerId;

    // Default constructor
    public OrderPlacementDto() {}

    // Constructor with all fields
    public OrderPlacementDto(Long customerId) {
        this.customerId = customerId;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
