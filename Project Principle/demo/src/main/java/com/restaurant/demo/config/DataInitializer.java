package com.restaurant.demo.config;

import com.restaurant.demo.service.CartService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CartService cartService;

    public DataInitializer(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void run(String... args) {
        if (!cartService.getAllCartItems().isEmpty()) {
            return;
        }

        cartService.addToCart(3, "BBQ Pork Noodles", 50, 2);
        cartService.addToCart(3, "Iced Tea", 20, 1);
        cartService.addToCart(2, "Fried Dumplings", 45, 3);
    }
}