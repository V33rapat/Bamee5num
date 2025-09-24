package com.restaurant.demo.repository;

import com.restaurant.demo.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCustomerId(int customerId);
    Optional<CartItem> findByCustomerIdAndName(int customerId, String name);
}

