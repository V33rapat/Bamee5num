package com.restaurant.demo.controller;

import com.restaurant.demo.dto.CustomerRegistrationDto;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        try {
            // Validate password confirmation
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match");
                return "register";
            }

            // Create registration DTO
            CustomerRegistrationDto registrationDto = new CustomerRegistrationDto();
            registrationDto.setName(firstName + " " + lastName);
            registrationDto.setUsername(username);
            registrationDto.setEmail(email);
            registrationDto.setPhone(phone);
            registrationDto.setPassword(password);
            registrationDto.setConfirmPassword(confirmPassword);

            // Register the customer
            customerService.registerCustomer(registrationDto);

            // Redirect to login with success message
            return "redirect:/login?success=Registration successful! Please sign in.";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/customer-page")
    public String customer() {
        return "customer";
    }

    /**
     * Customer-specific page endpoint with session validation
     * Displays personalized customer page based on customer ID in URL
     * 
     * @param customerId The customer ID from URL path variable
     * @param model Model to pass data to Thymeleaf template
     * @param session HttpSession for server-side session validation
     * @return View name for Thymeleaf or redirect to login on error
     */
    @GetMapping("/customer/{customerId}")
    public String customerPage(@PathVariable Long customerId, Model model, HttpSession session) {
        // Session validation: Check if session contains customer ID
        Long sessionCustomerId = (Long) session.getAttribute("customerId");
        
        // If no session exists or customer ID doesn't match, redirect to login with error
        if (sessionCustomerId == null || !sessionCustomerId.equals(customerId)) {
            return "redirect:/login?error=unauthorized";
        }
        
        // Fetch customer data from database using customer service
        Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
        
        // Handle case when customer is not found in database
        if (customerOpt.isEmpty()) {
            return "redirect:/login?error=notfound";
        }
        
        // Add customer object to model for Thymeleaf template rendering
        model.addAttribute("customer", customerOpt.get());
        
        // Return "customer" view name for Thymeleaf rendering
        return "customer";
    }

    @GetMapping("/employee")
    public String employee() {
        return "employee";
    }

    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }
}
