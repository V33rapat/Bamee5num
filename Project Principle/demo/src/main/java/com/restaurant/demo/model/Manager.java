package com.restaurant.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "managers")
@PrimaryKeyJoinColumn(name = "id")
public class Manager extends Employee {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Manager() {
        super();
    }

    public Manager(Long id, String name) {
        super(id, name, "Manager");
    }

    public void addItem(MenuItem item, List<MenuItem> menu) {
        menu.add(item);
    }

    public void removeItem(MenuItem item, List<MenuItem> menu) {
        menu.remove(item);
    }

    public List<User> manageEmployees(List<User> users) {
        List<User> result = new java.util.ArrayList<>();
        for (User user : users) {
            if ("employee".equalsIgnoreCase(user.getRole()) || "manager".equalsIgnoreCase(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }
    
    public SalesReport viewSalesReport(List<CartItem> cartItems, List<User> users) {
        LocalDate today = LocalDate.now();

        Map<Long, List<CartItem>> todayCartByCustomer = cartItems.stream()
                .filter(item -> item.getAddedAt() != null && today.equals(item.getAddedAt().toLocalDate()))
                .collect(Collectors.groupingBy(CartItem::getCustomerId));

        int orderCount = todayCartByCustomer.size();
        double revenue = todayCartByCustomer.values().stream()
                .flatMap(List::stream)
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        int newCustomers = 0;
        for (User user : users) {
            if ("customer".equalsIgnoreCase(user.getRole()) && user.getCreatedAt() != null) {
                if (user.getCreatedAt().startsWith(today.toString())) {
                    newCustomers++;
                }
            }
        }

        double avgRating = 0;
        return new SalesReport(orderCount, revenue, newCustomers, avgRating);
    }
    
    // helper class -> sales report data
    public static class SalesReport {
        public final int orderCount;
        public final double revenue;
        public final int newCustomers;
        public final double avgRating;

        public SalesReport(int orderCount, double revenue, int newCustomers, double avgRating) {
            this.orderCount = orderCount;
            this.revenue = revenue;
            this.newCustomers = newCustomers;
            this.avgRating = avgRating;
        }
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters for Manager-specific fields
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}