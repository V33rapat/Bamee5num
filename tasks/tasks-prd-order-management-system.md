# Task List: Order Management System Enhancement

Generated from: `prd-order-management-system.md`

## Relevant Files

### Backend - Models
- `Project Principle/demo/src/main/java/com/restaurant/demo/model/CartItem.java` - Add status field to existing CartItem entity
- `Project Principle/demo/src/main/java/com/restaurant/demo/model/Employee.java` - Add username and password fields for authentication
- `Project Principle/demo/src/main/java/com/restaurant/demo/model/Order.java` - *NEW* - Optional: Create dedicated Order entity if needed for better grouping

### Backend - Repositories
- `Project Principle/demo/src/main/java/com/restaurant/demo/repository/CartItemRepository.java` - Added query methods for order status filtering and chronological ordering
- `Project Principle/demo/src/main/java/com/restaurant/demo/repository/EmployeeRepository.java` - *CREATED* - Added findByUsername and existsByUsername methods for employee authentication

### Backend - DTOs
- `Project Principle/demo/src/main/java/com/restaurant/demo/dto/OrderPlacementDto.java` - *CREATED* - DTO for placing orders with customerId validation
- `Project Principle/demo/src/main/java/com/restaurant/demo/dto/OrderResponseDto.java` - *CREATED* - DTO for order responses with items list, totals, status, timestamps
- `Project Principle/demo/src/main/java/com/restaurant/demo/dto/EmployeeLoginDto.java` - *CREATED* - DTO for employee login with username and password
- `Project Principle/demo/src/main/java/com/restaurant/demo/dto/EmployeeRegistrationDto.java` - *CREATED* - DTO for employee registration by manager with name, position, credentials
- `Project Principle/demo/src/main/java/com/restaurant/demo/dto/OrderStatusUpdateDto.java` - *CREATED* - DTO for updating order status with customerId and newStatus validation

### Backend - Services
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/OrderService.java` - *CREATED* - Business logic for order management including place order, get pending orders, update status, and status transition validation
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/EmployeeAuthService.java` - *CREATED* - Employee authentication logic with BCrypt password verification
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/ManagerService.java` - *UPDATED* - Added employee registration method for managers to register new employees
- `Project Principle/demo/src/main/java/com/restaurant/demo/service/CartService.java` - Existing service for cart management (no changes needed)

### Backend - Controllers
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/OrderController.java` - *CREATED* - REST endpoints for order operations (place order, get pending orders)
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/EmployeeController.java` - *CREATED* - REST endpoints for employee operations (login, order management, status updates)
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/ManagerApiController.java` - *UPDATED* - Added employee registration endpoint with role-based access control and order statistics endpoint for manager dashboard
- `Project Principle/demo/src/main/java/com/restaurant/demo/controller/PageController.java` - *UPDATED* - Added employee login page route

### Frontend - HTML Templates
- `Project Principle/demo/src/main/resources/templates/customer-orders.html` - *CREATED* - Customer pending orders view page
- `Project Principle/demo/src/main/resources/templates/employee-orders.html` - *CREATED* - Employee order management page with filtering, statistics, and bill modal
- `Project Principle/demo/src/main/resources/templates/employee-login.html` - *CREATED* - Employee login page with username and password authentication
- `Project Principle/demo/src/main/resources/templates/manager.html` - *UPDATED* - Modified to add employee registration form with username/password fields and order statistics display
- `Project Principle/demo/src/main/resources/templates/employee.html` - Existing employee page (not modified, new employee-orders.html created instead)
- `Project Principle/demo/src/main/resources/templates/customer.html` - *UPDATED* - Modified to add navigation link to pending orders and change button text

### Frontend - JavaScript
- `Project Principle/demo/src/main/resources/static/js/customer-orders.js` - *CREATED* - Customer pending orders functionality with order display, grouping, and status badges
- `Project Principle/demo/src/main/resources/static/js/employee-orders.js` - *CREATED* - Employee order management functionality with filtering, status updates, notification polling, and bill viewing
- `Project Principle/demo/src/main/resources/static/js/employee-auth.js` - *CREATED* - Employee login functionality with session management and redirect
- `Project Principle/demo/src/main/resources/static/js/customer.js` - *UPDATED* - Modified to change payment button to order button with place order API integration
- `Project Principle/demo/src/main/resources/static/js/manager.js` - *UPDATED* - Modified to add employee registration UI handling with username/password fields, validation, and order statistics display

### Database
- `Project Principle/demo/database-setup.sql` - Add ALTER TABLE statements for status column and employee authentication fields

### Notes
- The system uses existing Jakarta Bean Validation for DTOs
- BCrypt password encoding is already configured in the system (PasswordEncoder bean exists)
- Follow existing patterns for REST API endpoints (e.g., `/api/customers`, `/api/cart`)
- Use existing CORS configuration pattern: `@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true", maxAge = 3600)`
- Session management already exists for customers; implement similar pattern for employees
- Status transitions: Pending → In Progress → Finish, or any status → Cancelled
- Consider simple polling (JavaScript setInterval) for order notifications rather than WebSocket

## Tasks

- [x] 1.0 Database Schema Updates
  - [x] 1.1 Add `status` column to `cart_items` table (VARCHAR/ENUM, default 'Pending', values: 'Pending', 'In Progress', 'Cancelled', 'Finish')
  - [x] 1.2 Add `username` column to `employees` table (VARCHAR(50), UNIQUE, NOT NULL)
  - [x] 1.3 Add `password` column to `employees` table (VARCHAR(255), NOT NULL for BCrypt hashes)
  - [x] 1.4 Create indexes on `cart_items.status` for performance
  - [x] 1.5 Test database schema changes with sample data
  
- [x] 2.0 Backend - Model Layer Updates
  - [x] 2.1 Add `status` field to `CartItem.java` entity with validation annotations (@NotBlank, enum validation)
  - [x] 2.2 Add `username` field to `Employee.java` with @Column(unique=true) and validation
  - [x] 2.3 Add `password` field to `Employee.java` with appropriate length constraints
  - [x] 2.4 Update CartItem constructor to accept status parameter (optional, defaults to "Pending")
  - [x] 2.5 Add getters/setters for new fields in both entities
  
- [x] 3.0 Backend - Repository Layer Updates
  - [x] 3.1 Add `findByCustomerAndStatus(Customer customer, String status)` to CartItemRepository
  - [x] 3.2 Add `findByStatus(String status)` to CartItemRepository for employee view
  - [x] 3.3 Add `findAllByOrderByCreatedAtDesc()` to CartItemRepository for chronological order
  - [x] 3.4 Add `findByUsername(String username)` to EmployeeRepository
  - [x] 3.5 Add `existsByUsername(String username)` to EmployeeRepository for validation
  
- [x] 4.0 Backend - DTO Creation
  - [x] 4.1 Create `OrderPlacementDto.java` with customerId and optional fields
  - [x] 4.2 Create `OrderResponseDto.java` with order details, items list, total, status, timestamp
  - [x] 4.3 Create `EmployeeLoginDto.java` with username and password fields
  - [x] 4.4 Create `EmployeeRegistrationDto.java` with name, position, username, password (for manager use)
  - [x] 4.5 Create `OrderStatusUpdateDto.java` with orderId/customerId and newStatus
  - [x] 4.6 Add Jakarta validation annotations to all DTOs (@NotBlank, @Size, etc.)
  
- [x] 5.0 Backend - Service Layer Development
  - [x] 5.1 Create `OrderService.java` with method to place order (convert cart to pending order)
  - [x] 5.2 Add method in OrderService to get pending orders by customerId
  - [x] 5.3 Add method in OrderService to get all orders by status (for employee)
  - [x] 5.4 Add method in OrderService to update order status with validation (status transition rules)
  - [x] 5.5 Add method in OrderService to get order count by status (for notifications)
  - [x] 5.6 Create `EmployeeAuthService.java` with login authentication method (username + password check with BCrypt)
  - [x] 5.7 Add employee registration method to ManagerService or create dedicated service
  - [x] 5.8 Update CartService to integrate with OrderService for order placement
  - [x] 5.9 Add business logic validation for status transitions (Pending→In Progress→Finish, or →Cancelled)
  
- [x] 6.0 Backend - Controller Layer Development
  - [x] 6.1 Create `OrderController.java` with @RestController and @RequestMapping("/api/orders")
  - [x] 6.2 Add POST `/api/customers/{customerId}/place-order` endpoint to place order
  - [x] 6.3 Add GET `/api/customers/{customerId}/pending-orders` endpoint for customer pending orders
  - [x] 6.4 Create `EmployeeController.java` with @RestController and @RequestMapping("/api/employees")
  - [x] 6.5 Add POST `/api/employees/login` endpoint for employee authentication
  - [x] 6.6 Add GET `/api/employees/orders` endpoint with optional status query parameter
  - [x] 6.7 Add GET `/api/employees/orders/{orderId}` endpoint for specific order details
  - [x] 6.8 Add PUT `/api/employees/orders/{orderId}/status` endpoint to update order status
  - [x] 6.9 Add GET `/api/employees/orders/pending/count` endpoint for notification polling
  - [x] 6.10 Add POST `/api/managers/employees` endpoint in ManagerApiController for employee registration
  - [x] 6.11 Add proper CORS configuration and session management to all new endpoints
  - [x] 6.12 Implement role-based access control (employees cannot access manager endpoints)
  
- [x] 7.0 Frontend - Customer Side Features
  - [x] 7.1 Create `customer-orders.html` template with Thymeleaf structure
  - [x] 7.2 Design customer orders page layout (header, order cards, status indicators)
  - [x] 7.3 Create `customer-orders.js` to fetch and display pending orders
  - [x] 7.4 Implement order grouping by customer with item details (name, quantity, price)
  - [x] 7.5 Display order total calculation and status badge
  - [x] 7.6 Modify `customer.html` to add navigation link to pending orders page
  - [x] 7.7 Modify `customer.js` to change "ชำระเงิน" button text to "สั่งจอง"
  - [x] 7.8 Update button click handler to call place-order API instead of payment
  - [x] 7.9 Show success confirmation message after order placement
  - [x] 7.10 Clear cart UI after successful order placement
  - [x] 7.11 Add error handling for order placement failures
  
- [x] 8.0 Frontend - Manager Side Features
  - [x] 8.1 Create employee registration form section in `manager.html` or separate modal
  - [x] 8.2 Add input fields for name, position (dropdown), username, password
  - [x] 8.3 Create `manager-employee-register.js` to handle form submission
  - [x] 8.4 Implement client-side validation (required fields, username uniqueness check)
  - [x] 8.5 Call POST `/api/managers/employees` endpoint with form data
  - [x] 8.6 Display success message with generated credentials (if applicable)
  - [x] 8.7 Display error messages for duplicate username or validation failures
  - [x] 8.8 Add employee registration to manager dashboard navigation/tabs
  - [x] 8.9 Update manager statistics to show order counts (pending, completed)
  - [x] 8.10 Integrate employee registration with existing manager.js functionality
  
- [x] 9.0 Frontend - Employee Side Features
  - [x] 9.1 Create `employee-login.html` template with login form (username, password)
  - [x] 9.2 Create `employee-auth.js` to handle employee login POST request
  - [x] 9.3 Store employee session data after successful login
  - [x] 9.4 Redirect to employee dashboard after login
  - [x] 9.5 Create `employee-orders.html` or modify existing `employee.html` for order management
  - [x] 9.6 Design order management UI (table or card layout grouped by customer)
  - [x] 9.7 Create `employee-orders.js` to fetch and display all orders
  - [x] 9.8 Implement order filtering by status (Pending, In Progress, Finish, Cancelled)
  - [x] 9.9 Display order details: customer ID/name, items list, quantities, prices, subtotals, total
  - [x] 9.10 Add status update dropdown/buttons for each order (Pending→In Progress→Finish, or →Cancelled)
  - [x] 9.11 Implement PUT request to update order status with confirmation
  - [x] 9.12 Add notification polling (setInterval) to check for new pending orders every 30 seconds
  - [x] 9.13 Display notification badge/alert when new orders detected
  - [x] 9.14 Implement bill viewing functionality for orders with any status
  - [x] 9.15 Add access control check to prevent employees from accessing manager pages
  - [x] 9.16 Style status badges with color coding (Pending: yellow, In Progress: blue, Finish: green, Cancelled: red)
  
- [ ] 10.0 Integration Testing and Validation
  - [ ] 10.1 Test customer can place order successfully (cart items get status "Pending")
  - [ ] 10.2 Test customer can view their pending orders with correct item details
  - [ ] 10.3 Test manager can register new employee with username and password
  - [ ] 10.4 Test employee can login with credentials
  - [ ] 10.5 Test employee can see all pending orders from all customers
  - [ ] 10.6 Test employee can update order status (Pending→In Progress→Finish)
  - [ ] 10.7 Test employee can cancel orders from any status
  - [ ] 10.8 Test order notification polling detects new orders
  - [ ] 10.9 Test role-based access control (employees cannot access manager endpoints)
  - [ ] 10.10 Test multiple customers can place orders independently
  - [ ] 10.11 Test order totals calculate correctly with multiple items
  - [ ] 10.12 Test status transition validation (invalid transitions are rejected)
  - [ ] 10.13 Test concurrent order handling (multiple pending orders per customer)
  - [ ] 10.14 Test error handling for all edge cases (missing data, invalid IDs, etc.)
  - [ ] 10.15 Verify database constraints and indexes are working correctly
