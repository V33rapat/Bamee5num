package com.restaurant.demo.service;

import com.restaurant.demo.dto.EmployeeLoginDto;
import com.restaurant.demo.model.Employee;
import com.restaurant.demo.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EmployeeAuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeAuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate employee with username and password
     * 
     * @param loginDto EmployeeLoginDto containing username and password
     * @return Optional<Employee> containing the employee if authentication successful
     * @throws RuntimeException if authentication fails
     */
    public Optional<Employee> authenticateEmployee(EmployeeLoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        // Find employee by username
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);

        // If not found, throw exception
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        Employee employee = employeeOpt.get();

        // Verify password using passwordEncoder
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Return Optional<Employee> if authentication successful
        return Optional.of(employee);
    }

    /**
     * Authenticate employee with username and password (alternative signature)
     * 
     * @param username Employee's username
     * @param password Employee's plain text password
     * @return Optional<Employee> containing the employee if authentication successful
     */
    public Optional<Employee> authenticateEmployee(String username, String password) {
        EmployeeLoginDto loginDto = new EmployeeLoginDto(username, password);
        return authenticateEmployee(loginDto);
    }

    /**
     * Check if employee exists by username
     * 
     * @param username The username to check
     * @return true if employee exists, false otherwise
     */
    public boolean employeeExists(String username) {
        return employeeRepository.existsByUsername(username);
    }

    /**
     * Get employee by username
     * 
     * @param username The username to search for
     * @return Optional<Employee> containing the employee if found
     */
    public Optional<Employee> getEmployeeByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }
}
