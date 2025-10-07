package com.restaurant.demo.service;

import com.restaurant.demo.dto.OrderResponseDto;
import com.restaurant.demo.dto.OrderStatusUpdateDto;
import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.model.OrderItem;
import com.restaurant.demo.model.Order;
import com.restaurant.demo.repository.CartItemRepository;
import com.restaurant.demo.repository.CustomerRepository;
import com.restaurant.demo.repository.EmployeeRepository;
import com.restaurant.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

                // 🧩 ดึงข้อมูลลูกค้า
                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new RuntimeException("Customer not found"));

                // 🧩 ดึงพนักงาน (ถ้ามี)
                Employee employee = null;
                if (employeeId != null) {
                        employee = employeeRepository.findById(employeeId)
                                        .orElseThrow(() -> new RuntimeException("Employee not found"));
                }

                // 🧩 ดึง cart items ที่ยัง pending
                List<CartItem> cartItems = cartItemRepository.findByCustomerAndStatus(customer, "Pending");
                if (cartItems.isEmpty())
                        throw new RuntimeException("Cart is empty");

                // 🧩 สร้าง Order ใหม่
                Order order = new Order();
                order.setCustomer(customer);
                order.setEmployee(employee);
                order.setStatus("Pending");
                order.setCreatedAt(now);
                order.setUpdatedAt(now);
                // 🛑 ลบ order.setTotalAmount(BigDecimal.ZERO); ออก หรือปล่อยไว้ก็ได้
                // 🛑 ลบ order = orderRepository.save(order); บรรทัดนี้ออก!

                BigDecimal totalAmount = BigDecimal.ZERO;

                // 🧩 แปลง CartItem → OrderItem
                for (CartItem ci : cartItems) {
                        OrderItem oi = new OrderItem();

                        // 1. กำหนดค่า Price & Quantity (พร้อมป้องกัน NULL)
                        BigDecimal price = ci.getItemPrice() != null ? ci.getItemPrice() : BigDecimal.ZERO;
                        int qty = ci.getQuantity() != null ? ci.getQuantity() : 1;

                        oi.setItemName(ci.getItemName());
                        oi.setItemPrice(price); // เรียก Setter ซึ่งเรียก calculateTotal()
                        oi.setQuantity(qty); // เรียก Setter ซึ่งเรียก calculateTotal()
                        oi.setCreatedAt(now);
                        oi.setUpdatedAt(now);

                        // 2. คำนวณ itemTotal ด้วยตัวเองเพื่อนำไปรวม
                        // (ใช้ตัวแปรที่คำนวณเองเพื่อความมั่นใจ)
                        BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(qty)); // คำนวณตรงๆ
                        // (ตอนนี้ oi.getTotal() ควรจะมีค่าเท่ากับ itemTotal แล้ว)

                        // 3. ผูก relation
                        oi.setOrder(order);
                        order.getOrderItems().add(oi);

                        // 4. รวมเข้า totalAmount
                        totalAmount = totalAmount.add(itemTotal); // 🛑 ตอนนี้ itemTotal ไม่ใช่ NULL แน่นอน

                        // 5. เปลี่ยนสถานะ cart item
                        ci.setStatus("Ordered");
                        ci.setUpdatedAt(now);
                }

                
                // 🧩 กำหนด TotalAmount ขั้นสุดท้าย
                order.setTotalAmount(totalAmount);
                order.setUpdatedAt(now);
                

                // 🧩 บันทึก Order และ OrderItems (Cascade) ในครั้งเดียว
                order = orderRepository.save(order); // ใช้งานเฉพาะการ Save ครั้งนี้

                // 🧩 อัปเดต cart items
                cartItemRepository.saveAll(cartItems);

                // 🧩 map เป็น DTO ส่งกลับ
                List<OrderResponseDto.OrderItemDto> dtoItems = order.getOrderItems().stream()
                                .map(i -> new OrderResponseDto.OrderItemDto(
                                                i.getId(),
                                                i.getItemName(),
                                                i.getItemPrice(),
                                                i.getQuantity(),
                                                i.getTotal()))
                                .toList();

                return new OrderResponseDto(
                                customer.getId(),
                                customer.getName(),
                                dtoItems,
                                order.getTotalAmount(),
                                order.getStatus(),
                                order.getCreatedAt(),
                                order.getUpdatedAt());
        }

        /**
         * Get pending orders for a specific customer
         * 
         * @param customerId The ID of the customer
         * @return OrderResponseDto containing pending order details
         * @throws RuntimeException if customer not found
         */
        public OrderResponseDto getPendingOrdersByCustomerId(Long customerId) {
                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

                List<CartItem> pendingItems = cartItemRepository.findByCustomerAndStatus(customer, "Pending");

                if (pendingItems.isEmpty()) {
                        return new OrderResponseDto(
                                        customer.getId(),
                                        customer.getName(),
                                        List.of(),
                                        BigDecimal.ZERO,
                                        "Pending",
                                        LocalDateTime.now(),
                                        LocalDateTime.now());
                }

                BigDecimal totalPrice = pendingItems.stream()
                                .map(CartItem::getTotalPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<OrderResponseDto.OrderItemDto> orderItems = pendingItems.stream()
                                .map(item -> new OrderResponseDto.OrderItemDto(
                                                item.getId(),
                                                item.getItemName(),
                                                item.getItemPrice(),
                                                item.getQuantity(),
                                                item.getTotalPrice()))
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
                                updatedAt);
        }

        /**
         * Get all orders filtered by status (for employee view)
         * 
         * @param status The status to filter by (Pending, In Progress, Cancelled,
         *               Finish)
         * @return List of OrderResponseDto grouped by customer
         */
        public List<OrderResponseDto> getAllOrdersByStatus(String status) {
                if (!isValidStatus(status)) {
                        throw new IllegalArgumentException("Invalid status: " + status);
                }

                List<CartItem> items = cartItemRepository.findByStatusIgnoreCase(status);

                Map<Customer, List<CartItem>> itemsByCustomer = items.stream()
                                .collect(Collectors.groupingBy(CartItem::getCustomer));

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
                                                                        item.getTotalPrice()))
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
                                                        updatedAt);
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
        public OrderResponseDto updateOrderStatus(Long orderId, String newStatus) {
                // ใช้ orderId จาก path variable
                Customer customer = customerRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + orderId));

                List<CartItem> customerItems = cartItemRepository.findByCustomer(customer);

                if (customerItems.isEmpty()) {
                        throw new RuntimeException("No items found for customer ID: " + orderId);
                }

                String currentStatus = customerItems.get(0).getStatus();

                if (!isValidStatusTransition(currentStatus, newStatus)) {
                        throw new IllegalArgumentException(
                                        String.format("Invalid status transition from %s to %s", currentStatus,
                                                        newStatus));
                }

                customerItems.forEach(item -> item.setStatus(newStatus));
                cartItemRepository.saveAll(customerItems);

                BigDecimal totalPrice = customerItems.stream()
                                .map(CartItem::getTotalPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<OrderResponseDto.OrderItemDto> orderItems = customerItems.stream()
                                .map(item -> new OrderResponseDto.OrderItemDto(
                                                item.getId(),
                                                item.getItemName(),
                                                item.getItemPrice(),
                                                item.getQuantity(),
                                                item.getTotalPrice()))
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
                                updatedAt);
        }

        /**
         * Get count of orders by status (for notification polling)
         * 
         * @param status The status to count
         * @return Count of orders with the specified status (grouped by customer)
         */
        public Long getOrderCountByStatus(String status) {
                if (!isValidStatus(status)) {
                        throw new IllegalArgumentException("Invalid status: " + status);
                }

                List<CartItem> items = cartItemRepository.findByStatus(status);

                return items.stream()
                                .map(CartItem::getCustomer)
                                .map(Customer::getId)
                                .distinct()
                                .count();
        }

        /**
         * Validate if the status is valid
         */
        private boolean isValidStatus(String status) {
                return status != null &&
        (status.equals("Pending") || status.equals("In Progress") ||
         status.equals("Cancelled") || status.equals("Finish") || 
         status.equals("Ordered")); // ✅ เพิ่ม "Ordered" เข้ามา
        }

        /**
         * Validate status transition rules
         * Status transitions: Pending → In Progress → Finish
         * Any status can transition to Cancelled
         */
        private boolean isValidStatusTransition(String currentStatus, String newStatus) {
                if (currentStatus == null || newStatus == null) {
                        return false;
                }

                if (newStatus.equals("Cancelled")) {
                        return true;
                }

                switch (currentStatus) {
                        case "Pending":
                                return newStatus.equals("In Progress") || newStatus.equals("Cancelled");
                        case "In Progress":
                                return newStatus.equals("Finish") || newStatus.equals("Cancelled");
                        case "Finish":
                        case "Cancelled":
                                return false;
                        default:
                                return false;
                }
        }
}
