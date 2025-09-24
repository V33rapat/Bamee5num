package com.restaurant.demo.service;

import com.restaurant.demo.dto.AuthResponseDto;
import com.restaurant.demo.dto.CustomerLoginDto;
import com.restaurant.demo.dto.CustomerRegistrationDto;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new customer with validation
     */
    public AuthResponseDto registerCustomer(CustomerRegistrationDto registrationDto) {
        try {
            // Check if username already exists
            if (customerRepository.existsByUsername(registrationDto.getUsername())) {
                return AuthResponseDto.error("Username already exists");
            }

            // Check if email already exists
            if (customerRepository.existsByEmail(registrationDto.getEmail())) {
                return AuthResponseDto.error("Email already exists");
            }

            // Create new customer
            Customer customer = new Customer();
            customer.setUsername(registrationDto.getUsername());
            customer.setEmail(registrationDto.getEmail());
            customer.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            customer.setName(registrationDto.getName());
            customer.setPhone(registrationDto.getPhone());

            // Save customer
            Customer savedCustomer = customerRepository.save(customer);

            return AuthResponseDto.success(
                "Customer registered successfully",
                savedCustomer.getId(),
                savedCustomer.getUsername(),
                savedCustomer.getEmail(),
                savedCustomer.getName(),
                savedCustomer.getPhone()
            );

        } catch (Exception e) {
            return AuthResponseDto.error("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Authenticate customer login
     */
    public AuthResponseDto loginCustomer(CustomerLoginDto loginDto) {
        try {
            // Find customer by username or email
            Optional<Customer> customerOpt = findCustomerByUsernameOrEmail(loginDto.getUsernameOrEmail());

            if (customerOpt.isEmpty()) {
                return AuthResponseDto.error("Invalid username/email or password");
            }

            Customer customer = customerOpt.get();

            // Check password
            if (!passwordEncoder.matches(loginDto.getPassword(), customer.getPassword())) {
                return AuthResponseDto.error("Invalid username/email or password");
            }

            return AuthResponseDto.success(
                "Login successful",
                customer.getId(),
                customer.getUsername(),
                customer.getEmail(),
                customer.getName(),
                customer.getPhone()
            );

        } catch (Exception e) {
            return AuthResponseDto.error("Login failed: " + e.getMessage());
        }
    }

    /**
     * Find customer by ID
     */
    public Optional<Customer> findCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    /**
     * Find customer by username or email
     */
    public Optional<Customer> findCustomerByUsernameOrEmail(String usernameOrEmail) {
        // Try to find by username first
        Optional<Customer> customer = customerRepository.findByUsername(usernameOrEmail);

        // If not found by username, try by email
        if (customer.isEmpty()) {
            customer = customerRepository.findByEmail(usernameOrEmail);
        }

        return customer;
    }

    /**
     * Validate password strength
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // Add more password validation rules as needed
        return true;
    }

    /**
     * Check if username is available
     */
    public boolean isUsernameAvailable(String username) {
        return !customerRepository.existsByUsername(username);
    }

    /**
     * Check if email is available
     */
    public boolean isEmailAvailable(String email) {
        return !customerRepository.existsByEmail(email);
    }

    /**
     * Update customer profile
     */
    public AuthResponseDto updateCustomer(Long customerId, CustomerRegistrationDto updateDto) {
        try {
            Optional<Customer> customerOpt = customerRepository.findById(customerId);

            if (customerOpt.isEmpty()) {
                return AuthResponseDto.error("Customer not found");
            }

            Customer customer = customerOpt.get();

            // Check if username is being changed and if it's available
            if (!customer.getUsername().equals(updateDto.getUsername()) &&
                !isUsernameAvailable(updateDto.getUsername())) {
                return AuthResponseDto.error("Username already exists");
            }

            // Check if email is being changed and if it's available
            if (!customer.getEmail().equals(updateDto.getEmail()) &&
                !isEmailAvailable(updateDto.getEmail())) {
                return AuthResponseDto.error("Email already exists");
            }

            // Update customer fields
            customer.setUsername(updateDto.getUsername());
            customer.setEmail(updateDto.getEmail());
            customer.setName(updateDto.getName());
            customer.setPhone(updateDto.getPhone());

            // Update password if provided
            if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
                customer.setPassword(passwordEncoder.encode(updateDto.getPassword()));
            }

            Customer updatedCustomer = customerRepository.save(customer);

            return AuthResponseDto.success(
                "Customer updated successfully",
                updatedCustomer.getId(),
                updatedCustomer.getUsername(),
                updatedCustomer.getEmail(),
                updatedCustomer.getName(),
                updatedCustomer.getPhone()
            );

        } catch (Exception e) {
            return AuthResponseDto.error("Update failed: " + e.getMessage());
        }
    }
}
