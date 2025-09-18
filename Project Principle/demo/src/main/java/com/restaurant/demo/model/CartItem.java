package com.restaurant.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int customerId;
    private String name;
    private int price;
    private int quantity;
    private LocalDateTime addedAt;

    public CartItem() {
    }

    public CartItem(int id, int customerId, String name, int price, int quantity) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.addedAt = LocalDateTime.now();
    }

    public CartItem(int customerId, String name, int price, int quantity) {
        this.customerId = customerId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.addedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}