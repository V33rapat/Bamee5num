package com.restaurant.demo.service.employee;

import com.restaurant.demo.model.Employee;
import com.restaurant.demo.model.User;
import com.restaurant.demo.service.employee.dto.EmployeeCredentials;
import com.restaurant.demo.service.employee.dto.EmployeeRegistrationRequest;
import com.restaurant.demo.service.employee.dto.EmployeeRegistrationResult;
import com.restaurant.demo.service.employee.dto.EmployeeUpdateRequest;
import com.restaurant.demo.service.user.UserDirectory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class EmployeeService {

    // จัดการพนักงานและบัญชีผู้ใช้ที่เกี่ยวข้อง
    private final EmployeeDirectory employeeDirectory;
    private final UserDirectory userDirectory;
    private final LoginCodeGenerator loginCodeGenerator;

    public EmployeeService(EmployeeDirectory employeeDirectory,
                           UserDirectory userDirectory,
                           LoginCodeGenerator loginCodeGenerator) {
        this.employeeDirectory = employeeDirectory;
        this.userDirectory = userDirectory;
        this.loginCodeGenerator = loginCodeGenerator;
    }

    public List<Employee> getEmployees() {
        return employeeDirectory.findAll();
    }
    
    // ลงทะเบียนพนักงานใหม่ พร้อมสร้างบัญชีผู้ใช้ที่เกี่ยวข้อง
    public EmployeeRegistrationResult registerEmployee(EmployeeRegistrationRequest request) {
        String name = requireNonBlank(request.getName(), "name");
        String position = Optional.ofNullable(request.getPosition()).orElse("");
        String username = resolveUsername(request.getUsername(), name);
        ensureUsernameAvailable(username);

        int employeeId = employeeDirectory.nextIdentity();
        String loginCode = loginCodeGenerator.generate();

        Employee employee = new Employee((long) employeeId, name, position);
        employeeDirectory.save(employee);

        User account = new User(employeeId, username, name, name, "employee", Instant.now().toString(), loginCode);
        userDirectory.save(account);

        EmployeeCredentials credentials = new EmployeeCredentials(username, loginCode);
        return new EmployeeRegistrationResult(employee, credentials);
    }

    // อัปเดตข้อมูลพนักงานและบัญชีผู้ใช้ที่เกี่ยวข้อง
    public Optional<Employee> updateEmployee(int id, EmployeeUpdateRequest request) {
        return employeeDirectory.findById(id).map(existing -> {
            String newName = Optional.ofNullable(request.getName())
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .orElse(existing.getName());
            String newPosition = Optional.ofNullable(request.getPosition()).orElse(existing.getPosition());

            existing.setName(newName);
            existing.setPosition(newPosition);
            employeeDirectory.save(existing);

            userDirectory.findById(id).ifPresent(user -> {
                user.setFullName(newName);
                user.setName(newName);
                userDirectory.save(user);
            });

            return existing;
        });
    }

    // ลบพนักงานและบัญชีผู้ใช้ที่เกี่ยวข้อง
    public boolean deleteEmployee(int id) {
        boolean removed = employeeDirectory.deleteById(id);
        if (removed) {
            userDirectory.deleteById(id);
        }
        return removed;
    }

    // ดึงข้อมูลการเข้าสู่ระบบของพนักงาน
    public Optional<EmployeeCredentials> getCredentialsFor(int employeeId) {
        return userDirectory.findById(employeeId)
                .filter(user -> "employee".equalsIgnoreCase(user.getRole()))
                .map(user -> new EmployeeCredentials(user.getUsername(), user.getLoginCode()));
    }
    
    // ตรวจสอบว่าค่าที่ระบุไม่เป็นค่าว่าง
    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee " + fieldName + " is required");
        }
        return value.trim();
    }

    // ตรวจสอบว่าชื่อผู้ใช้ไม่ซ้ำกัน
    private void ensureUsernameAvailable(String username) {
        userDirectory.findByUsername(username).ifPresent(existing -> {
            throw new IllegalArgumentException("Username already exists: " + username);
        });
    }

    // สร้างชื่อผู้ใช้ที่ไม่ซ้ำกัน
    private String resolveUsername(String requestedUsername, String name) {
        String base = (requestedUsername != null && !requestedUsername.trim().isEmpty())
                ? requestedUsername.trim()
                : slugify(name);

        String candidate = base;
        int suffix = 1;
        while (userDirectory.findByUsername(candidate).isPresent()) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }

    // ทำให้เป็นรูปแบบ slug (ตัวพิมพ์เล็ก ไม่มีช่องว่างหรืออักขระพิเศษ)
    private String slugify(String value) {
        String normalized = value.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9]", "");
        if (normalized.isEmpty()) {
            normalized = "employee";
        }
        return normalized;
    }
}
