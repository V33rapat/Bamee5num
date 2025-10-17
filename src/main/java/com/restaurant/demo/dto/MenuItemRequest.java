package com.restaurant.demo.dto;

import jakarta.validation.constraints.*;

public class MenuItemRequest {

    @NotBlank(message = "Menu name is required")
    @Size(max = 100, message = "Menu name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    @DecimalMax(value = "9999.99", message = "Price must not exceed 9999.99")
    private Double price;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "^(Noodles|Beverages|Desserts)$", message = "Category must be one of: Noodles, Beverages, Desserts")
    private String category;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Active status is required")
    private Boolean active;

    // Default constructor
    public MenuItemRequest() {}

    // Constructor with all fields
    public MenuItemRequest(String name, Double price, String category, String description, Boolean active) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.active = active;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
