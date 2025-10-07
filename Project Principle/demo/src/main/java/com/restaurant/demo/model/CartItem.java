package com.restaurant.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {

    // --- เพิ่ม constant สำหรับสถานะ ---
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_CANCELLED = "Cancelled";
    public static final String STATUS_FINISH = "Finish";
    public static final String STATUS_ORDERED = "Ordered";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank(message = "Item name is required")
    @Size(min = 1, max = 100, message = "Item name must be between 1 and 100 characters")
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @NotNull(message = "Item price is required")
    @DecimalMin(value = "0.01", message = "Item price must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Item price must not exceed 9999.99")
    @Digits(integer = 4, fraction = 2, message = "Item price must have at most 4 integer digits and 2 decimal places")
    @Column(name = "item_price", nullable = false, precision = 6, scale = 2)
    private BigDecimal itemPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity must not exceed 100")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Pending|In Progress|Cancelled|Finish|Ordered", message = "Status must be one of: Pending, In Progress, Cancelled, Finish, Ordered")
    @Column(name = "status", nullable = false, length = 20)
    private String status = STATUS_PENDING; // ใช้ constant แทน string ตรงนี้

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // Default constructor
    public CartItem() {
    }

    // Constructor with required fields (backward compatible)
    public CartItem(Customer customer, String itemName, BigDecimal itemPrice, Integer quantity) {
        this.customer = customer;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.status = STATUS_PENDING; // ใช้ constant
    }

    // Constructor with status parameter
    public CartItem(Customer customer, String itemName, BigDecimal itemPrice, Integer quantity, String status) {
        this.customer = customer;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.status = status != null ? status : STATUS_PENDING; // ใช้ constant
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null || status.isEmpty()) {
            status = STATUS_PENDING; // ใช้ constant
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Calculated field for total price
    public BigDecimal getTotalPrice() {
        if (itemPrice != null && quantity != null) {
            return itemPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    // Compatibility methods for Manager features
    public LocalDateTime getAddedAt() {
        return this.createdAt;
    }

    public Long getCustomerId() {
        return this.customer != null ? this.customer.getId() : null;
    }

    public double getPrice() {
        return this.itemPrice != null ? this.itemPrice.doubleValue() : 0.0;
    }
}
