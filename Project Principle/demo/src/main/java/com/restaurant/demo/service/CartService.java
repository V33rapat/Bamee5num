package com.restaurant.demo.service;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.repository.CartItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;

    public CartService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public List<CartItem> getCartByCustomerId(int customerId) {
        return cartItemRepository.findByCustomerId(customerId);
    }

    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    public CartItem addToCart(int customerId, String name, int price, int quantity) {
        CartItem newItem = new CartItem(customerId, name, price, quantity);
        return cartItemRepository.save(newItem);
    }

    public CartItem addToCart(CartItem cartItem) {
        if (cartItem.getAddedAt() == null) {
            cartItem.setAddedAt(java.time.LocalDateTime.now());
        }
        return cartItemRepository.save(cartItem);
    }

    public void removeFromCart(int id) {
        cartItemRepository.deleteById(id);
    }
}