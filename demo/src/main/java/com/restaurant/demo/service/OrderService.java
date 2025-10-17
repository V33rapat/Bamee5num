package com.restaurant.demo.service;

import com.restaurant.demo.dto.OrderResponseDto;
import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.model.OrderItem;
import com.restaurant.demo.model.Order;
import com.restaurant.demo.model.OrderStatus;
import com.restaurant.demo.repository.CartItemRepository;
import com.restaurant.demo.repository.CustomerRepository;
import com.restaurant.demo.repository.EmployeeRepository;
import com.restaurant.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

        private final CartItemRepository cartItemRepository;
        private final CustomerRepository customerRepository;
        private final OrderRepository orderRepository;
        private final EmployeeRepository employeeRepository;

        public OrderService(CartItemRepository cartItemRepository,
                        CustomerRepository customerRepository,
                        OrderRepository orderRepository,
                        EmployeeRepository employeeRepository) {
                this.cartItemRepository = cartItemRepository;
                this.customerRepository = customerRepository;
                this.orderRepository = orderRepository;
                this.employeeRepository = employeeRepository;
        }

        @Transactional
        public OrderResponseDto placeOrder(Long customerId, Long employeeId) {
                LocalDateTime now = LocalDateTime.now();

                // Fetch customer
                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new RuntimeException("Customer not found"));

                // Fetch employee (optional)
                Employee employee = null;
                if (employeeId != null) {
                        employee = employeeRepository.findById(employeeId)
                                        .orElseThrow(() -> new RuntimeException("Employee not found"));
                }

                // Get cart items (only those still in cart, not ordered)
                List<CartItem> cartItems = cartItemRepository.findByCustomerAndStatus(customer, "Pending");
                if (cartItems.isEmpty())
                        throw new RuntimeException("Cart is empty");

                // Create new Order entity
                Order order = new Order();
                order.setCustomer(customer);
                order.setEmployee(employee);
                order.setStatus(OrderStatus.PENDING.getValue());
                order.setCreatedAt(now);
                order.setUpdatedAt(now);

                BigDecimal totalAmount = BigDecimal.ZERO;

                // Convert CartItem → OrderItem
                for (CartItem ci : cartItems) {
                        OrderItem oi = new OrderItem();

                        // Set values with null protection
                        BigDecimal price = ci.getItemPrice() != null ? ci.getItemPrice() : BigDecimal.ZERO;
                        int qty = ci.getQuantity() != null ? ci.getQuantity() : 1;

                        oi.setItemName(ci.getItemName());
                        oi.setItemPrice(price);
                        oi.setQuantity(qty);
                        oi.setCreatedAt(now);
                        oi.setUpdatedAt(now);

                        // Calculate item total
                        BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(qty));

                        // Link to order
                        oi.setOrder(order);
                        order.getOrderItems().add(oi);

                        // Add to total
                        totalAmount = totalAmount.add(itemTotal);
                }

                // Set total amount
                order.setTotalAmount(totalAmount);
                order.setUpdatedAt(now);

                // Save Order and OrderItems (Cascade)
                order = orderRepository.save(order);

                // 🔥 CRITICAL FIX: Clear cart after successful order placement
                // Delete cart items to prevent accumulation
                cartItemRepository.deleteAll(cartItems);

                // Map to DTO for response
                List<OrderResponseDto.OrderItemDto> dtoItems = order.getOrderItems().stream()
                                .map(i -> new OrderResponseDto.OrderItemDto(
                                                i.getId(),
                                                i.getItemName(),
                                                i.getItemPrice(),
                                                i.getQuantity(),
                                                i.getTotal()))
                                .toList();

                return new OrderResponseDto(
                                order.getId(),           // orderId
                                customer.getId(),        // customerId
                                customer.getName(),      // customerName
                                dtoItems,                // items
                                order.getTotalAmount(),  // totalPrice
                                order.getStatus(),       // status
                                order.getCreatedAt(),    // createdAt
                                order.getUpdatedAt());   // updatedAt
        }

        /**
         * Get all orders for a specific customer (all statuses)
         * 
         * @param customerId The ID of the customer
         * @return List of OrderResponseDto containing customer's orders
         * @throws RuntimeException if customer not found
         */
        public List<OrderResponseDto> getOrdersByCustomerId(Long customerId) {
                // Validate customer exists
                customerRepository.findById(customerId)
                                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

                List<Order> orders = orderRepository.findByCustomer_Id(customerId);

                return orders.stream()
                                .map(this::mapOrderToDto)
                                .collect(Collectors.toList());
        }

        /**
         * Get pending orders for a specific customer
         * 
         * @param customerId The ID of the customer
         * @return List of OrderResponseDto containing pending orders
         * @throws RuntimeException if customer not found
         */
        public List<OrderResponseDto> getPendingOrdersByCustomerId(Long customerId) {
                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

                List<Order> pendingOrders = orderRepository.findByCustomerAndStatus(customer, OrderStatus.PENDING.getValue());

                return pendingOrders.stream()
                                .map(this::mapOrderToDto)
                                .collect(Collectors.toList());
        }

        /**
         * Get all orders filtered by status (for employee view)
         * 
         * @param status The status to filter by (Pending, In Progress, Finish, Cancelled)
         * @return List of OrderResponseDto
         */
        public List<OrderResponseDto> getAllOrdersByStatus(String status) {
                if (!OrderStatus.isValid(status)) {
                        throw new IllegalArgumentException("Invalid status: " + status);
                }

                List<Order> orders = orderRepository.findByStatusIgnoreCase(status);

                return orders.stream()
                                .map(this::mapOrderToDto)
                                .collect(Collectors.toList());
        }

        /**
         * Get order by ID
         * 
         * @param orderId The order ID
         * @return OrderResponseDto containing order details
         * @throws RuntimeException if order not found
         */
        public OrderResponseDto getOrderById(Long orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

                return mapOrderToDto(order);
        }

        /**
         * Update order status with validation
         * 
         * @param orderId The order ID
         * @param newStatus The new status
         * @return OrderResponseDto with updated order
         * @throws RuntimeException if order not found or validation fails
         */
        public OrderResponseDto updateOrderStatus(Long orderId, String newStatus) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

                String currentStatus = order.getStatus();

                if (!OrderStatus.isValidTransition(currentStatus, newStatus)) {
                        throw new IllegalArgumentException(
                                        String.format("Invalid status transition from %s to %s", currentStatus,
                                                        newStatus));
                }

                order.setStatus(newStatus);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);

                return mapOrderToDto(order);
        }

        /**
         * Get count of orders by status (for notification polling)
         * 
         * @param status The status to count
         * @return Count of orders with the specified status
         */
        public Long getOrderCountByStatus(String status) {
                if (!OrderStatus.isValid(status)) {
                        throw new IllegalArgumentException("Invalid status: " + status);
                }

                return orderRepository.countByStatus(status);
        }

        /**
         * Helper method to map Order entity to OrderResponseDto
         * 
         * @param order The Order entity
         * @return OrderResponseDto
         */
        private OrderResponseDto mapOrderToDto(Order order) {
                List<OrderResponseDto.OrderItemDto> orderItems = order.getOrderItems().stream()
                                .map(item -> new OrderResponseDto.OrderItemDto(
                                                item.getId(),
                                                item.getItemName(),
                                                item.getItemPrice(),
                                                item.getQuantity(),
                                                item.getTotal()))
                                .collect(Collectors.toList());

                return new OrderResponseDto(
                                order.getId(),                    // orderId
                                order.getCustomer().getId(),      // customerId
                                order.getCustomer().getName(),    // customerName
                                orderItems,                       // items
                                order.getTotalAmount(),           // totalPrice
                                order.getStatus(),                // status
                                order.getCreatedAt(),             // createdAt
                                order.getUpdatedAt());            // updatedAt
        }
}
