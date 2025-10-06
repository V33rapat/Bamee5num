package com.restaurant.demo.service;

import com.restaurant.demo.dto.CartItemDto;
import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.model.MenuItem;
import com.restaurant.demo.repository.CartItemRepository;
import com.restaurant.demo.repository.CustomerRepository;
import com.restaurant.demo.repository.MenuItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MenuItemRepo menuItemRepository;

    // Helper method to get customer by ID
    private Customer getCustomerById(Long customerId) {
        if (customerId == null) {
            throw new RuntimeException("Customer ID is required");
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
    }

    public CartItem addToCart(Long customerId, Long menuItemId, Integer quantity) {
        Customer customer = getCustomerById(customerId);
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + menuItemId));

        BigDecimal itemPrice = BigDecimal.valueOf(menuItem.getPrice());
        return addToCart(customer, menuItem.getName(), itemPrice, quantity);
    }

    public CartItem updateCartItemQuantity(Long cartItemId, Long customerId, Integer quantity) {
        Customer customer = getCustomerById(customerId);
        return updateQuantity(cartItemId, quantity, customer);
    }

    public void removeFromCart(Long cartItemId, Long customerId) {
        Customer customer = getCustomerById(customerId);
        removeFromCart(cartItemId, customer);
    }

    public List<CartItem> getCartByCustomer(Customer customer) {
        if (customer == null) {
            throw new RuntimeException("Customer authentication required");
        }
        return cartItemRepository.findByCustomer(customer);
    }

    public CartItem getCartItem(Long cartItemId, Long customerId) {
        Customer customer = getCustomerById(customerId);
        return getCartItem(cartItemId, customer).orElse(null);
    }

    public void clearCart(Long customerId) {
        Customer customer = getCustomerById(customerId);
        clearCart(customer);
    }

    public BigDecimal calculateCartTotal(Long customerId) {
        Customer customer = getCustomerById(customerId);
        List<CartItem> items = getCartByCustomer(customer);
        return items.stream()
                .map(item -> item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    public List<CartItem> getCartItems(Long customerId) {
        Customer customer = getCustomerById(customerId);
        return cartItemRepository.findByCustomer(customer);
    }

    public List<CartItem> getCartByCustomerId(Long customerId) {
        Customer customer = getCustomerById(customerId);
        return cartItemRepository.findByCustomer(customer);
    }

    public CartItem addToCart(Customer customer, String name, BigDecimal price, int quantity) {
        if (customer == null) {
            throw new RuntimeException("Customer authentication required");
        }
        if (quantity < 1 || quantity > 99) {
            throw new RuntimeException("Quantity must be between 1 and 99");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCustomer_IdAndItemName(customer.getId(), name);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = Math.min(item.getQuantity() + quantity, 99);
            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(customer, name, price, quantity);
            return cartItemRepository.save(newItem);
        }
    }

    public CartItem incrementQuantity(Long itemId, Customer customer) {
        CartItem item = getCartItem(itemId, customer).orElseThrow(() -> new RuntimeException("Cart item not found"));
        int newQuantity = item.getQuantity() + 1;
        if (newQuantity > 99) throw new RuntimeException("Quantity cannot exceed 99");
        item.setQuantity(newQuantity);
        return cartItemRepository.save(item);
    }

    public CartItem decrementQuantity(Long itemId, Customer customer) {
        CartItem item = getCartItem(itemId, customer).orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (item.getQuantity() <= 1) throw new RuntimeException("Quantity cannot be less than 1");
        item.setQuantity(item.getQuantity() - 1);
        return cartItemRepository.save(item);
    }

    public CartItem updateQuantity(Long itemId, int quantity, Customer customer) {
        if (quantity < 1 || quantity > 99) throw new RuntimeException("Quantity must be between 1 and 99");
        CartItem item = getCartItem(itemId, customer).orElseThrow(() -> new RuntimeException("Cart item not found"));
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeFromCart(Long itemId, Customer customer) {
        CartItem item = getCartItem(itemId, customer).orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(item);
    }

    public void clearCart(Customer customer) {
        List<CartItem> items = cartItemRepository.findByCustomer(customer);
        cartItemRepository.deleteAll(items);
    }

    public Optional<CartItem> getCartItem(Long itemId, Customer customer) {
        Optional<CartItem> optionalItem = cartItemRepository.findById(itemId);
        optionalItem.ifPresent(item -> {
            if (!item.getCustomer().getId().equals(customer.getId())) {
                throw new RuntimeException("Access denied: Item does not belong to customer");
            }
        });
        return optionalItem;
    }

    public CartItem updateCartItem(CartItem cartItem, Customer customer) {
        if (cartItem.getId() != null) {
            Optional<CartItem> existingItem = cartItemRepository.findById(cartItem.getId());
            existingItem.ifPresent(item -> {
                if (!item.getCustomer().getId().equals(customer.getId())) {
                    throw new RuntimeException("Access denied: Item does not belong to customer");
                }
            });
        }
        cartItem.setCustomer(customer);
        return cartItemRepository.save(cartItem);
    }

    public CartItemDto toDto(CartItem item) {
        return new CartItemDto(
                item.getId(),
                item.getCustomer().getId(),
                item.getItemName(),
                item.getItemPrice(),
                item.getQuantity(),
                item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                item.getStatus(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

    public List<CartItemDto> toDtoList(List<CartItem> items) {
        return items.stream().map(this::toDto).collect(Collectors.toList());
    }

    // ------------------ Status Methods ------------------
    @Transactional
    public void finishCart(Long customerId) {
        Customer customer = getCustomerById(customerId);
        List<CartItem> items = cartItemRepository.findByCustomerAndStatus(customer, CartItem.STATUS_PENDING);
        if (items.isEmpty()) throw new RuntimeException("Cart is empty. Cannot place order.");
        items.forEach(item -> item.setStatus(CartItem.STATUS_FINISH));
        cartItemRepository.saveAll(items);
    }

    @Transactional
    public void resetCart(Long customerId) {
        Customer customer = getCustomerById(customerId);
        List<CartItem> items = cartItemRepository.findByCustomerAndStatus(customer, CartItem.STATUS_FINISH);
        items.forEach(item -> item.setStatus(CartItem.STATUS_PENDING));
        cartItemRepository.saveAll(items);
    }
}
