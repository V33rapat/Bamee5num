package com.restaurant.demo.service;

import com.restaurant.demo.model.CartItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final List<CartItem> cartItems = new ArrayList<>();

    public CartService() {
        // ตัวอย่างข้อมูล
        cartItems.add(new CartItem(1, "บะหมี่หมูแดง", 50, 2));
        cartItems.add(new CartItem(1, "น้ำอัดลม", 20, 1));
        cartItems.add(new CartItem(2, "ขนมหวาน", 30, 3));
    }

    public List<CartItem> getCartByCustomerId(int customerId) {
        List<CartItem> result = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.getCustomerId() == customerId) {
                result.add(item);
            }
        }
        return result;
    }

    public CartItem addToCart(int customerId, String name, int price, int quantity) {
        CartItem newItem = new CartItem(customerId, name, price, quantity);
        cartItems.add(newItem);
        return newItem;
    }
}
