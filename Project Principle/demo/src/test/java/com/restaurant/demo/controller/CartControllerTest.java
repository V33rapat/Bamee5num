package com.restaurant.demo.controller;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    private CartItem mockCartItem;
    private CartItem mockCartItem2;

    @BeforeEach
    void setUp() {
        CartController cartController = new CartController();
        ReflectionTestUtils.setField(cartController, "cartService", cartService);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setUsername("testuser");
        mockCustomer.setEmail("test@example.com");
        mockCustomer.setName("Test User");
        mockCustomer.setPhone("+1234567890");
        mockCustomer.setCreatedAt(LocalDateTime.now());
        mockCustomer.setUpdatedAt(LocalDateTime.now());

        mockCartItem = new CartItem();
        mockCartItem.setId(1L);
        mockCartItem.setCustomer(mockCustomer);
        mockCartItem.setItemName("Pizza Margherita");
        mockCartItem.setItemPrice(new BigDecimal("15.99"));
        mockCartItem.setQuantity(2);
        mockCartItem.setCreatedAt(LocalDateTime.now());
        mockCartItem.setUpdatedAt(LocalDateTime.now());

        mockCartItem2 = new CartItem();
        mockCartItem2.setId(2L);
        mockCartItem2.setCustomer(mockCustomer);
        mockCartItem2.setItemName("Burger Deluxe");
        mockCartItem2.setItemPrice(new BigDecimal("12.50"));
        mockCartItem2.setQuantity(1);
        mockCartItem2.setCreatedAt(LocalDateTime.now());
        mockCartItem2.setUpdatedAt(LocalDateTime.now());
    }

    // Tests for POST /api/cart/add
    @Test
    void addToCart_WithValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        when(cartService.addToCart(anyLong(), anyString(), any(BigDecimal.class), anyInt()))
                .thenReturn(mockCartItem);

        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "1")
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "15.99")
                        .param("quantity", "2"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemName").value("Pizza Margherita"))
                .andExpect(jsonPath("$.itemPrice").value(15.99))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void addToCart_WithInvalidCustomerId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "0") // Invalid ID
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "15.99")
                        .param("quantity", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addToCart_WithNegativeCustomerId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "-1")
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "15.99")
                        .param("quantity", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addToCart_WithMissingCustomerId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "15.99")
                        .param("quantity", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addToCart_WithEmptyItemName_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "1")
                        .param("itemName", "")
                        .param("itemPrice", "15.99")
                        .param("quantity", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addToCart_WithInvalidItemPrice_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "1")
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "0.00") // Invalid price
                        .param("quantity", "2"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "1")
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "10000.00") // Price too high
                        .param("quantity", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addToCart_WithInvalidQuantity_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "1")
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "15.99")
                        .param("quantity", "0")) // Invalid quantity
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "1")
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "15.99")
                        .param("quantity", "101")) // Quantity too high
                .andExpect(status().isBadRequest());
    }

    @Test
    void addToCart_WithServiceException_ShouldReturnError() throws Exception {
        // Arrange
        when(cartService.addToCart(anyLong(), anyString(), any(BigDecimal.class), anyInt()))
                .thenThrow(new RuntimeException("Customer not found"));

        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "999")
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "15.99")
                        .param("quantity", "2"))
                .andExpect(status().isInternalServerError());
    }

    // Tests for PUT /api/cart/update/{cartItemId}
    @Test
    void updateCartItem_WithValidData_ShouldReturnOk() throws Exception {
        // Arrange
        mockCartItem.setQuantity(5);
        when(cartService.updateCartItemQuantity(anyLong(), anyLong(), anyInt()))
                .thenReturn(mockCartItem);

        // Act & Assert
        mockMvc.perform(put("/api/cart/update/1")
                        .param("customerId", "1")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void updateCartItem_WithInvalidCartItemId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/cart/update/0")
                        .param("customerId", "1")
                        .param("quantity", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCartItem_WithNonExistentItem_ShouldReturnError() throws Exception {
        // Arrange
        when(cartService.updateCartItemQuantity(anyLong(), anyLong(), anyInt()))
                .thenThrow(new RuntimeException("Cart item not found"));

        // Act & Assert
        mockMvc.perform(put("/api/cart/update/999")
                        .param("customerId", "1")
                        .param("quantity", "5"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateCartItem_WithUnauthorizedCustomer_ShouldReturnError() throws Exception {
        // Arrange
        when(cartService.updateCartItemQuantity(anyLong(), anyLong(), anyInt()))
                .thenThrow(new RuntimeException("Access denied: Item does not belong to authenticated customer"));

        // Act & Assert
        mockMvc.perform(put("/api/cart/update/1")
                        .param("customerId", "2") // Different customer
                        .param("quantity", "5"))
                .andExpect(status().isInternalServerError());
    }

    // Tests for DELETE /api/cart/remove/{cartItemId}
    @Test
    void removeFromCart_WithValidData_ShouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/cart/remove/1")
                        .param("customerId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeFromCart_WithInvalidCartItemId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/cart/remove/0")
                        .param("customerId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeFromCart_WithNonExistentItem_ShouldReturnError() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Cart item not found"))
                .when(cartService).removeFromCart(anyLong(), anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/cart/remove/999")
                        .param("customerId", "1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void removeFromCart_WithUnauthorizedCustomer_ShouldReturnError() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Access denied: Item does not belong to authenticated customer"))
                .when(cartService).removeFromCart(anyLong(), anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/cart/remove/1")
                        .param("customerId", "2"))
                .andExpect(status().isInternalServerError());
    }

    // Tests for GET /api/cart/customer/{customerId}
    @Test
    void getCartItems_WithValidCustomerId_ShouldReturnOk() throws Exception {
        // Arrange
        List<CartItem> cartItems = Arrays.asList(mockCartItem, mockCartItem2);
        when(cartService.getCartItems(1L)).thenReturn(cartItems);

        // Act & Assert
        mockMvc.perform(get("/api/cart/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].itemName").value("Pizza Margherita"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].itemName").value("Burger Deluxe"));
    }

    @Test
    void getCartItems_WithEmptyCart_ShouldReturnEmptyArray() throws Exception {
        // Arrange
        when(cartService.getCartItems(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/cart/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getCartItems_WithInvalidCustomerId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/cart/customer/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCartItems_WithNonExistentCustomer_ShouldReturnError() throws Exception {
        // Arrange
        when(cartService.getCartItems(999L))
                .thenThrow(new RuntimeException("Customer not found"));

        // Act & Assert
        mockMvc.perform(get("/api/cart/customer/999"))
                .andExpect(status().isInternalServerError());
    }

    // Tests for GET /api/cart/{cartItemId}
    @Test
    void getCartItem_WithValidData_ShouldReturnOk() throws Exception {
        // Arrange
        when(cartService.getCartItem(1L, 1L)).thenReturn(mockCartItem);

        // Act & Assert
        mockMvc.perform(get("/api/cart/1")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemName").value("Pizza Margherita"));
    }

    @Test
    void getCartItem_WithNonExistentItem_ShouldReturnOk() throws Exception {
        // Arrange - Service returns null for non-existent items
        when(cartService.getCartItem(999L, 1L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/cart/999")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void getCartItem_WithInvalidIds_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/cart/0")
                        .param("customerId", "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/cart/1")
                        .param("customerId", "0"))
                .andExpect(status().isBadRequest());
    }

    // Tests for DELETE /api/cart/clear/{customerId}
    @Test
    void clearCart_WithValidCustomerId_ShouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/cart/clear/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void clearCart_WithInvalidCustomerId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/cart/clear/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clearCart_WithNonExistentCustomer_ShouldReturnError() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Customer not found"))
                .when(cartService).clearCart(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/cart/clear/999"))
                .andExpect(status().isInternalServerError());
    }

    // Tests for GET /api/cart/total/{customerId}
    @Test
    void getCartTotal_WithValidCustomerId_ShouldReturnOk() throws Exception {
        // Arrange
        BigDecimal total = new BigDecimal("44.48"); // 15.99*2 + 12.50*1
        when(cartService.calculateCartTotal(1L)).thenReturn(total);

        // Act & Assert
        mockMvc.perform(get("/api/cart/total/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("44.48"));
    }

    @Test
    void getCartTotal_WithEmptyCart_ShouldReturnZero() throws Exception {
        // Arrange
        when(cartService.calculateCartTotal(1L)).thenReturn(BigDecimal.ZERO);

        // Act & Assert
        mockMvc.perform(get("/api/cart/total/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("0"));
    }

    @Test
    void getCartTotal_WithInvalidCustomerId_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/cart/total/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCartTotal_WithNonExistentCustomer_ShouldReturnError() throws Exception {
        // Arrange
        when(cartService.calculateCartTotal(999L))
                .thenThrow(new RuntimeException("Customer not found"));

        // Act & Assert
        mockMvc.perform(get("/api/cart/total/999"))
                .andExpect(status().isInternalServerError());
    }

    // CORS tests
    @Test
    void addToCart_ShouldAllowCorsRequests() throws Exception {
        // Arrange
        when(cartService.addToCart(anyLong(), anyString(), any(BigDecimal.class), anyInt()))
                .thenReturn(mockCartItem);

        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .header("Origin", "http://localhost:3000")
                        .param("customerId", "1")
                        .param("itemName", "Pizza Margherita")
                        .param("itemPrice", "15.99")
                        .param("quantity", "2"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    // Edge cases and validation tests
    @Test
    void addToCart_WithLongItemName_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String longName = "A".repeat(101); // Exceeds 100 character limit

        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "1")
                        .param("itemName", longName)
                        .param("itemPrice", "15.99")
                        .param("quantity", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addToCart_WithValidBoundaryValues_ShouldReturnCreated() throws Exception {
        // Arrange
        when(cartService.addToCart(anyLong(), anyString(), any(BigDecimal.class), anyInt()))
                .thenReturn(mockCartItem);

        // Act & Assert - Test minimum valid values
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "1")
                        .param("itemName", "A") // Minimum length
                        .param("itemPrice", "0.01") // Minimum price
                        .param("quantity", "1")) // Minimum quantity
                .andExpect(status().isCreated());

        // Act & Assert - Test maximum valid values
        mockMvc.perform(post("/api/cart/add")
                        .param("customerId", "1")
                        .param("itemName", "A".repeat(100)) // Maximum length
                        .param("itemPrice", "9999.99") // Maximum price
                        .param("quantity", "100")) // Maximum quantity
                .andExpect(status().isCreated());
    }
}
