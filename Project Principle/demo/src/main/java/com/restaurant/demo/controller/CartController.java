package com.restaurant.demo.controller;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.service.CartService;
import com.restaurant.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;

    // GET: Get cart for authenticated customer
    @GetMapping("/{customerId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Long customerId) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<CartItem> cartItems = cartService.getCartByCustomer(customerOpt.get());
            return ResponseEntity.ok(cartItems);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // POST: Add item to cart for authenticated customer
    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestBody AddToCartRequest request) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(request.getCustomerId());
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            CartItem cartItem = cartService.addToCart(
                customerOpt.get(),
                request.getName(),
                request.getPrice(),
                request.getQuantity()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT: Increment quantity of cart item
    @PutMapping("/increment/{itemId}")
    public ResponseEntity<CartItem> incrementQuantity(@PathVariable Long itemId, @RequestParam Long customerId) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            CartItem updatedItem = cartService.incrementQuantity(itemId, customerOpt.get());
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT: Decrement quantity of cart item
    @PutMapping("/decrement/{itemId}")
    public ResponseEntity<CartItem> decrementQuantity(@PathVariable Long itemId, @RequestParam Long customerId) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            CartItem updatedItem = cartService.decrementQuantity(itemId, customerOpt.get());
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT: Update quantity of cart item directly
    @PutMapping("/update-quantity/{itemId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable Long itemId, @RequestBody UpdateQuantityRequest request) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(request.getCustomerId());
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            CartItem updatedItem = cartService.updateQuantity(itemId, request.getQuantity(), customerOpt.get());
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE: Remove item from cart
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId, @RequestParam Long customerId) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            cartService.removeFromCart(itemId, customerOpt.get());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: Clear entire cart for customer
    @DeleteMapping("/clear/{customerId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        try {
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            cartService.clearCart(customerOpt.get());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Inner classes for request DTOs
    public static class AddToCartRequest {
        private Long customerId;
        private String name;
        private int price;
        private int quantity;

        // Constructors
        public AddToCartRequest() {}

        public AddToCartRequest(Long customerId, String name, int price, int quantity) {
            this.customerId = customerId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        // Getters and Setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class UpdateQuantityRequest {
        private Long customerId;
        private int quantity;

        // Constructors
        public UpdateQuantityRequest() {}

        public UpdateQuantityRequest(Long customerId, int quantity) {
            this.customerId = customerId;
            this.quantity = quantity;
        }

        // Getters and Setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
