package com.restaurant.demo.controller;

import com.restaurant.demo.dto.AuthResponseDto;
import com.restaurant.demo.dto.CustomerLoginDto;
import com.restaurant.demo.dto.CustomerRegistrationDto;
import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.service.CartService;
import com.restaurant.demo.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CartService cartService;
    private final CustomerService customerService;

    public CustomerController(CartService cartService, CustomerService customerService) {
        this.cartService = cartService;
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerCustomer(@Valid @RequestBody CustomerRegistrationDto registrationDto) {
        AuthResponseDto response = customerService.registerCustomer(registrationDto);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginCustomer(@Valid @RequestBody CustomerLoginDto loginDto) {
        AuthResponseDto response = customerService.loginCustomer(loginDto);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/profile/{customerId}")
    public ResponseEntity<Customer> getCustomerProfile(@PathVariable Long customerId) {
        Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            // Remove password from response for security
            customer.setPassword(null);
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Legacy cart endpoint - deprecated, use CartController instead
    @GetMapping("")
    public List<CartItem> getCustomerCart(@RequestParam int id) {
        return cartService.getCartByCustomerId(id);
    }

    // Legacy cart endpoint - deprecated, use CartController instead
    @PostMapping("/add")
    public CartItem addToCart(@RequestParam int customerId,
                              @RequestParam String name,
                              @RequestParam int price,
                              @RequestParam int quantity) {
        return cartService.addToCart(customerId, name, price, quantity);
    }
}
