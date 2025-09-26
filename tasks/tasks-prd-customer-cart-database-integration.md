# Task List: Customer Cart Database Integration

Based on PRD: `prd-customer-cart-database-integration.md`

## Relevant Files

- `src/main/java/com/restaurant/demo/model/Customer.java` - New entity for customer management with authentication fields
- `src/main/java/com/restaurant/demo/model/CartItem.java` - Existing entity that needs updates for proper relationships and timestamps
- `src/main/java/com/restaurant/demo/repository/CustomerRepository.java` - New repository for customer data access
- `src/main/java/com/restaurant/demo/repository/CartItemRepository.java` - Existing repository that may need updates
- `src/main/java/com/restaurant/demo/service/CustomerService.java` - New service for customer registration and authentication
- `src/main/java/com/restaurant/demo/service/CartService.java` - Existing service that needs authentication integration
- `src/main/java/com/restaurant/demo/controller/CustomerController.java` - Existing controller that needs enhancement for registration/login
- `src/main/java/com/restaurant/demo/controller/CartController.java` - Existing controller that needs authentication integration
- `src/main/java/com/restaurant/demo/dto/CustomerRegistrationDto.java` - New DTO for customer registration requests
- `src/main/java/com/restaurant/demo/dto/CustomerLoginDto.java` - New DTO for customer login requests
- `src/main/java/com/restaurant/demo/dto/AuthResponseDto.java` - New DTO for authentication responses
- `src/main/java/com/restaurant/demo/config/SecurityConfig.java` - New configuration for password encoding
- `src/main/java/com/restaurant/demo/config/CorsConfig.java` - New configuration for CORS handling across all API endpoints
- `src/main/java/com/restaurant/demo/exception/GlobalExceptionHandler.java` - New global exception handler for API responses
- `src/main/resources/application.properties` - Configuration update for MySQL connection
- `pom.xml` - Dependency updates for validation and security
- `database-setup.sql` - MySQL database schema creation script
- `src/test/java/com/restaurant/demo/service/CustomerServiceTest.java` - Unit tests for CustomerService
- `src/test/java/com/restaurant/demo/service/CartServiceTest.java` - Unit tests for updated CartService
- `src/test/java/com/restaurant/demo/controller/CustomerControllerTest.java` - Integration tests for CustomerController
- `src/test/java/com/restaurant/demo/controller/CartControllerTest.java` - Integration tests for CartController

### Notes

- Unit tests should be placed alongside the code files they are testing in the same directory structure under `src/test/java/`
- Use `mvn test` to run all tests or `mvn test -Dtest=ClassName` to run specific test classes
- MySQL database should be set up before running integration tests

## Tasks

- [x] 1.0 Configure MySQL Database Integration
  - [x] 1.1 Update `pom.xml` to add Spring Boot Validation and Security dependencies
  - [x] 1.2 Configure MySQL connection in `application.properties` (replace H2 configuration)
  - [x] 1.3 Set up MySQL database schema and connection parameters
  - [x] 1.4 Test MySQL connectivity by running the application

- [x] 2.0 Implement Customer Entity and Authentication System
  - [x] 2.1 Create `Customer.java` entity with id, username, email, password, createdAt, updatedAt fields
  - [x] 2.2 Add JPA annotations and relationships to Customer entity
  - [x] 2.3 Create `CustomerRepository.java` interface with findByUsername and findByEmail methods
  - [x] 2.4 Create `SecurityConfig.java` for password encoding configuration
  - [x] 2.5 Create DTOs: `CustomerRegistrationDto.java`, `CustomerLoginDto.java`, `AuthResponseDto.java`
  - [x] 2.6 Implement `CustomerService.java` with registration, login, and authentication methods
  - [x] 2.7 Add password validation and duplicate email/username checking

- [x] 3.0 Update Cart System with Customer Authentication
  - [x] 3.1 Update `CartItem.java` entity to include proper relationship with Customer entity
  - [x] 3.2 Add createdAt and updatedAt timestamp fields to CartItem entity
  - [x] 3.3 Update `CartItemRepository.java` to use Customer entity instead of customerId
  - [x] 3.4 Modify `CartService.java` methods to work with Customer entity and add authentication checks
  - [x] 3.5 Update cart operations to validate customer ownership of cart items

- [x] 4.0 Implement Customer Registration and Login APIs
  - [x] 4.1 Update `CustomerController.java` to add registration endpoint (/api/customers/register)
  - [x] 4.2 Add login endpoint (/api/customers/login) to CustomerController
  - [x] 4.3 Add customer profile endpoint (/api/customers/profile) for authenticated users
  - [x] 4.4 Update `CartController.java` to require authentication for all cart operations
  - [x] 4.5 Modify cart endpoints to use authenticated customer information
  - [x] 4.6 Add CORS configuration for API endpoints

- [ ] 5.0 Add Comprehensive Error Handling and Validation
  - [ ] 5.1 Create `GlobalExceptionHandler.java` for centralized exception handling
  - [ ] 5.2 Add custom exception classes for business logic errors
  - [ ] 5.3 Add validation annotations to DTOs and entities
  - [ ] 5.4 Implement proper HTTP status codes for different error scenarios
  - [ ] 5.5 Add input validation for all API endpoints
  - [ ] 5.6 Create standardized error response format

- [ ] 6.0 Create Unit and Integration Tests
  - [ ] 6.1 Create `CustomerServiceTest.java` for testing registration and authentication logic
  - [ ] 6.2 Create `CartServiceTest.java` for testing updated cart operations with authentication
  - [ ] 6.3 Create `CustomerControllerTest.java` for testing customer API endpoints
  - [ ] 6.4 Create `CartControllerTest.java` for testing authenticated cart operations
  - [ ] 6.5 Set up test database configuration for integration tests
  - [ ] 6.6 Add test data fixtures and helper methods for consistent testing
