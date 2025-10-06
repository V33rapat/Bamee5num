package com.restaurant.demo.repository;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ดึงรายการตาม Customer entity
    List<CartItem> findByCustomer(Customer customer);

    // ดึงรายการตาม customerId (ผ่าน customer entity)
    List<CartItem> findByCustomer_Id(Long customerId);

    // ดึงรายการตาม Customer และ Status
    List<CartItem> findByCustomerAndStatus(Customer customer, String status);

    // ดึงรายการตาม Status
    List<CartItem> findByStatus(String status);

    // ดึงรายการตาม customerId และชื่อ item
    Optional<CartItem> findByCustomer_IdAndItemName(Long customerId, String itemName);
}
