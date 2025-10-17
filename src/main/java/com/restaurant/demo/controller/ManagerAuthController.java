package com.restaurant.demo.controller;

import com.restaurant.demo.dto.ManagerLoginDto;
import com.restaurant.demo.dto.ManagerRegistrationDto;
import com.restaurant.demo.exception.InvalidManagerCredentialsException;
import com.restaurant.demo.exception.ManagerAlreadyExistsException;
import com.restaurant.demo.model.Manager;
import com.restaurant.demo.service.ManagerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ManagerAuthController {

    private static final Logger logger = LoggerFactory.getLogger(ManagerAuthController.class);

    private final ManagerService managerService;

    public ManagerAuthController(ManagerService managerService) {
        this.managerService = managerService;
    }

    /**
     * Show manager registration form
     * 
     * @param model Model to add attributes
     * @return View name for manager registration page
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("managerRegistrationDto", new ManagerRegistrationDto());
        return "manager-register";
    }

    /**
     * Process manager registration
     * 
     * @param registrationDto DTO containing registration data
     * @param bindingResult Validation results
     * @param model Model to add attributes
     * @param redirectAttributes Attributes for redirect
     * @return View name or redirect path
     */
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("managerRegistrationDto") ManagerRegistrationDto registrationDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Processing manager registration for email: {}", registrationDto.getEmail());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors during registration: {}", bindingResult.getAllErrors());
            return "manager-register";
        }

        // Check if passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            model.addAttribute("error", "Password and confirm password do not match");
            logger.warn("Password mismatch for user: {}", registrationDto.getUsername());
            return "manager-register";
        }

        try {
            // Call managerService.registerManager()
            Manager registeredManager = managerService.registerManager(registrationDto);
            logger.info("Manager registered successfully: {} (ID: {})", 
                registeredManager.getUsername(), registeredManager.getId());

            // On success, redirect to login with success message
            redirectAttributes.addFlashAttribute("message", 
                "Registration successful! Please login with your credentials.");
            return "redirect:/manager/login";

        } catch (ManagerAlreadyExistsException e) {
            // Catch ManagerAlreadyExistsException and add error to model
            model.addAttribute("error", e.getMessage());
            logger.warn("Registration failed - manager already exists: {}", e.getMessage());
            return "manager-register";

        } catch (IllegalArgumentException e) {
            // Catch other validation errors
            model.addAttribute("error", e.getMessage());
            logger.warn("Registration failed - validation error: {}", e.getMessage());
            return "manager-register";

        } catch (Exception e) {
            // Catch unexpected errors
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            logger.error("Unexpected error during manager registration", e);
            return "manager-register";
        }
    }

    /**
     * Show manager login form
     * 
     * @param model Model to add attributes
     * @return View name for manager login page
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("managerLoginDto", new ManagerLoginDto());
        return "manager-login";
    }

    /**
     * Process manager login
     * 
     * @param loginDto DTO containing login credentials
     * @param bindingResult Validation results
     * @param session HTTP session
     * @param model Model to add attributes
     * @return View name or redirect path
     */
    @PostMapping("/login")
    public String processLogin(
            @Valid @ModelAttribute("managerLoginDto") ManagerLoginDto loginDto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        logger.info("Processing manager login for email: {}, sessionId: {}", 
            loginDto.getEmail(), session.getId());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors during login: {}", bindingResult.getAllErrors());
            return "manager-login";
        }

        try {
            // Call managerService.authenticateManager()
            Optional<Manager> managerOpt = managerService.authenticateManager(
                loginDto.getEmail(), 
                loginDto.getPassword()
            );

            if (managerOpt.isEmpty()) {
                // If authentication fails, add error message and return to login form
                model.addAttribute("error", "Invalid email or password");
                logger.warn("Login failed - invalid credentials for email: {}", loginDto.getEmail());
                return "manager-login";
            }

            Manager manager = managerOpt.get();

            // If successful, store manager data in session
            session.setAttribute("managerId", manager.getId());
            session.setAttribute("managerUsername", manager.getUsername());
            session.setAttribute("managerEmail", manager.getEmail());
            session.setAttribute("managerAuthenticated", true);

            logger.info("Login successful - managerId: {}, username: {}, sessionId: {}", 
                manager.getId(), manager.getUsername(), session.getId());

            // Redirect to manager dashboard
            return "redirect:/manager";

        } catch (InvalidManagerCredentialsException e) {
            // If authentication fails, add error message and return to login form
            model.addAttribute("error", e.getMessage());
            logger.warn("Login failed - invalid credentials: {}", e.getMessage());
            return "manager-login";

        } catch (Exception e) {
            // Catch unexpected errors
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            logger.error("Unexpected error during manager login", e);
            return "manager-login";
        }
    }

    /**
     * Logout manager and invalidate session (GET method for direct access)
     * 
     * @param session HTTP session
     * @return Redirect to login page
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("Manager logout - sessionId: {}", session.getId());
        
        // Invalidate session
        session.invalidate();
        
        // Redirect to login page
        return "redirect:/manager/login";
    }

    /**
     * Logout manager and invalidate session (POST method for form submission)
     * 
     * @param session HTTP session
     * @return Redirect to login page
     */
    @PostMapping("/logout")
    public String logoutPost(HttpSession session) {
        logger.info("Manager logout (POST) - sessionId: {}", session.getId());
        
        // Invalidate session
        session.invalidate();
        
        // Redirect to login page
        return "redirect:/manager/login";
    }
}
