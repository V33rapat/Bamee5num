package com.restaurant.demo.controller;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    // GET: ดึง cart ของ user
    @GetMapping("/{userId}")
    public List<CartItem> getCart(@PathVariable int userId) {
        return cartItemRepository.findByCustomerId(userId);
    }

    // POST: เพิ่มสินค้าใน cart
    @PostMapping("/add")
    public CartItem addToCart(@RequestBody CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    // DELETE: ลบสินค้าออกจาก cart
    @DeleteMapping("/remove/{id}")
    public void removeFromCart(@PathVariable Integer id) {
        cartItemRepository.deleteById(id); // แก้จาก deleteAllById(Long) เป็น deleteById(Long)
    }
}
