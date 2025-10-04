# Task List: Manager Authentication System Implementation

## Relevant Files

### Backend - Model/Entity
- `src/main/java/com/restaurant/demo/model/Employee.java` - Update Employee entity to change id type from int to Long
- `src/main/java/com/restaurant/demo/model/Manager.java` - Update Manager entity to include authentication fields (username, email, password, timestamps) while KEEPING inheritance from Employee

### Backend - DTOs
- `src/main/java/com/restaurant/demo/dto/ManagerRegistrationDto.java` - **NEW** - DTO for manager registration with validation annotations
- `src/main/java/com/restaurant/demo/dto/ManagerLoginDto.java` - **NEW** - DTO for manager login credentials

### Backend - Repository
- `src/main/java/com/restaurant/demo/repository/ManagerRepository.java` - **NEW** - Repository interface with custom query methods (findByEmail, existsByEmail, existsByUsername)

### Backend - Service
- `src/main/java/com/restaurant/demo/service/ManagerService.java` - **CREATED** - Service layer for manager authentication logic (registration, login, password hashing)

### Backend - Controller
- `src/main/java/com/restaurant/demo/controller/ManagerAuthController.java` - **CREATED** - Controller for manager registration and login endpoints with session management and logout functionality

### Backend - Configuration
- `src/main/java/com/restaurant/demo/config/SecurityConfig.java` - Update security configuration to handle manager authentication routes separately

### Backend - Exception Handling
- `src/main/java/com/restaurant/demo/exception/ManagerAlreadyExistsException.java` - **CREATED** - Custom exception for duplicate manager credentials
- `src/main/java/com/restaurant/demo/exception/ManagerNotFoundException.java` - **CREATED** - Custom exception for manager not found scenarios
- `src/main/java/com/restaurant/demo/exception/InvalidManagerCredentialsException.java` - **CREATED** - Custom exception for invalid manager credentials during authentication

### Frontend - Templates
- `src/main/resources/templates/manager-register.html` - **CREATED** - Manager registration page with Thymeleaf form binding and validation
- `src/main/resources/templates/manager-login.html` - **NEW** - Manager login page
- `src/main/resources/templates/manager.html` - Update existing manager dashboard to include logout functionality

### Frontend - JavaScript
- `src/main/resources/static/js/manager-auth.js` - **CREATED** - Client-side validation for manager authentication forms with real-time validation

### Database
- `database-setup.sql` - Update SQL script to change employees.id to BIGINT and alter managers table with new columns (username, email, password, timestamps)

### Testing
- `src/test/java/com/restaurant/demo/service/ManagerServiceTest.java` - **NEW** - Unit tests for ManagerService
- `src/test/java/com/restaurant/demo/controller/ManagerAuthControllerTest.java` - **NEW** - Integration tests for ManagerAuthController
- `src/test/java/com/restaurant/demo/repository/ManagerRepositoryTest.java` - **NEW** - Repository tests for custom query methods

### Notes
- The existing codebase uses BCryptPasswordEncoder (already configured in SecurityConfig)
- Customer authentication pattern in `CustomerService.java` can be used as reference
- **IMPORTANT:** Manager entity uses JOINED inheritance strategy (extends Employee) - DO NOT break this inheritance!
- Manager inherits from Employee, so authentication fields are added to Manager class while keeping the inheritance intact
- ID type changed from `int` to `Long` in Employee for consistency with Customer entity (which uses Long)
- The managers table has a foreign key to employees(id), maintaining the inheritance relationship at database level
- Test structure follows pattern in existing test files under `src/test/java/com/restaurant/demo/`
- Run tests using Maven: `mvn test` or specific test: `mvn test -Dtest=ManagerServiceTest`

---

## Tasks

- [x] 1.0 Update Database Schema for Manager Authentication
  - [x] 1.1 Open `database-setup.sql` file
  - [x] 1.2 Locate the `employees` table definition - change `id` column type from `INT` to `BIGINT` for consistency
  - [x] 1.3 Update the `managers` table foreign key to reference `employees(id)` with BIGINT type
  - [x] 1.4 Locate the `managers` table definition (currently only has `id` column with FK to employees)
  - [x] 1.5 Add ALTER TABLE statement to add `username` VARCHAR(50) UNIQUE NOT NULL to managers table
  - [x] 1.6 Add ALTER TABLE statement to add `email` VARCHAR(100) UNIQUE NOT NULL to managers table
  - [x] 1.7 Add ALTER TABLE statement to add `password` VARCHAR(255) NOT NULL to managers table
  - [x] 1.8 Add ALTER TABLE statement to add `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP to managers table
  - [x] 1.9 Add ALTER TABLE statement to add `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP to managers table
  - [x] 1.10 Add indexes for `username` and `email` columns on managers table for performance
  - [x] 1.11 Run the SQL script against your database to apply the changes
  - [x] 1.12 Verify the table structure using `DESCRIBE employees;` and `DESCRIBE managers;` in MySQL
  - [x] 1.13 Verify the foreign key relationship is intact between managers and employees

- [x] 2.0 Update Manager Entity and Create DTOs
  - [x] 2.1 Open `Employee.java` and change `id` field type from `int` to `Long` for consistency
  - [x] 2.2 Update Employee constructors and methods to use `Long` instead of `int`
  - [x] 2.3 Open `Manager.java` - KEEP the inheritance from Employee (extends Employee)
  - [x] 2.4 KEEP existing annotations: `@Entity`, `@Table(name = "managers")`, `@PrimaryKeyJoinColumn(name = "id")`
  - [x] 2.5 KEEP all existing business methods (addItem, removeItem, manageEmployees, viewSalesReport, SalesReport class)
  - [x] 2.6 Add new private fields for authentication: `username` (String), `email` (String), `password` (String)
  - [x] 2.7 Add new private fields for timestamps: `createdAt` (LocalDateTime), `updatedAt` (LocalDateTime)
  - [x] 2.8 Add `@Column` annotation on username: `@Column(unique = true, nullable = false, length = 50)`
  - [x] 2.9 Add `@Column` annotation on email: `@Column(unique = true, nullable = false, length = 100)`
  - [x] 2.10 Add `@Column` annotation on password: `@Column(nullable = false, length = 255)`
  - [x] 2.11 Add `@Column` annotations on timestamps: `@Column(name = "created_at")` and `@Column(name = "updated_at")`
  - [x] 2.12 Add validation annotations: `@NotBlank` on username and email, `@Email` on email, `@Size` constraints
  - [x] 2.13 Add `@PrePersist` method to set `createdAt` and `updatedAt` to current timestamp
  - [x] 2.14 Add `@PreUpdate` method to update `updatedAt` to current timestamp
  - [x] 2.15 Generate getters and setters for new fields (username, email, password, createdAt, updatedAt)
  - [x] 2.16 Update existing constructors or add new constructors that accept authentication parameters
  - [x] 2.17 Create `ManagerRegistrationDto.java` in dto package
  - [x] 2.18 Add fields to ManagerRegistrationDto: username, email, password, confirmPassword
  - [x] 2.19 Add validation annotations: `@NotBlank`, `@Email`, `@Size(min=8)` for password, `@Size(min=3, max=50)` for username
  - [x] 2.20 Generate getters and setters for ManagerRegistrationDto
  - [x] 2.21 Create `ManagerLoginDto.java` in dto package
  - [x] 2.22 Add fields to ManagerLoginDto: email, password
  - [x] 2.23 Add validation annotations: `@NotBlank`, `@Email`
  - [x] 2.24 Generate getters and setters for `ManagerLoginDto`

- [x] 3.0 Implement Manager Repository Layer
  - [x] 3.1 Create `ManagerRepository.java` interface in repository package
  - [x] 3.2 Extend `JpaRepository<Manager, Long>` (Note: Long type matches updated Employee id)
  - [x] 3.3 Add custom query method: `Optional<Manager> findByEmail(String email);`
  - [x] 3.4 Add custom query method: `Optional<Manager> findByUsername(String username);`
  - [x] 3.5 Add custom query method: `boolean existsByEmail(String email);`
  - [x] 3.6 Add custom query method: `boolean existsByUsername(String username);`
  - [x] 3.7 Add `@Repository` annotation to the interface (optional but recommended)

- [x] 4.0 Implement Manager Service Layer
  - [x] 4.1 Create `ManagerService.java` class in service package
  - [x] 4.2 Add `@Service` and `@Transactional` annotations
  - [x] 4.3 Inject `ManagerRepository` using `@Autowired` or constructor injection
  - [x] 4.4 Inject `PasswordEncoder` using `@Autowired` or constructor injection
  - [x] 4.5 Create `registerManager(ManagerRegistrationDto dto)` method
  - [x] 4.6 In registerManager: Check if email already exists using `existsByEmail()`, throw ManagerAlreadyExistsException if true
  - [x] 4.7 In registerManager: Check if username already exists using `existsByUsername()`, throw ManagerAlreadyExistsException if true
  - [x] 4.8 In registerManager: Validate password and confirmPassword match, throw exception if not
  - [x] 4.9 In registerManager: Create new Manager entity, set username, email
  - [x] 4.10 In registerManager: Hash password using `passwordEncoder.encode(dto.getPassword())`
  - [x] 4.11 In registerManager: Save manager to database using repository.save()
  - [x] 4.12 In registerManager: Return saved manager or success message
  - [x] 4.13 Create `authenticateManager(String email, String password)` method
  - [x] 4.14 In authenticateManager: Find manager by email using `findByEmail()`
  - [x] 4.15 In authenticateManager: If not found, return Optional.empty() or throw exception
  - [x] 4.16 In authenticateManager: Verify password using `passwordEncoder.matches(password, manager.getPassword())`
  - [x] 4.17 In authenticateManager: Return Optional<Manager> if authentication successful
  - [x] 4.18 Create `getManagerByEmail(String email)` method that returns Optional<Manager>
  - [x] 4.19 Create `getManagerById(Long id)` method that returns Optional<Manager>

- [x] 5.0 Create Manager Authentication Controller
  - [x] 5.1 Create `ManagerAuthController.java` class in controller package
  - [x] 5.2 Add `@Controller` annotation (not @RestController, since we're returning views)
  - [x] 5.3 Add `@RequestMapping("/manager")` at class level
  - [x] 5.4 Inject `ManagerService` using constructor injection
  - [x] 5.5 Create `showRegistrationForm()` method with `@GetMapping("/register")`
  - [x] 5.6 In showRegistrationForm: Add empty ManagerRegistrationDto to model
  - [x] 5.7 In showRegistrationForm: Return "manager-register" view name
  - [x] 5.8 Create `processRegistration()` method with `@PostMapping("/register")`
  - [x] 5.9 In processRegistration: Add `@Valid @ModelAttribute` ManagerRegistrationDto parameter
  - [x] 5.10 In processRegistration: Add BindingResult parameter to capture validation errors
  - [x] 5.11 In processRegistration: Check for validation errors, return to form if errors exist
  - [x] 5.12 In processRegistration: Check if passwords match, add error and return if not
  - [x] 5.13 In processRegistration: Call managerService.registerManager() in try-catch block
  - [x] 5.14 In processRegistration: Catch ManagerAlreadyExistsException and add error to model
  - [x] 5.15 In processRegistration: On success, redirect to "/manager/login" with success message
  - [x] 5.16 Create `showLoginForm()` method with `@GetMapping("/login")`
  - [x] 5.17 In showLoginForm: Add empty ManagerLoginDto to model
  - [x] 5.18 In showLoginForm: Return "manager-login" view name
  - [x] 5.19 Create `processLogin()` method with `@PostMapping("/login")`
  - [x] 5.20 In processLogin: Add `@Valid @ModelAttribute` ManagerLoginDto parameter
  - [x] 5.21 In processLogin: Add HttpSession parameter to manage session
  - [x] 5.22 In processLogin: Call managerService.authenticateManager()
  - [x] 5.23 In processLogin: If authentication fails, add error message and return to login form
  - [x] 5.24 In processLogin: If successful, store manager ID, username, email in session
  - [x] 5.25 In processLogin: Set session attribute "managerAuthenticated" = true
  - [x] 5.26 In processLogin: Redirect to "/manager" (dashboard)
  - [x] 5.27 Create `logout()` method with `@PostMapping("/logout")` or `@GetMapping("/logout")`
  - [x] 5.28 In logout: Invalidate session using `session.invalidate()`
  - [x] 5.29 In logout: Redirect to "/manager/login"

- [x] 6.0 Update Security Configuration for Manager Routes
  - [x] 6.1 Open `SecurityConfig.java` file
  - [x] 6.2 Locate the `authorizeHttpRequests` configuration block
  - [x] 6.3 Add `.requestMatchers("/manager/register", "/manager/login").permitAll()` before authentication requirements
  - [x] 6.4 Add `.requestMatchers("/manager/**").authenticated()` to require authentication for manager dashboard
  - [x] 6.5 Consider creating a separate SecurityFilterChain for manager routes (optional, for complete separation)
  - [x] 6.6 If creating separate chain: Create new `@Bean` method `managerSecurityFilterChain(HttpSecurity http)`
  - [x] 6.7 If creating separate chain: Use `.securityMatcher("/manager/**")` to apply only to manager routes
  - [x] 6.8 If creating separate chain: Configure separate session management for managers
  - [x] 6.9 If creating separate chain: Set login page to "/manager/login"
  - [x] 6.10 If creating separate chain: Set logout URL to "/manager/logout"
  - [x] 6.11 Ensure BCryptPasswordEncoder bean is available (already exists, verify it's being used)
  - [x] 6.12 Test that manager routes are accessible without breaking customer authentication

- [x] 7.0 Create Manager Registration Frontend (HTML + JS)
  - [x] 7.1 Create `manager-register.html` file in `src/main/resources/templates/`
  - [x] 7.2 Add HTML boilerplate with Thymeleaf namespace: `xmlns:th="http://www.thymeleaf.org"`
  - [x] 7.3 Copy styling from `register.html` (customer registration) for consistency
  - [x] 7.4 Set page title to "Manager Registration - Bamee 5 Num"
  - [x] 7.5 Create main container div with class "register-container"
  - [x] 7.6 Add heading "Manager Registration"
  - [x] 7.7 Create form with `th:action="@{/manager/register}"` and `method="post"`
  - [x] 7.8 Add `th:object="${managerRegistrationDto}"` to form tag
  - [x] 7.9 Add CSRF token input: `<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>`
  - [x] 7.10 Create form group for username with label, input, and error display
  - [x] 7.11 Add input: `<input type="text" th:field="*{username}" placeholder="Username" required />`
  - [x] 7.12 Add error display: `<span th:if="${#fields.hasErrors('username')}" th:errors="*{username}"></span>`
  - [x] 7.13 Create form group for email with validation
  - [x] 7.14 Add input: `<input type="email" th:field="*{email}" placeholder="Email" required />`
  - [x] 7.15 Add error display for email field
  - [x] 7.16 Create form group for password with minimum 8 characters
  - [x] 7.17 Add input: `<input type="password" th:field="*{password}" placeholder="Password (min 8 characters)" required />`
  - [x] 7.18 Add error display for password field
  - [x] 7.19 Create form group for confirm password
  - [x] 7.20 Add input: `<input type="password" th:field="*{confirmPassword}" placeholder="Confirm Password" required />`
  - [x] 7.21 Add error display for confirmPassword field
  - [x] 7.22 Add general error message display area: `<div th:if="${error}" class="error-message" th:text="${error}"></div>`
  - [x] 7.23 Add success message display area: `<div th:if="${message}" class="success-message" th:text="${message}"></div>`
  - [x] 7.24 Add submit button with text "Register"
  - [x] 7.25 Add link to login page: "Already have an account? <a th:href="@{/manager/login}">Login here</a>"
  - [x] 7.26 Add CSS styling for error messages (red color)
  - [x] 7.27 Add CSS styling for success messages (green color)
  - [x] 7.28 Ensure responsive design matches existing pages
  - [x] 7.29 Create `manager-auth.js` file in `src/main/resources/static/js/`
  - [x] 7.30 Add client-side validation for username (min 3 characters, alphanumeric + underscore only)
  - [x] 7.31 Add client-side validation for email format
  - [x] 7.32 Add client-side validation for password length (min 8 characters)
  - [x] 7.33 Add client-side validation to check if password and confirmPassword match
  - [x] 7.34 Display validation errors in real-time as user types
  - [x] 7.35 Disable submit button if validation fails
  - [x] 7.36 Link manager-auth.js to manager-register.html: `<script src="/js/manager-auth.js"></script>`

- [x] 8.0 Create Manager Login Frontend (HTML + JS)
  - [x] 8.1 Create `manager-login.html` file in `src/main/resources/templates/`
  - [x] 8.2 Add HTML boilerplate with Thymeleaf namespace
  - [x] 8.3 Copy styling from `login.html` (customer login) for consistency
  - [x] 8.4 Set page title to "Manager Login - Bamee 5 Num"
  - [x] 8.5 Create main container div with class "login-container"
  - [x] 8.6 Add heading "Manager Login"
  - [x] 8.7 Create form with `th:action="@{/manager/login}"` and `method="post"`
  - [x] 8.8 Add `th:object="${managerLoginDto}"` to form tag
  - [x] 8.9 Add CSRF token input
  - [x] 8.10 Create form group for email
  - [x] 8.11 Add input: `<input type="email" th:field="*{email}" placeholder="Email" required />`
  - [x] 8.12 Add error display for email field
  - [x] 8.13 Create form group for password
  - [x] 8.14 Add input: `<input type="password" th:field="*{password}" placeholder="Password" required />`
  - [x] 8.15 Add error display for password field
  - [x] 8.16 Add error message display: `<div th:if="${error}" class="error-message" th:text="${error}"></div>`
  - [x] 8.17 Add success message display: `<div th:if="${message}" class="success-message" th:text="${message}"></div>`
  - [x] 8.18 Add submit button with text "Login"
  - [x] 8.19 Add link to registration page: "Don't have an account? <a th:href="@{/manager/register}">Register here</a>"
  - [x] 8.20 Add CSS styling consistent with login.html
  - [x] 8.21 Ensure responsive design
  - [x] 8.22 Link manager-auth.js for client-side validation: `<script src="/js/manager-auth.js"></script>`
  - [x] 8.23 In manager-auth.js, add login form validation
  - [x] 8.24 Validate email format before submission
  - [x] 8.25 Ensure password field is not empty before submission

- [ ] 9.0 Update Manager Dashboard with Logout Functionality
  - [ ] 9.1 Open `manager.html` file
  - [ ] 9.2 Locate the header or navigation section
  - [ ] 9.3 Add welcome message displaying manager username from session: `<span th:text="${session.managerUsername}"></span>`
  - [ ] 9.4 Create logout button or link
  - [ ] 9.5 Add logout form: `<form th:action="@{/manager/logout}" method="post">`
  - [ ] 9.6 Add CSRF token to logout form
  - [ ] 9.7 Add submit button: `<button type="submit">Logout</button>`
  - [ ] 9.8 Style the logout button to match the dashboard design
  - [ ] 9.9 Position logout button in top-right corner or header area
  - [ ] 9.10 Open `manager.js` file
  - [ ] 9.11 Add session check on page load to verify manager is authenticated
  - [ ] 9.12 If session check fails, redirect to `/manager/login`
  - [ ] 9.13 Test that logout clears session and redirects properly

- [ ] 10.0 Implement Custom Exception Handlers for Manager Authentication
  - [ ] 10.1 Create `ManagerAlreadyExistsException.java` in exception package
  - [ ] 10.2 Extend `RuntimeException`
  - [ ] 10.3 Add constructor accepting custom message
  - [ ] 10.4 Add static factory method: `withEmail(String email)` returning exception with message
  - [ ] 10.5 Add static factory method: `withUsername(String username)` returning exception with message
  - [ ] 10.6 Create `ManagerNotFoundException.java` in exception package
  - [ ] 10.7 Extend `RuntimeException`
  - [ ] 10.8 Add constructor accepting custom message
  - [ ] 10.9 Add static factory method: `withEmail(String email)` returning appropriate message
  - [ ] 10.10 Add static factory method: `withId(Long id)` returning appropriate message
  - [ ] 10.11 Create `InvalidManagerCredentialsException.java` in exception package (optional)
  - [ ] 10.12 Extend `RuntimeException`
  - [ ] 10.13 Add constructor with message "Invalid email or password"
  - [ ] 10.14 Update ManagerService to throw these custom exceptions where appropriate
  - [ ] 10.15 Add `@ControllerAdvice` class to handle exceptions globally (optional, or handle in controller)

- [ ] 11.0 Write Unit and Integration Tests
  - [ ] 11.1 Create `ManagerRepositoryTest.java` in test/repository package
  - [ ] 11.2 Add `@DataJpaTest` annotation
  - [ ] 11.3 Inject `ManagerRepository` using `@Autowired`
  - [ ] 11.4 Write test: `testFindByEmail_Success()` - create manager, save, find by email, assert found
  - [ ] 11.5 Write test: `testFindByEmail_NotFound()` - find non-existent email, assert empty Optional
  - [ ] 11.6 Write test: `testExistsByEmail_True()` - create manager, assert existsByEmail returns true
  - [ ] 11.7 Write test: `testExistsByEmail_False()` - assert non-existent email returns false
  - [ ] 11.8 Write test: `testExistsByUsername_True()` - create manager, assert existsByUsername returns true
  - [ ] 11.9 Write test: `testExistsByUsername_False()` - assert non-existent username returns false
  - [ ] 11.10 Write test: `testSaveManager_Success()` - create and save manager, assert ID is generated
  - [ ] 11.11 Create `ManagerServiceTest.java` in test/service package
  - [ ] 11.12 Add `@ExtendWith(MockitoExtension.class)` annotation
  - [ ] 11.13 Mock `ManagerRepository` using `@Mock`
  - [ ] 11.14 Mock `PasswordEncoder` using `@Mock`
  - [ ] 11.15 Create `@InjectMocks` ManagerService instance
  - [ ] 11.16 Write test: `testRegisterManager_Success()` - mock repository returns, assert manager saved
  - [ ] 11.17 Write test: `testRegisterManager_EmailExists()` - mock existsByEmail=true, assert exception thrown
  - [ ] 11.18 Write test: `testRegisterManager_UsernameExists()` - mock existsByUsername=true, assert exception thrown
  - [ ] 11.19 Write test: `testRegisterManager_PasswordMismatch()` - pass mismatched passwords, assert exception
  - [ ] 11.20 Write test: `testAuthenticateManager_Success()` - mock findByEmail, mock password matches, assert success
  - [ ] 11.21 Write test: `testAuthenticateManager_InvalidEmail()` - mock findByEmail returns empty, assert failure
  - [ ] 11.22 Write test: `testAuthenticateManager_InvalidPassword()` - mock password doesn't match, assert failure
  - [ ] 11.23 Write test: `testGetManagerByEmail_Found()` - mock repository, assert manager returned
  - [ ] 11.24 Write test: `testGetManagerByEmail_NotFound()` - mock returns empty, assert empty Optional
  - [ ] 11.25 Create `ManagerAuthControllerTest.java` in test/controller package
  - [ ] 11.26 Add `@WebMvcTest(ManagerAuthController.class)` annotation
  - [ ] 11.27 Inject `MockMvc` using `@Autowired`
  - [ ] 11.28 Mock `ManagerService` using `@MockBean`
  - [ ] 11.29 Write test: `testShowRegistrationForm()` - perform GET /manager/register, assert view name
  - [ ] 11.30 Write test: `testShowLoginForm()` - perform GET /manager/login, assert view name
  - [ ] 11.31 Write test: `testProcessRegistration_Success()` - perform POST with valid data, assert redirect
  - [ ] 11.32 Write test: `testProcessRegistration_ValidationErrors()` - POST with invalid data, assert errors
  - [ ] 11.33 Write test: `testProcessRegistration_EmailExists()` - mock service throws exception, assert error
  - [ ] 11.34 Write test: `testProcessLogin_Success()` - POST valid credentials, assert redirect to dashboard
  - [ ] 11.35 Write test: `testProcessLogin_InvalidCredentials()` - POST invalid credentials, assert error message
  - [ ] 11.36 Write test: `testLogout()` - perform logout, assert redirect to login page
  - [ ] 11.37 Run all tests using `mvn test` and ensure they pass
  - [ ] 11.38 Check test coverage (aim for >80% coverage)

- [ ] 12.0 End-to-End Testing and Validation
  - [ ] 12.1 Start the Spring Boot application using `mvn spring-boot:run` or run from IDE
  - [ ] 12.2 Verify database connection is successful and managers table has new columns
  - [ ] 12.3 Navigate to `http://localhost:8080/manager/register` in browser
  - [ ] 12.4 Verify registration page loads correctly with all form fields
  - [ ] 12.5 Test registration with valid data, verify redirect to login page
  - [ ] 12.6 Test registration with duplicate email, verify error message displayed
  - [ ] 12.7 Test registration with duplicate username, verify error message displayed
  - [ ] 12.8 Test registration with password < 8 characters, verify validation error
  - [ ] 12.9 Test registration with mismatched passwords, verify error message
  - [ ] 12.10 Test registration with invalid email format, verify validation error
  - [ ] 12.11 Navigate to `http://localhost:8080/manager/login`
  - [ ] 12.12 Verify login page loads correctly
  - [ ] 12.13 Test login with valid credentials from previous registration
  - [ ] 12.14 Verify redirect to manager dashboard on successful login
  - [ ] 12.15 Verify manager username is displayed on dashboard
  - [ ] 12.16 Test login with invalid email, verify error message
  - [ ] 12.17 Test login with incorrect password, verify error message
  - [ ] 12.18 Test accessing `/manager` without authentication, verify redirect to login
  - [ ] 12.19 After login, test logout functionality
  - [ ] 12.20 Verify logout clears session and redirects to login page
  - [ ] 12.21 After logout, try accessing `/manager` again, verify redirect to login
  - [ ] 12.22 Test that customer login still works independently (no interference)
  - [ ] 12.23 Verify manager session doesn't affect customer session and vice versa
  - [ ] 12.24 Test client-side validation in registration form (try submitting invalid data)
  - [ ] 12.25 Test client-side validation in login form
  - [ ] 12.26 Check browser console for any JavaScript errors
  - [ ] 12.27 Test responsive design on mobile viewport
  - [ ] 12.28 Verify all CSS styles are applied correctly
  - [ ] 12.29 Check database to confirm manager records are saved with hashed passwords
  - [ ] 12.30 Verify timestamps (created_at, updated_at) are populated correctly
  - [ ] 12.31 Document any issues found during testing
  - [ ] 12.32 Fix any bugs or issues discovered
  - [ ] 12.33 Perform final end-to-end test of complete flow: register → login → dashboard → logout
  - [ ] 12.34 Mark feature as complete and ready for production
