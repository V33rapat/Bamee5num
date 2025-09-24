package com.restaurant.demo.fixtures;

import com.restaurant.demo.model.Customer;
import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.dto.CustomerRegistrationDto;
import com.restaurant.demo.dto.CustomerLoginDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Test data fixtures for creating consistent test data across all tests
 */
public class TestDataFixtures {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Customer test data
    public static final String TEST_CUSTOMER_NAME = "John Doe";
    public static final String TEST_CUSTOMER_EMAIL = "john.doe@example.com";
    public static final String TEST_CUSTOMER_PHONE = "1234567890";
    public static final String TEST_CUSTOMER_PASSWORD = "password123";

    public static final String TEST_CUSTOMER_2_NAME = "Jane Smith";
    public static final String TEST_CUSTOMER_2_EMAIL = "jane.smith@example.com";
    public static final String TEST_CUSTOMER_2_PHONE = "0987654321";
    public static final String TEST_CUSTOMER_2_PASSWORD = "password456";

    // Cart item test data
    public static final String TEST_ITEM_NAME = "Chicken Pad Thai";
    public static final BigDecimal TEST_ITEM_PRICE = new BigDecimal("12.99");
    public static final int TEST_ITEM_QUANTITY = 2;

    public static final String TEST_ITEM_2_NAME = "Tom Yum Soup";
    public static final BigDecimal TEST_ITEM_2_PRICE = new BigDecimal("8.50");
    public static final int TEST_ITEM_2_QUANTITY = 1;

    /**
     * Creates a test customer with default values
     */
    public static Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setName(TEST_CUSTOMER_NAME);
        customer.setEmail(TEST_CUSTOMER_EMAIL);
        customer.setPhone(TEST_CUSTOMER_PHONE);
        customer.setPasswordHash(passwordEncoder.encode(TEST_CUSTOMER_PASSWORD));
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        return customer;
    }

    /**
     * Creates a second test customer with different values
     */
    public static Customer createTestCustomer2() {
        Customer customer = new Customer();
        customer.setName(TEST_CUSTOMER_2_NAME);
        customer.setEmail(TEST_CUSTOMER_2_EMAIL);
        customer.setPhone(TEST_CUSTOMER_2_PHONE);
        customer.setPasswordHash(passwordEncoder.encode(TEST_CUSTOMER_2_PASSWORD));
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        return customer;
    }

    /**
     * Creates a customer with custom values
     */
    public static Customer createCustomer(String name, String email, String phone, String password) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setPasswordHash(passwordEncoder.encode(password));
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        return customer;
    }

    /**
     * Creates a test cart item with default values
     */
    public static CartItem createTestCartItem(Customer customer) {
        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setItemName(TEST_ITEM_NAME);
        cartItem.setItemPrice(TEST_ITEM_PRICE);
        cartItem.setQuantity(TEST_ITEM_QUANTITY);
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        return cartItem;
    }

    /**
     * Creates a second test cart item with different values
     */
    public static CartItem createTestCartItem2(Customer customer) {
        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setItemName(TEST_ITEM_2_NAME);
        cartItem.setItemPrice(TEST_ITEM_2_PRICE);
        cartItem.setQuantity(TEST_ITEM_2_QUANTITY);
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        return cartItem;
    }

    /**
     * Creates a cart item with custom values
     */
    public static CartItem createCartItem(Customer customer, String itemName, BigDecimal price, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setItemName(itemName);
        cartItem.setItemPrice(price);
        cartItem.setQuantity(quantity);
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        return cartItem;
    }

    /**
     * Creates a customer registration DTO with default values
     */
    public static CustomerRegistrationDto createTestRegistrationDto() {
        CustomerRegistrationDto dto = new CustomerRegistrationDto();
        dto.setName(TEST_CUSTOMER_NAME);
        dto.setEmail(TEST_CUSTOMER_EMAIL);
        dto.setPhone(TEST_CUSTOMER_PHONE);
        dto.setPassword(TEST_CUSTOMER_PASSWORD);
        return dto;
    }

    /**
     * Creates a customer registration DTO with custom values
     */
    public static CustomerRegistrationDto createRegistrationDto(String name, String email, String phone, String password) {
        CustomerRegistrationDto dto = new CustomerRegistrationDto();
        dto.setName(name);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setPassword(password);
        return dto;
    }

    /**
     * Creates a customer login DTO with default values
     */
    public static CustomerLoginDto createTestLoginDto() {
        CustomerLoginDto dto = new CustomerLoginDto();
        dto.setUsernameOrEmail(TEST_CUSTOMER_EMAIL);
        dto.setPassword(TEST_CUSTOMER_PASSWORD);
        return dto;
    }

    /**
     * Creates a customer login DTO with custom values
     */
    public static CustomerLoginDto createLoginDto(String usernameOrEmail, String password) {
        CustomerLoginDto dto = new CustomerLoginDto();
        dto.setUsernameOrEmail(usernameOrEmail);
        dto.setPassword(password);
        return dto;
    }
}
