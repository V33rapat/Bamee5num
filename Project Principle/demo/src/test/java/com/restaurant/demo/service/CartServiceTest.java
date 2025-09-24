package com.restaurant.demo.service;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.repository.CartItemRepository;
import com.restaurant.demo.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CartService cartService;

    private Customer mockCustomer;
    private Customer otherCustomer;
    private CartItem mockCartItem;
    private CartItem existingCartItem;

    @BeforeEach
    void setUp() {
        mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setUsername("testuser");
        mockCustomer.setEmail("test@example.com");
        mockCustomer.setName("Test User");
        mockCustomer.setCreatedAt(LocalDateTime.now());
        mockCustomer.setUpdatedAt(LocalDateTime.now());

        otherCustomer = new Customer();
        otherCustomer.setId(2L);
        otherCustomer.setUsername("otheruser");
        otherCustomer.setEmail("other@example.com");
        otherCustomer.setName("Other User");

        mockCartItem = new CartItem();
        mockCartItem.setId(1L);
        mockCartItem.setCustomer(mockCustomer);
        mockCartItem.setItemName("Pizza Margherita");
        mockCartItem.setItemPrice(new BigDecimal("15.99"));
        mockCartItem.setQuantity(2);
        mockCartItem.setCreatedAt(LocalDateTime.now());
        mockCartItem.setUpdatedAt(LocalDateTime.now());

        existingCartItem = new CartItem();
        existingCartItem.setId(2L);
        existingCartItem.setCustomer(mockCustomer);
        existingCartItem.setItemName("Burger Deluxe");
        existingCartItem.setItemPrice(new BigDecimal("12.50"));
        existingCartItem.setQuantity(1);
        existingCartItem.setCreatedAt(LocalDateTime.now());
        existingCartItem.setUpdatedAt(LocalDateTime.now());
    }

    // Tests for addToCart with customerId
    @Test
    void addToCart_WithValidCustomerId_ShouldAddNewItem() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(cartItemRepository.findByCustomerAndItemName(mockCustomer, "Pizza Margherita")).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(mockCartItem);

        // Act
        CartItem result = cartService.addToCart(1L, "Pizza Margherita", new BigDecimal("15.99"), 2);

        // Assert
        assertNotNull(result);
        assertEquals("Pizza Margherita", result.getItemName());
        assertEquals(new BigDecimal("15.99"), result.getItemPrice());
        assertEquals(2, result.getQuantity());
        verify(customerRepository).findById(1L);
        verify(cartItemRepository).findByCustomerAndItemName(mockCustomer, "Pizza Margherita");
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addToCart_WithInvalidCustomerId_ShouldThrowException() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> cartService.addToCart(999L, "Pizza", new BigDecimal("10.00"), 1)
        );

        assertTrue(exception.getMessage().contains("Customer not found"));
        verify(customerRepository).findById(999L);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addToCart_WithNullCustomerId_ShouldThrowException() {
        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> cartService.addToCart(null, "Pizza", new BigDecimal("10.00"), 1)
        );

        assertTrue(exception.getMessage().contains("Customer ID is required"));
        verify(customerRepository, never()).findById(anyLong());
    }

    // Tests for addToCart with Customer object
    @Test
    void addToCart_WithExistingItem_ShouldIncreaseQuantity() {
        // Arrange
        existingCartItem.setQuantity(3);
        when(cartItemRepository.findByCustomerAndItemName(mockCustomer, "Pizza Margherita")).thenReturn(Optional.of(existingCartItem));
        when(cartItemRepository.save(existingCartItem)).thenReturn(existingCartItem);

        // Act
        CartItem result = cartService.addToCart(mockCustomer, "Pizza Margherita", new BigDecimal("15.99"), 2);

        // Assert
        assertEquals(5, result.getQuantity()); // 3 existing + 2 new = 5
        verify(cartItemRepository).findByCustomerAndItemName(mockCustomer, "Pizza Margherita");
        verify(cartItemRepository).save(existingCartItem);
    }

    @Test
    void addToCart_WithQuantityExceedingLimit_ShouldCapAt99() {
        // Arrange
        existingCartItem.setQuantity(95);
        when(cartItemRepository.findByCustomerAndItemName(mockCustomer, "Pizza Margherita")).thenReturn(Optional.of(existingCartItem));
        when(cartItemRepository.save(existingCartItem)).thenReturn(existingCartItem);

        // Act
        CartItem result = cartService.addToCart(mockCustomer, "Pizza Margherita", new BigDecimal("15.99"), 10);

        // Assert
        assertEquals(99, result.getQuantity()); // Should be capped at 99
        verify(cartItemRepository).save(existingCartItem);
    }

    @Test
    void addToCart_WithInvalidQuantity_ShouldThrowException() {
        // Act & Assert
        RuntimeException exception1 = assertThrows(
            RuntimeException.class,
            () -> cartService.addToCart(mockCustomer, "Pizza", new BigDecimal("10.00"), 0)
        );
        assertTrue(exception1.getMessage().contains("Quantity must be at least 1"));

        RuntimeException exception2 = assertThrows(
            RuntimeException.class,
            () -> cartService.addToCart(mockCustomer, "Pizza", new BigDecimal("10.00"), 100)
        );
        assertTrue(exception2.getMessage().contains("Quantity cannot exceed 99"));
    }

    @Test
    void addToCart_WithNullCustomer_ShouldThrowException() {
        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> cartService.addToCart(null, "Pizza", new BigDecimal("10.00"), 1)
        );

        assertTrue(exception.getMessage().contains("Customer authentication required"));
    }

    // Tests for incrementQuantity
    @Test
    void incrementQuantity_WithValidItem_ShouldIncreaseQuantity() {
        // Arrange
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));
        when(cartItemRepository.save(mockCartItem)).thenReturn(mockCartItem);

        // Act
        CartItem result = cartService.incrementQuantity(1L, mockCustomer);

        // Assert
        assertEquals(3, result.getQuantity()); // 2 + 1 = 3
        verify(cartItemRepository).findById(1L);
        verify(cartItemRepository).save(mockCartItem);
    }

    @Test
    void incrementQuantity_WithQuantityAt99_ShouldThrowException() {
        // Arrange
        mockCartItem.setQuantity(99);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> cartService.incrementQuantity(1L, mockCustomer)
        );

        assertTrue(exception.getMessage().contains("Quantity cannot exceed 99"));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void incrementQuantity_WithWrongOwner_ShouldThrowException() {
        // Arrange
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> cartService.incrementQuantity(1L, otherCustomer)
        );

        assertTrue(exception.getMessage().contains("Access denied"));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    // Tests for decrementQuantity
    @Test
    void decrementQuantity_WithValidItem_ShouldDecreaseQuantity() {
        // Arrange
        mockCartItem.setQuantity(3);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));
        when(cartItemRepository.save(mockCartItem)).thenReturn(mockCartItem);

        // Act
        CartItem result = cartService.decrementQuantity(1L, mockCustomer);

        // Assert
        assertEquals(2, result.getQuantity()); // 3 - 1 = 2
        verify(cartItemRepository).save(mockCartItem);
    }

    @Test
    void decrementQuantity_WithQuantityAt1_ShouldThrowException() {
        // Arrange
        mockCartItem.setQuantity(1);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> cartService.decrementQuantity(1L, mockCustomer)
        );

        assertTrue(exception.getMessage().contains("Quantity cannot be less than 1"));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    // Tests for updateQuantity
    @Test
    void updateQuantity_WithValidQuantity_ShouldUpdateQuantity() {
        // Arrange
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));
        when(cartItemRepository.save(mockCartItem)).thenReturn(mockCartItem);

        // Act
        CartItem result = cartService.updateQuantity(1L, 5, mockCustomer);

        // Assert
        assertEquals(5, result.getQuantity());
        verify(cartItemRepository).save(mockCartItem);
    }

    @Test
    void updateQuantity_WithInvalidQuantity_ShouldThrowException() {
        // Act & Assert
        RuntimeException exception1 = assertThrows(
            RuntimeException.class,
            () -> cartService.updateQuantity(1L, 0, mockCustomer)
        );
        assertTrue(exception1.getMessage().contains("Quantity must be at least 1"));

        RuntimeException exception2 = assertThrows(
            RuntimeException.class,
            () -> cartService.updateQuantity(1L, 100, mockCustomer)
        );
        assertTrue(exception2.getMessage().contains("Quantity cannot exceed 99"));
    }

    // Tests for removeFromCart
    @Test
    void removeFromCart_WithValidItem_ShouldRemoveItem() {
        // Arrange
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));

        // Act
        cartService.removeFromCart(1L, mockCustomer);

        // Assert
        verify(cartItemRepository).findById(1L);
        verify(cartItemRepository).deleteById(1L);
    }

    @Test
    void removeFromCart_WithNonExistentItem_ShouldThrowException() {
        // Arrange
        when(cartItemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> cartService.removeFromCart(999L, mockCustomer)
        );

        assertTrue(exception.getMessage().contains("Cart item not found"));
        verify(cartItemRepository, never()).deleteById(anyLong());
    }

    // Tests for getCartByCustomer
    @Test
    void getCartByCustomer_WithValidCustomer_ShouldReturnCartItems() {
        // Arrange
        List<CartItem> cartItems = Arrays.asList(mockCartItem, existingCartItem);
        when(cartItemRepository.findByCustomer(mockCustomer)).thenReturn(cartItems);

        // Act
        List<CartItem> result = cartService.getCartByCustomer(mockCustomer);

        // Assert
        assertEquals(2, result.size());
        assertEquals(cartItems, result);
        verify(cartItemRepository).findByCustomer(mockCustomer);
    }

    @Test
    void getCartByCustomer_WithNullCustomer_ShouldThrowException() {
        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> cartService.getCartByCustomer(null)
        );

        assertTrue(exception.getMessage().contains("Customer authentication required"));
    }

    // Tests for clearCart
    @Test
    void clearCart_WithValidCustomer_ShouldRemoveAllItems() {
        // Arrange
        List<CartItem> cartItems = Arrays.asList(mockCartItem, existingCartItem);
        when(cartItemRepository.findByCustomer(mockCustomer)).thenReturn(cartItems);

        // Act
        cartService.clearCart(mockCustomer);

        // Assert
        verify(cartItemRepository).findByCustomer(mockCustomer);
        verify(cartItemRepository).deleteAll(cartItems);
    }

    // Tests for calculateCartTotal
    @Test
    void calculateCartTotal_WithValidCustomerId_ShouldReturnCorrectTotal() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        List<CartItem> cartItems = Arrays.asList(mockCartItem, existingCartItem);
        when(cartItemRepository.findByCustomer(mockCustomer)).thenReturn(cartItems);

        // Act
        BigDecimal total = cartService.calculateCartTotal(1L);

        // Assert
        // mockCartItem: 15.99 * 2 = 31.98
        // existingCartItem: 12.50 * 1 = 12.50
        // Total: 44.48
        assertEquals(new BigDecimal("44.48"), total);
        verify(customerRepository).findById(1L);
        verify(cartItemRepository).findByCustomer(mockCustomer);
    }

    @Test
    void calculateCartTotal_WithEmptyCart_ShouldReturnZero() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(cartItemRepository.findByCustomer(mockCustomer)).thenReturn(Arrays.asList());

        // Act
        BigDecimal total = cartService.calculateCartTotal(1L);

        // Assert
        assertEquals(BigDecimal.ZERO, total);
    }

    // Tests for wrapper methods with customerId
    @Test
    void getCartItems_WithValidCustomerId_ShouldReturnItems() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        List<CartItem> cartItems = Arrays.asList(mockCartItem);
        when(cartItemRepository.findByCustomer(mockCustomer)).thenReturn(cartItems);

        // Act
        List<CartItem> result = cartService.getCartItems(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(mockCartItem, result.get(0));
    }

    @Test
    void updateCartItemQuantity_WithValidData_ShouldUpdateQuantity() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));
        when(cartItemRepository.save(mockCartItem)).thenReturn(mockCartItem);

        // Act
        CartItem result = cartService.updateCartItemQuantity(1L, 1L, 5);

        // Assert
        assertEquals(5, result.getQuantity());
    }

    @Test
    void getCartItem_WithValidData_ShouldReturnItem() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));

        // Act
        CartItem result = cartService.getCartItem(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockCartItem.getId(), result.getId());
    }

    @Test
    void clearCart_WithValidCustomerId_ShouldClearCart() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        List<CartItem> cartItems = Arrays.asList(mockCartItem);
        when(cartItemRepository.findByCustomer(mockCustomer)).thenReturn(cartItems);

        // Act
        cartService.clearCart(1L);

        // Assert
        verify(cartItemRepository).deleteAll(cartItems);
    }
}
