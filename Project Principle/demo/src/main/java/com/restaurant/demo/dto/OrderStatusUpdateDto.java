package com.restaurant.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderStatusUpdateDto {

    @NotBlank(message = "New status is required")
    @Pattern(regexp = "^(Pending|In Progress|Cancelled|Finish)$", 
             message = "Status must be one of: Pending, In Progress, Cancelled, Finish")
    private String newStatus;

    public OrderStatusUpdateDto() {}

    public OrderStatusUpdateDto(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
