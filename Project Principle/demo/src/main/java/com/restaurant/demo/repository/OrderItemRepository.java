package com.restaurant.demo.repository;

import com.restaurant.demo.model.Order;
import com.restaurant.demo.model.OrderItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<Order, Long> {

    void save(List<OrderItem> orderItems);
}
