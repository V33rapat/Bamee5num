package com.restaurant.demo.controller;

import com.restaurant.demo.dto.CustomerRegistrationDto;
import com.restaurant.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/employee")
    public String employee() {
        return "employee";
    }

    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }
}
