package com.restaurant.demo.dto;

import com.restaurant.demo.model.MenuItem;

public class MenuItemResponse {

    private Long id;
    private String name;
    private Double price;
    private String category;
    private String description;
    private Boolean active;

    // Default constructor
    public MenuItemResponse() {}

    // Constructor with all fields
    public MenuItemResponse(Long id, String name, Double price, String category, String description, Boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.active = active;
    }

    // Static factory method to convert from MenuItem entity
    public static MenuItemResponse fromEntity(MenuItem menuItem) {
        if (menuItem == null) {
            return null;
        }
        return new MenuItemResponse(
            menuItem.getId(),
            menuItem.getName(),
            menuItem.getPrice(),
            menuItem.getCategory(),
            menuItem.getDescription(),
            menuItem.isActive()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
