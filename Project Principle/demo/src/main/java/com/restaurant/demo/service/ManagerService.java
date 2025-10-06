package com.restaurant.demo.service;

import com.restaurant.demo.dto.EmployeeRegistrationDto;
import com.restaurant.demo.dto.ManagerRegistrationDto;
import com.restaurant.demo.exception.InvalidManagerCredentialsException;
import com.restaurant.demo.exception.ManagerAlreadyExistsException;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.model.Manager;
import com.restaurant.demo.repository.EmployeeRepository;
import com.restaurant.demo.repository.ManagerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerService(ManagerRepository managerRepository, 
                         EmployeeRepository employeeRepository,
                         PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new manager with validation
     * 
     * @param dto ManagerRegistrationDto containing registration details
     * @return Manager the saved manager entity
     * @throws ManagerAlreadyExistsException if email or username already exists
     * @throws IllegalArgumentException if passwords don't match
     */
    public Manager registerManager(ManagerRegistrationDto dto) {
        // Check if email already exists
        if (managerRepository.existsByEmail(dto.getEmail())) {
            throw ManagerAlreadyExistsException.withEmail(dto.getEmail());
        }

        // Check if username already exists
        if (managerRepository.existsByUsername(dto.getUsername())) {
            throw ManagerAlreadyExistsException.withUsername(dto.getUsername());
        }

        // Validate password and confirmPassword match
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }

        // Create new Manager entity
        Manager manager = new Manager();
        
        // Set inherited Employee fields
        manager.setName(dto.getUsername()); // Using username as name
        manager.setPosition("Manager");
        
        // Set Manager-specific fields
        manager.setUsername(dto.getUsername());
        manager.setEmail(dto.getEmail());
        
        // Hash password using passwordEncoder
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        manager.setPassword(hashedPassword);

        // Save manager to database
        Manager savedManager = managerRepository.save(manager);

        return savedManager;
    }

    /**
     * Authenticate manager with email and password
     * 
     * @param email Manager's email
     * @param password Manager's plain text password
     * @return Optional<Manager> containing the manager if authentication successful
     * @throws InvalidManagerCredentialsException if credentials are invalid
     */
    public Optional<Manager> authenticateManager(String email, String password) {
        // Find manager by email
        Optional<Manager> managerOpt = managerRepository.findByEmail(email);

        // If not found, return empty Optional
        if (managerOpt.isEmpty()) {
            throw InvalidManagerCredentialsException.forLogin();
        }

        Manager manager = managerOpt.get();

        // Verify password using passwordEncoder
        if (!passwordEncoder.matches(password, manager.getPassword())) {
            throw InvalidManagerCredentialsException.forLogin();
        }

        // Return Optional<Manager> if authentication successful
        return Optional.of(manager);
    }

    /**
     * Get manager by email
     * 
     * @param email Manager's email
     * @return Optional<Manager> containing the manager if found
     */
    public Optional<Manager> getManagerByEmail(String email) {
        return managerRepository.findByEmail(email);
    }

    /**
     * Get manager by ID
     * 
     * @param id Manager's ID
     * @return Optional<Manager> containing the manager if found
     */
    public Optional<Manager> getManagerById(Long id) {
        return managerRepository.findById(id);
    }

    /**
     * Register a new employee (by manager)
     * 
     * @param dto EmployeeRegistrationDto containing employee registration details
     * @return Employee the saved employee entity
     * @throws RuntimeException if username already exists
     * @throws IllegalArgumentException if validation fails
     */
    public Employee registerEmployee(EmployeeRegistrationDto dto) {
        // Check if username already exists
        if (employeeRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists: " + dto.getUsername());
        }

        // Validate position (additional validation beyond DTO validation)
        String position = dto.getPosition();
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position is required");
        }

        // Create new Employee entity
        Employee employee = new Employee();
        
        // Set Employee fields
        employee.setName(dto.getName());
        employee.setPosition(dto.getPosition());
        employee.setUsername(dto.getUsername());
        
        // Hash password using passwordEncoder
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        employee.setPassword(hashedPassword);

        // Save employee to database
        Employee savedEmployee = employeeRepository.save(employee);

        return savedEmployee;
    }

    /**
     * Get all employees
     * 
     * @return List of all employees
     */
    public java.util.List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Get employee by ID
     * 
     * @param id Employee's ID
     * @return Optional<Employee> containing the employee if found
     */
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    /**
     * Delete employee by ID
     * 
     * @param id Employee's ID
     * @return true if employee was deleted, false if not found
     */
    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
