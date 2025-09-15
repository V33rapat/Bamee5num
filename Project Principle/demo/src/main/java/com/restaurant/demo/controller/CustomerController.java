package com.restaurant.demo.controller;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CartService cartService;

    public CustomerController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("")
    public List<CartItem> getCustomerCart(@RequestParam int id) {
        return cartService.getCartByCustomerId(id);
    }

    @PostMapping("/add")
    public CartItem addToCart(@RequestParam int customerId,
                              @RequestParam String name,
                              @RequestParam int price,
                              @RequestParam int quantity) {
        return cartService.addToCart(customerId, name, price, quantity);
    }
}
