package com.restaurant.demo.repository;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCustomer(Customer customer);

    List<CartItem> findByCustomerAndStatus(Customer customer, String status);

    List<CartItem> findByStatus(String status);

    Optional<CartItem> findByCustomer_IdAndItemName(Long customerId, String itemName);

    @Query("SELECT c FROM CartItem c WHERE LOWER(c.status) = LOWER(:status)")
    List<CartItem> findByStatusIgnoreCase(@Param("status") String status);
}
