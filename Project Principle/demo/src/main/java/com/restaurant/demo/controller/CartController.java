package com.restaurant.demo.controller;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Validated
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true", maxAge = 3600)
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Add item to cart
     */
    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(
            @RequestParam @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId,

            // ***************************************************************
            // แก้ไข: เปลี่ยนจาก itemName/itemPrice เป็น menuItemId
            @RequestParam @NotNull(message = "Menu Item ID is required") // <-- ใช้ @NotNull แทน @NotBlank
            @Positive(message = "Menu Item ID must be positive") Long menuItemId, // <-- ใช้ Long แทน String/BigDecimal
            // ***************************************************************

            @RequestParam @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") @Max(value = 100, message = "Quantity must not exceed 100") Integer quantity) {

        // 2. เรียกใช้ Service โดยส่ง Customer ID และ Menu Item ID
        // ***************************************************************
        CartItem cartItem = cartService.addToCart(customerId, menuItemId, quantity); // <-- แก้ Signature ที่เรียกใช้
        // ***************************************************************

        return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
    }

    /**
     * Update cart item quantity
     */
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(
            @PathVariable @NotNull(message = "Cart item ID is required") @Positive(message = "Cart item ID must be positive") Long cartItemId,
            @RequestParam @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId,
            @RequestParam @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") @Max(value = 100, message = "Quantity must not exceed 100") Integer quantity) {

        CartItem cartItem = cartService.updateCartItemQuantity(cartItemId, customerId, quantity);
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable @NotNull(message = "Cart item ID is required") @Positive(message = "Cart item ID must be positive") Long cartItemId,
            @RequestParam @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId) {

        cartService.removeFromCart(cartItemId, customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get all cart items for a customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CartItem>> getCartItems(
            @PathVariable @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId) {

        List<CartItem> cartItems = cartService.getCartItems(customerId);
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    /**
     * Get cart item by ID
     */
    @GetMapping("/{cartItemId}")
    public ResponseEntity<CartItem> getCartItem(
            @PathVariable @NotNull(message = "Cart item ID is required") @Positive(message = "Cart item ID must be positive") Long cartItemId,
            @RequestParam @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId) {

        CartItem cartItem = cartService.getCartItem(cartItemId, customerId);
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    /**
     * Clear all cart items for a customer
     */
    @DeleteMapping("/clear/{customerId}")
    public ResponseEntity<Void> clearCart(
            @PathVariable @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId) {

        cartService.clearCart(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get cart total for a customer
     */
    @GetMapping("/total/{customerId}")
    public ResponseEntity<BigDecimal> getCartTotal(
            @PathVariable @NotNull(message = "Customer ID is required") @Positive(message = "Customer ID must be positive") Long customerId) {

        BigDecimal total = cartService.calculateCartTotal(customerId);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }
}