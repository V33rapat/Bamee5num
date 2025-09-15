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
        CartItem newItem = new CartItem(customerId, name, price, quantity);
        return cartItemRepository.save(newItem);
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
