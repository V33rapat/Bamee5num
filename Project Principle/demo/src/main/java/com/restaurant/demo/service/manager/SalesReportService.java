package com.restaurant.demo.service.manager;

import com.restaurant.demo.model.Manager;
import com.restaurant.demo.model.Order;
import com.restaurant.demo.model.User;
import com.restaurant.demo.repository.OrderRepository;
import com.restaurant.demo.service.user.UserDirectory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalesReportService {

    private final OrderRepository orderRepository;
    private final UserDirectory userDirectory;
    private final ManagerContext managerContext;

    public SalesReportService(OrderRepository orderRepository,
                              UserDirectory userDirectory,
                              ManagerContext managerContext) {
        this.orderRepository = orderRepository;
        this.userDirectory = userDirectory;
        this.managerContext = managerContext;
    }

    public Manager.SalesReport getDailySalesReport() {
        // Get today's date range
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        // Get all orders created today
        List<Order> todayOrders = orderRepository.findByCreatedAtBetween(startOfDay, endOfDay);
        
        // Get all users for new customer calculation
        List<User> users = userDirectory.findAll();
        
        // Get current manager
        User managerUser = managerContext.getCurrentManager();
        Manager manager = managerUser != null ? new Manager((long) managerUser.getId(), managerUser.getFullName()) : new Manager();
        
        // Use the new method that accepts orders instead of cart items
        return manager.viewSalesReportFromOrders(todayOrders, users);
    }
}
