package com.restaurant.demo.service.employee;

import com.restaurant.demo.model.Employee;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
//@Profile("test") // Only use this in-memory implementation for testing, not in production
public class InMemoryEmployeeDirectory implements EmployeeDirectory {

    private final Map<Long, Employee> employees = new ConcurrentHashMap<>();
    private final AtomicInteger sequence;

    public InMemoryEmployeeDirectory() {
        Employee employeeOne = new Employee(1L, "Employee One", "Chef");
        Employee employeeTwo = new Employee(2L, "Employee Two", "Cashier");
        employees.put(employeeOne.getId(), employeeOne);
        employees.put(employeeTwo.getId(), employeeTwo);
        long maxId = employees.keySet().stream().max(Long::compareTo).orElse(0L);
        this.sequence = new AtomicInteger((int) maxId);
    }

    @Override
    public List<Employee> findAll() {
        return employees.values().stream()
                .sorted(Comparator.comparingLong(Employee::getId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(employees.get((long) id));
    }

    @Override
    public Employee save(Employee employee) {
        employees.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public boolean deleteById(int id) {
        return employees.remove((long) id) != null;
    }

    @Override
    public int nextIdentity() {
        return sequence.incrementAndGet();
    }
}
