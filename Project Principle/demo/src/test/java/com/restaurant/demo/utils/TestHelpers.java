package com.restaurant.demo.utils;

import com.restaurant.demo.model.Customer;
import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.repository.CustomerRepository;
import com.restaurant.demo.repository.CartItemRepository;
import com.restaurant.demo.fixtures.TestDataFixtures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Test helper methods for common database operations in tests
 */
@Component
@Transactional
public class TestHelpers {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    /**
     * Creates and saves a test customer to the database
     */
    public Customer createAndSaveTestCustomer() {
        Customer customer = TestDataFixtures.createTestCustomer();
        return customerRepository.save(customer);
    }

    /**
     * Creates and saves a second test customer to the database
     */
    public Customer createAndSaveTestCustomer2() {
        Customer customer = TestDataFixtures.createTestCustomer2();
        return customerRepository.save(customer);
    }

    /**
     * Creates and saves a custom customer to the database
     */
    public Customer createAndSaveCustomer(String name, String email, String phone, String password) {
        Customer customer = TestDataFixtures.createCustomer(name, email, phone, password);
        return customerRepository.save(customer);
    }

    /**
     * Creates and saves a test cart item for the given customer
     */
    public CartItem createAndSaveTestCartItem(Customer customer) {
        CartItem cartItem = TestDataFixtures.createTestCartItem(customer);
        return cartItemRepository.save(cartItem);
    }

    /**
     * Creates and saves a second test cart item for the given customer
     */
    public CartItem createAndSaveTestCartItem2(Customer customer) {
        CartItem cartItem = TestDataFixtures.createTestCartItem2(customer);
        return cartItemRepository.save(cartItem);
    }

    /**
     * Creates and saves a custom cart item for the given customer
     */
    public CartItem createAndSaveCartItem(Customer customer, String itemName, BigDecimal price, int quantity) {
        CartItem cartItem = TestDataFixtures.createCartItem(customer, itemName, price, quantity);
        return cartItemRepository.save(cartItem);
    }

    /**
     * Creates a customer with a full cart (multiple items)
     */
    public Customer createCustomerWithFullCart() {
        Customer customer = createAndSaveTestCustomer();
        createAndSaveTestCartItem(customer);
        createAndSaveTestCartItem2(customer);
        createAndSaveCartItem(customer, "Green Curry", new BigDecimal("11.50"), 1);
        return customer;
    }

    /**
     * Cleans up all test data from the database
     */
    public void cleanupTestData() {
        cartItemRepository.deleteAll();
        customerRepository.deleteAll();
    }

    /**
     * Gets the total number of cart items for a customer
     */
    public long getCartItemCount(Customer customer) {
        return cartItemRepository.findByCustomer(customer).size();
    }

    /**
     * Gets all cart items for a customer
     */
    public List<CartItem> getCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    /**
     * Calculates the total price of all items in a customer's cart
     */
    public BigDecimal calculateCartTotal(Customer customer) {
        List<CartItem> cartItems = getCartItems(customer);
        return cartItems.stream()
                .map(item -> item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Verifies that a customer exists in the database
     */
    public boolean customerExists(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    /**
     * Verifies that a cart item exists for a customer
     */
    public boolean cartItemExists(Customer customer, String itemName) {
        return cartItemRepository.findByCustomerAndItemName(customer, itemName).isPresent();
    }
}
