package com.restaurant.demo.repository;

import com.restaurant.demo.model.Order;
import com.restaurant.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find all orders for a specific customer
     * @param customerId The customer ID
     * @return List of orders for the customer
     */
    List<Order> findByCustomer_Id(Long customerId);
    
    /**
     * Find all orders with a specific status
     * @param status The order status (Pending, In Progress, Finish, Cancelled)
     * @return List of orders with the specified status
     */
    List<Order> findByStatus(String status);
    
    /**
     * Find all orders with case-insensitive status matching
     * @param status The order status
     * @return List of orders with the specified status
     */
    @Query("SELECT o FROM Order o WHERE LOWER(o.status) = LOWER(:status)")
    List<Order> findByStatusIgnoreCase(@Param("status") String status);
    
    /**
     * Find all orders sorted by creation date (newest first)
     * @return List of all orders ordered by creation date descending
     */
    List<Order> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find orders by employee ID
     * @param employeeId The employee ID
     * @return List of orders handled by the employee
     */
    List<Order> findByEmployee_Id(Long employeeId);
    
    /**
     * Find orders by customer and status
     * @param customer The customer entity
     * @param status The order status
     * @return List of orders matching customer and status
     */
    List<Order> findByCustomerAndStatus(Customer customer, String status);
    
    /**
     * Find orders created within a date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of orders created within the date range
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count orders by status
     * @param status The order status
     * @return Count of orders with the specified status
     */
    long countByStatus(String status);
}
