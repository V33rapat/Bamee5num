package com.restaurant.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int customerId;
    private String name;
    private int price;
    private int quantity;

    public CartItem() {}

    public CartItem(int id, int customerId, String name, int price, int quantity) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public CartItem(int customerId, String name, int price, int quantity) {
    this.customerId = customerId;
    this.name = name;
    this.price = price;
    this.quantity = quantity;
}

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
