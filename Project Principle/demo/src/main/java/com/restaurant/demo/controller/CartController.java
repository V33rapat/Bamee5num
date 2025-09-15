package com.restaurant.demo.controller;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    // GET: ดึง cart ของ user
    @GetMapping("/{userId}")
    public List<CartItem> getCart(@PathVariable int userId) {
        return cartService.getCartByCustomerId(userId);
    }

    // POST: เพิ่มสินค้าใน cart
    @PostMapping("/add")
    public CartItem addToCart(@RequestBody CartItem cartItem) {
        return cartService.addToCart(cartItem.getCustomerId(), cartItem.getName(),
                cartItem.getPrice(), cartItem.getQuantity());
    }

    // DELETE: ลบสินค้าออกจาก cart
    @DeleteMapping("/remove/{id}")
    public void removeFromCart(@PathVariable Integer id) {
        cartService.removeFromCart(id);
    }

    // DELETE: ล้าง cart ทั้งหมดของลูกค้า
    @DeleteMapping("/clear/{customerId}")
    public void clearCart(@PathVariable int customerId) {
        cartService.clearCart(customerId);
    }
}
