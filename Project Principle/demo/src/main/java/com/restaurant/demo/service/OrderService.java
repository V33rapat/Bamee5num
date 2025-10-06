package com.restaurant.demo.service;

import com.restaurant.demo.dto.OrderResponseDto;
import com.restaurant.demo.dto.OrderStatusUpdateDto;
import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.model.OrderItem;
import com.restaurant.demo.repository.CartItemRepository;
import com.restaurant.demo.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.restaurant.demo.model.Order;
import com.restaurant.demo.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    public OrderService(CartItemRepository cartItemRepository, CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
         this.orderRepository = orderRepository;
    }

    /**
     * Place an order by converting cart items to pending orders
     * 
     * @param customerId The ID of the customer placing the order
     * @return OrderResponseDto containing the placed order details
     * @throws RuntimeException if customer not found or cart is empty
     */
    public OrderResponseDto placeOrder(Long customerId) {
    // หา customer
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

    // ดึง cart items
    List<CartItem> cartItems = cartItemRepository.findByCustomerAndStatus(customer, "Cart");

    if (cartItems.isEmpty()) {
        throw new RuntimeException("Cart is empty. Cannot place order.");
    }

    // สร้าง Order ใหม่
    Order order = new Order();
    order.setCustomer(customer);
    order.setStatus("Pending");
    order.setCreatedAt(LocalDateTime.now());
    order.setUpdatedAt(LocalDateTime.now());

    // สร้าง OrderItem จาก CartItem
    List<OrderItem> orderItems = cartItems.stream().map(cart -> {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setItemName(cart.getItemName());
        item.setTotalPrice(cart.getItemPrice());
        item.setQuantity(cart.getQuantity());
        item.setTotalPrice(cart.getTotalPrice());
        return item;
    }).collect(Collectors.toList());

    order.setOrderItems(orderItems);

    // บันทึก Order และ OrderItem
    orderRepository.save(order); // cascade save OrderItem

    // ล้างหรืออัพเดต status ของ cart
    cartItems.forEach(c -> c.setStatus("Ordered"));
    cartItemRepository.saveAll(cartItems);

    // แปลงเป็น DTO
    BigDecimal totalPrice = orderItems.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    List<OrderResponseDto.OrderItemDto> dtoItems = orderItems.stream()
            .map(i -> new OrderResponseDto.OrderItemDto(
                        i.getId(),
                        i.getItemName(),
                        BigDecimal.valueOf(i.getItemPrice()), // แปลงเป็น BigDecimal
                        i.getQuantity(),
                        i.getTotalPrice()
            ))
            .collect(Collectors.toList());

    return new OrderResponseDto(
            customer.getId(),
            customer.getName(),
            dtoItems,
            totalPrice,
            order.getStatus(),
            order.getCreatedAt(),
            order.getUpdatedAt()
    );
}


    /**
     * Get pending orders for a specific customer
     * 
     * @param customerId The ID of the customer
     * @return OrderResponseDto containing pending order details
     * @throws RuntimeException if customer not found
     */
    public OrderResponseDto getPendingOrdersByCustomerId(Long customerId) {
        // Find customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        // Get pending cart items
        List<CartItem> pendingItems = cartItemRepository.findByCustomerAndStatus(customer, "Pending");

        if (pendingItems.isEmpty()) {
            // Return empty order response
            return new OrderResponseDto(
                    customer.getId(),
                    customer.getName(),
                    List.of(),
                    BigDecimal.ZERO,
                    "Pending",
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        }

        // Calculate total price
        BigDecimal totalPrice = pendingItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Convert to OrderItemDto
        List<OrderResponseDto.OrderItemDto> orderItems = pendingItems.stream()
                .map(item -> new OrderResponseDto.OrderItemDto(
                        item.getId(),
                        item.getItemName(),
                        item.getItemPrice(),
                        item.getQuantity(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        LocalDateTime createdAt = pendingItems.stream()
                .map(CartItem::getCreatedAt)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime updatedAt = pendingItems.stream()
                .map(CartItem::getUpdatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        return new OrderResponseDto(
                customer.getId(),
                customer.getName(),
                orderItems,
                totalPrice,
                "Pending",
                createdAt,
                updatedAt
        );
    }

    /**
     * Get all orders filtered by status (for employee view)
     * 
     * @param status The status to filter by (Pending, In Progress, Cancelled, Finish)
     * @return List of OrderResponseDto grouped by customer
     */
    public List<OrderResponseDto> getAllOrdersByStatus(String status) {
        // Validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        // Get all cart items with the specified status
        List<CartItem> items = cartItemRepository.findByStatus(status);

        // Group by customer
        Map<Customer, List<CartItem>> itemsByCustomer = items.stream()
                .collect(Collectors.groupingBy(CartItem::getCustomer));

        // Convert to OrderResponseDto list
        return itemsByCustomer.entrySet().stream()
                .map(entry -> {
                    Customer customer = entry.getKey();
                    List<CartItem> customerItems = entry.getValue();

                    BigDecimal totalPrice = customerItems.stream()
                            .map(CartItem::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    List<OrderResponseDto.OrderItemDto> orderItems = customerItems.stream()
                            .map(item -> new OrderResponseDto.OrderItemDto(
                                    item.getId(),
                                    item.getItemName(),
                                    item.getItemPrice(),
                                    item.getQuantity(),
                                    item.getTotalPrice()
                            ))
                            .collect(Collectors.toList());

                    LocalDateTime createdAt = customerItems.stream()
                            .map(CartItem::getCreatedAt)
                            .min(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.now());

                    LocalDateTime updatedAt = customerItems.stream()
                            .map(CartItem::getUpdatedAt)
                            .max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.now());

                    return new OrderResponseDto(
                            customer.getId(),
                            customer.getName(),
                            orderItems,
                            totalPrice,
                            status,
                            createdAt,
                            updatedAt
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Update order status with validation
     * 
     * @param updateDto OrderStatusUpdateDto containing customerId and new status
     * @return OrderResponseDto with updated order
     * @throws RuntimeException if customer not found or validation fails
     */
    public OrderResponseDto updateOrderStatus(OrderStatusUpdateDto updateDto) {
        Long customerId = updateDto.getCustomerId();
        String newStatus = updateDto.getNewStatus();

        // Find customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        // Get all cart items for this customer (we'll update all items to the same status)
        List<CartItem> customerItems = cartItemRepository.findByCustomer(customer);

        if (customerItems.isEmpty()) {
            throw new RuntimeException("No items found for customer ID: " + customerId);
        }

        // Get current status (assuming all items have the same status)
        String currentStatus = customerItems.get(0).getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }

        // Update all items to new status
        customerItems.forEach(item -> item.setStatus(newStatus));
        cartItemRepository.saveAll(customerItems);

        // Calculate total price
        BigDecimal totalPrice = customerItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Convert to OrderResponseDto
        List<OrderResponseDto.OrderItemDto> orderItems = customerItems.stream()
                .map(item -> new OrderResponseDto.OrderItemDto(
                        item.getId(),
                        item.getItemName(),
                        item.getItemPrice(),
                        item.getQuantity(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        LocalDateTime createdAt = customerItems.stream()
                .map(CartItem::getCreatedAt)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime updatedAt = customerItems.stream()
                .map(CartItem::getUpdatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        return new OrderResponseDto(
                customer.getId(),
                customer.getName(),
                orderItems,
                totalPrice,
                newStatus,
                createdAt,
                updatedAt
        );
    }

    /**
     * Get count of orders by status (for notification polling)
     * 
     * @param status The status to count
     * @return Count of orders with the specified status (grouped by customer)
     */
    public Long getOrderCountByStatus(String status) {
        // Validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        // Get all cart items with the specified status
        List<CartItem> items = cartItemRepository.findByStatus(status);

        // Count unique customers (each customer represents one order)
        return items.stream()
                .map(CartItem::getCustomer)
                .map(Customer::getId)
                .distinct()
                .count();
    }

    /**
     * Validate if the status is valid
     * 
     * @param status The status to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidStatus(String status) {
        return status != null && 
               (status.equals("Pending") || status.equals("In Progress") || 
                status.equals("Cancelled") || status.equals("Finish"));
    }

    /**
     * Validate status transition rules
     * Status transitions: Pending → In Progress → Finish
     * Any status can transition to Cancelled
     * 
     * @param currentStatus The current status
     * @param newStatus The new status
     * @return true if transition is valid, false otherwise
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        // Any status can transition to Cancelled
        if (newStatus.equals("Cancelled")) {
            return true;
        }

        // Status transition rules
        switch (currentStatus) {
            case "Pending":
                return newStatus.equals("In Progress") || newStatus.equals("Cancelled");
            case "In Progress":
                return newStatus.equals("Finish") || newStatus.equals("Cancelled");
            case "Finish":
            case "Cancelled":
                // Cannot change from Finish or Cancelled to other statuses
                return false;
            default:
                return false;
        }
    }
}
