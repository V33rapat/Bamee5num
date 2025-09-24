package com.restaurant.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private Customer customer;

    @NotBlank(message = "Item name is required")
    @Column(name = "item_name", nullable = false)
    private String name;

    @Min(value = 0, message = "Price must be non-negative")
    @Column(name = "item_price", nullable = false)
    private int price;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int quantity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public CartItem() {}

    public CartItem(Customer customer, String name, int price, int quantity) {
        this.customer = customer;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Legacy constructor for backward compatibility (deprecated)
    @Deprecated
    public CartItem(int customerId, String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        // Note: customerId is ignored, use setCustomer() method instead
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Backward compatibility method (deprecated)
    @Deprecated
    public int getCustomerId() {
        return customer != null ? customer.getId().intValue() : 0;
    }

    @Deprecated
    public void setCustomerId(int customerId) {
        // This method is deprecated, use setCustomer() instead
        // Implementation left empty to avoid breaking existing code
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public int getTotalPrice() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", customer=" + (customer != null ? customer.getUsername() : "null") +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
