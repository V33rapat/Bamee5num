package com.restaurant.demo.service.employee;

import com.restaurant.demo.model.Employee;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryEmployeeDirectory implements EmployeeDirectory {

    private final Map<Integer, Employee> employees = new ConcurrentHashMap<>();
    private final AtomicInteger sequence;

    public InMemoryEmployeeDirectory() {
        Employee employeeOne = new Employee(1, "Employee One", "Chef");
        Employee employeeTwo = new Employee(2, "Employee Two", "Cashier");
        employees.put(employeeOne.getId(), employeeOne);
        employees.put(employeeTwo.getId(), employeeTwo);
        int maxId = employees.keySet().stream().max(Integer::compareTo).orElse(0);
        this.sequence = new AtomicInteger(maxId);
    }

    @Override
    public List<Employee> findAll() {
        return employees.values().stream()
                .sorted(Comparator.comparingInt(Employee::getId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public Employee save(Employee employee) {
        employees.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public boolean deleteById(int id) {
        return employees.remove(id) != null;
    }

    @Override
    public int nextIdentity() {
        return sequence.incrementAndGet();
    }
}
