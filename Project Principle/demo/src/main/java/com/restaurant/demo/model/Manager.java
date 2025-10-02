package com.restaurant.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "managers")
@PrimaryKeyJoinColumn(name = "id")
public class Manager extends Employee {
    public Manager() {
        super();
    }

    public Manager(int id, String name) {
        super(id, name, "Manager");
    }

    public void addItem(MenuItem item, List<MenuItem> menu) {
        menu.add(item);
    }

    public void removeItem(MenuItem item, List<MenuItem> menu) {
        menu.remove(item);
    }

    public List<User> manageEmployees(List<User> users) {
        List<User> result = new java.util.ArrayList<>();
        for (User user : users) {
            if ("employee".equalsIgnoreCase(user.getRole()) || "manager".equalsIgnoreCase(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }
    
    public SalesReport viewSalesReport(List<CartItem> cartItems, List<User> users) {
        LocalDate today = LocalDate.now();

        Map<Long, List<CartItem>> todayCartByCustomer = cartItems.stream()
                .filter(item -> item.getAddedAt() != null && today.equals(item.getAddedAt().toLocalDate()))
                .collect(Collectors.groupingBy(CartItem::getCustomerId));

        int orderCount = todayCartByCustomer.size();
        double revenue = todayCartByCustomer.values().stream()
                .flatMap(List::stream)
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        int newCustomers = 0;
        for (User user : users) {
            if ("customer".equalsIgnoreCase(user.getRole()) && user.getCreatedAt() != null) {
                if (user.getCreatedAt().startsWith(today.toString())) {
                    newCustomers++;
                }
            }
        }

        double avgRating = 0;
        return new SalesReport(orderCount, revenue, newCustomers, avgRating);
    }
    
    // helper class -> sales report data
    public static class SalesReport {
        public final int orderCount;
        public final double revenue;
        public final int newCustomers;
        public final double avgRating;

        public SalesReport(int orderCount, double revenue, int newCustomers, double avgRating) {
            this.orderCount = orderCount;
            this.revenue = revenue;
            this.newCustomers = newCustomers;
            this.avgRating = avgRating;
        }
    }
}