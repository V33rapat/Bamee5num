package com.restaurant.demo.service;

import com.restaurant.demo.dto.AuthResponseDto;
import com.restaurant.demo.dto.CustomerLoginDto;
import com.restaurant.demo.dto.CustomerRegistrationDto;
import com.restaurant.demo.exception.CustomerAlreadyExistsException;
import com.restaurant.demo.exception.InvalidCredentialsException;
import com.restaurant.demo.model.Customer;
import com.restaurant.demo.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRegistrationDto validRegistrationDto;
    private CustomerLoginDto validLoginDto;
    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        validRegistrationDto = new CustomerRegistrationDto();
        validRegistrationDto.setUsername("testuser");
        validRegistrationDto.setEmail("test@example.com");
        validRegistrationDto.setPassword("Password123!");
        validRegistrationDto.setName("Test User");
        validRegistrationDto.setPhone("+1234567890");

        validLoginDto = new CustomerLoginDto();
        validLoginDto.setUsernameOrEmail("testuser");
        validLoginDto.setPassword("Password123!");

        mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setUsername("testuser");
        mockCustomer.setEmail("test@example.com");
        mockCustomer.setPasswordHash("encodedPassword");
        mockCustomer.setName("Test User");
        mockCustomer.setPhone("+1234567890");
        mockCustomer.setCreatedAt(LocalDateTime.now());
        mockCustomer.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void registerCustomer_WithValidData_ShouldReturnAuthResponse() {
        // Arrange
        when(customerRepository.existsByUsername(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(mockCustomer);

        // Act
        AuthResponseDto result = customerService.registerCustomer(validRegistrationDto);

        // Assert
        assertNotNull(result);
        assertEquals(mockCustomer.getId(), result.getCustomerId());
        assertEquals(mockCustomer.getUsername(), result.getUsername());
        assertEquals(mockCustomer.getEmail(), result.getEmail());
        assertTrue(result.getToken().startsWith("registration-token-"));
        assertNotNull(result.getLoginTime());

        verify(customerRepository).existsByUsername("testuser");
        verify(customerRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("Password123!");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void registerCustomer_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(customerRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        CustomerAlreadyExistsException exception = assertThrows(
            CustomerAlreadyExistsException.class,
            () -> customerService.registerCustomer(validRegistrationDto)
        );

        assertTrue(exception.getMessage().contains("testuser"));
        verify(customerRepository).existsByUsername("testuser");
        verify(customerRepository, never()).existsByEmail(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void registerCustomer_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(customerRepository.existsByUsername(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        CustomerAlreadyExistsException exception = assertThrows(
            CustomerAlreadyExistsException.class,
            () -> customerService.registerCustomer(validRegistrationDto)
        );

        assertTrue(exception.getMessage().contains("test@example.com"));
        verify(customerRepository).existsByUsername("testuser");
        verify(customerRepository).existsByEmail("test@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void loginCustomer_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        when(customerRepository.findByUsername(anyString())).thenReturn(Optional.of(mockCustomer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        AuthResponseDto result = customerService.loginCustomer(validLoginDto);

        // Assert
        assertNotNull(result);
        assertEquals(mockCustomer.getId(), result.getCustomerId());
        assertEquals(mockCustomer.getUsername(), result.getUsername());
        assertEquals(mockCustomer.getEmail(), result.getEmail());
        assertTrue(result.getToken().startsWith("login-token-"));
        assertNotNull(result.getLoginTime());

        verify(customerRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("Password123!", "encodedPassword");
    }

    @Test
    void loginCustomer_WithEmailInsteadOfUsername_ShouldReturnAuthResponse() {
        // Arrange
        validLoginDto.setUsernameOrEmail("test@example.com");
        when(customerRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(mockCustomer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        AuthResponseDto result = customerService.loginCustomer(validLoginDto);

        // Assert
        assertNotNull(result);
        assertEquals(mockCustomer.getId(), result.getCustomerId());
        verify(customerRepository).findByUsername("test@example.com");
        verify(customerRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("Password123!", "encodedPassword");
    }

    @Test
    void loginCustomer_WithInvalidUsername_ShouldThrowException() {
        // Arrange
        when(customerRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class,
            () -> customerService.loginCustomer(validLoginDto)
        );

        assertTrue(exception.getMessage().contains("Invalid credentials"));
        verify(customerRepository).findByUsername("testuser");
        verify(customerRepository).findByEmail("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void loginCustomer_WithInvalidPassword_ShouldThrowException() {
        // Arrange
        when(customerRepository.findByUsername(anyString())).thenReturn(Optional.of(mockCustomer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class,
            () -> customerService.loginCustomer(validLoginDto)
        );

        assertTrue(exception.getMessage().contains("Invalid credentials"));
        verify(customerRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("Password123!", "encodedPassword");
    }

    @Test
    void findCustomerById_WithValidId_ShouldReturnCustomer() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));

        // Act
        Optional<Customer> result = customerService.findCustomerById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockCustomer.getId(), result.get().getId());
        assertEquals(mockCustomer.getUsername(), result.get().getUsername());
        verify(customerRepository).findById(1L);
    }

    @Test
    void findCustomerById_WithInvalidId_ShouldReturnEmpty() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.findCustomerById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(customerRepository).findById(999L);
    }

    @Test
    void findCustomerByUsernameOrEmail_WithUsername_ShouldReturnCustomer() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(mockCustomer));

        // Act
        Optional<Customer> result = customerService.findCustomerByUsernameOrEmail("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockCustomer.getId(), result.get().getId());
        verify(customerRepository).findByUsername("testuser");
        verify(customerRepository, never()).findByEmail(anyString());
    }

    @Test
    void findCustomerByUsernameOrEmail_WithEmail_ShouldReturnCustomer() {
        // Arrange
        when(customerRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockCustomer));

        // Act
        Optional<Customer> result = customerService.findCustomerByUsernameOrEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockCustomer.getId(), result.get().getId());
        verify(customerRepository).findByUsername("test@example.com");
        verify(customerRepository).findByEmail("test@example.com");
    }

    @Test
    void findCustomerByUsernameOrEmail_WithInvalidCredentials_ShouldReturnEmpty() {
        // Arrange
        when(customerRepository.findByUsername("invalid")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("invalid")).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.findCustomerByUsernameOrEmail("invalid");

        // Assert
        assertFalse(result.isPresent());
        verify(customerRepository).findByUsername("invalid");
        verify(customerRepository).findByEmail("invalid");
    }

    @Test
    void isValidPassword_WithValidPassword_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(customerService.isValidPassword("Password123!"));
        assertTrue(customerService.isValidPassword("MySecure@Pass1"));
        assertTrue(customerService.isValidPassword("Complex$Password9"));
    }

    @Test
    void isValidPassword_WithInvalidPasswords_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(customerService.isValidPassword(null));
        assertFalse(customerService.isValidPassword(""));
        assertFalse(customerService.isValidPassword("short"));
        assertFalse(customerService.isValidPassword("password123!")); // no uppercase
        assertFalse(customerService.isValidPassword("PASSWORD123!")); // no lowercase
        assertFalse(customerService.isValidPassword("Password!")); // no digit
        assertFalse(customerService.isValidPassword("Password123")); // no special character
        assertFalse(customerService.isValidPassword("OnlyLetters")); // missing digit and special char
    }
}
