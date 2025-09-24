package com.restaurant.demo.service;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    public List<CartItem> getCartByCustomerId(int customerId) {
        return cartItemRepository.findByCustomerId(customerId);
    }

    public CartItem addToCart(int customerId, String name, int price, int quantity) {
        // Validate initial quantity
        if (quantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }
        if (quantity > 99) {
            throw new RuntimeException("Quantity cannot exceed 99");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCustomerIdAndName(customerId, name);

        if (existingItem.isPresent()) {
            // Item exists, increase quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            // Apply quantity validation
            if (newQuantity > 99) {
                newQuantity = 99; // Cap at maximum
            }

            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            // Item doesn't exist, create new entry
            CartItem newItem = new CartItem(customerId, name, price, quantity);
            return cartItemRepository.save(newItem);
        }
    }

    public CartItem incrementQuantity(int itemId) {
        Optional<CartItem> optionalItem = cartItemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }

        CartItem item = optionalItem.get();
        int newQuantity = item.getQuantity() + 1;

        // Apply maximum quantity validation
        if (newQuantity > 99) {
            throw new RuntimeException("Quantity cannot exceed 99");
        }

        item.setQuantity(newQuantity);
        return cartItemRepository.save(item);
    }

    public CartItem decrementQuantity(int itemId) {
        Optional<CartItem> optionalItem = cartItemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }

        CartItem item = optionalItem.get();
        if (item.getQuantity() <= 1) {
            throw new RuntimeException("Quantity cannot be less than 1");
        }

        item.setQuantity(item.getQuantity() - 1);
        return cartItemRepository.save(item);
    }

    public CartItem updateQuantity(int itemId, int quantity) {
        if (quantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }
        if (quantity > 99) {
            throw new RuntimeException("Quantity cannot exceed 99");
        }

        Optional<CartItem> optionalItem = cartItemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }

        CartItem item = optionalItem.get();
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeFromCart(int itemId) {
        cartItemRepository.deleteById(itemId);
    }

    public void clearCart(int customerId) {
        List<CartItem> customerItems = cartItemRepository.findByCustomerId(customerId);
        cartItemRepository.deleteAll(customerItems);
    }

    public Optional<CartItem> getCartItem(int itemId) {
        return cartItemRepository.findById(itemId);
    }

    public CartItem updateCartItem(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }
}

