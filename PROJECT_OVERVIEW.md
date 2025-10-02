# Project Overview: Bamee5num Restaurant Web Application
**Branch:** `feature/seperate-customer-cart`  
**Last Updated:** October 2, 2025

---

## 🎯 Project Summary
A Spring Boot web application for "Bamee5num" (บะหมี่ห้าน้ำ) - a restaurant management system with customer authentication, shopping cart functionality, and role-based access control.

---

## 🏗️ Technology Stack

### Backend
- **Framework:** Spring Boot 3.5.5
- **Java Version:** 17
- **Build Tool:** Maven
- **Database:** MySQL (Production) / H2 (Testing)
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security with BCrypt password encoding
- **Validation:** Jakarta Bean Validation

### Frontend
- **Template Engine:** Thymeleaf
- **JavaScript:** ES6 Modules (customer.js, employee.js, manager.js, db.js, landing.js)
- **CSS:** Custom styling (style.css)
- **Architecture:** Server-rendered pages with AJAX API calls

### Key Dependencies
```xml
- spring-boot-starter-web
- spring-boot-starter-thymeleaf
- spring-boot-starter-validation
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-test
- mysql-connector-j (9.4.0)
- h2database (test scope)
```

---

## 📁 Project Structure

```
demo/
├── src/main/java/com/restaurant/demo/
│   ├── DemoApplication.java          # Main application entry point
│   ├── config/                        # Configuration classes
│   │   ├── SecurityConfig.java        # Spring Security configuration
│   │   ├── CorsConfig.java            # CORS settings
│   │   ├── ValidationConfig.java      # Validation setup
│   │   └── CustomAuthenticationSuccessHandler.java
│   ├── controller/                    # REST & Page Controllers
│   │   ├── CustomerController.java    # Customer API endpoints
│   │   ├── CartController.java        # Cart API endpoints
│   │   └── PageController.java        # Thymeleaf page routing
│   ├── service/                       # Business logic
│   │   ├── CustomerService.java       # Customer management
│   │   ├── CartService.java           # Cart operations
│   │   └── CustomUserDetailsService.java
│   ├── repository/                    # Data access layer
│   │   ├── CustomerRepository.java    # Customer CRUD
│   │   └── CartItemRepository.java    # Cart CRUD
│   ├── model/                         # Entity classes
│   │   ├── Customer.java              # Customer entity
│   │   └── CartItem.java              # Cart item entity
│   ├── dto/                           # Data Transfer Objects
│   │   ├── CustomerRegistrationDto.java
│   │   ├── CustomerLoginDto.java
│   │   └── AuthResponseDto.java
│   └── exception/                     # Custom exceptions & handlers
│       ├── GlobalExceptionHandler.java
│       ├── ErrorResponse.java
│       ├── ValidationErrorResponse.java
│       ├── CustomerNotFoundException.java
│       ├── CustomerAlreadyExistsException.java
│       ├── InvalidCredentialsException.java
│       ├── CartItemNotFoundException.java
│       ├── UnauthorizedCartAccessException.java
│       └── InvalidCartOperationException.java
│
├── src/main/resources/
│   ├── application.properties         # Database & server config
│   ├── static/                        # Static assets
│   │   ├── css/style.css
│   │   └── js/
│   │       ├── customer.js            # Customer dashboard logic
│   │       ├── employee.js
│   │       ├── manager.js
│   │       ├── landing.js
│   │       └── db.js
│   └── templates/                     # Thymeleaf templates
│       ├── index.html                 # Landing page
│       ├── login.html
│       ├── register.html
│       ├── customer.html              # Customer dashboard
│       ├── employee.html
│       └── manager.html
│
├── src/test/java/com/restaurant/demo/
│   ├── BaseIntegrationTest.java       # Base test class
│   ├── DemoApplicationTests.java
│   ├── config/                        # Test configurations (empty)
│   ├── controller/                    # Controller tests (empty)
│   ├── fixtures/                      # Test data fixtures (empty)
│   ├── service/                       # Service tests (empty)
│   └── utils/                         # Test utilities (empty)
│
├── src/test/resources/
│   └── application-test.properties    # Test database config
│
├── database-setup.sql                 # MySQL schema initialization
├── pom.xml                            # Maven configuration
└── target/                            # Compiled classes
```

---

## 🗄️ Database Schema

### Tables

#### **customers**
```sql
- id: BIGINT (PK, AUTO_INCREMENT)
- name: VARCHAR(50) NOT NULL
- username: VARCHAR(20) UNIQUE NOT NULL
- email: VARCHAR(100) UNIQUE NOT NULL
- phone: VARCHAR(15) NOT NULL
- password_hash: VARCHAR(255) NOT NULL
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
```

#### **cart_items**
```sql
- id: BIGINT (PK, AUTO_INCREMENT)
- customer_id: BIGINT (FK -> customers.id) NOT NULL
- item_name: VARCHAR(255) NOT NULL
- item_price: DECIMAL(10,2) NOT NULL
- quantity: INT NOT NULL DEFAULT 1
- created_at: TIMESTAMP
- updated_at: TIMESTAMP
```

**Relationships:**
- One Customer → Many CartItems (OneToMany)
- Each CartItem → One Customer (ManyToOne with CASCADE DELETE)

---

## 🔐 Security Configuration

### Authentication
- **Method:** Form-based login with Spring Security
- **Password Encoding:** BCrypt
- **Session Management:** HTTP session with 30-minute timeout
- **Cookie Settings:** HttpOnly, SameSite=Lax, Secure=false (development)

### Authorization
- **Public Endpoints:**
  - `/`, `/login`, `/register`
  - `/css/**`, `/js/**`, `/images/**`, `/static/**`
  - `/api/customers/login`, `/api/customers/register`
  - `/customer/**`, `/api/cart/**` (authenticated users)
  
- **Protected Endpoints:**
  - All other routes require authentication

### CORS
- **Allowed Origins:** `http://localhost:8080`
- **Allow Credentials:** `true`
- **Max Age:** 3600 seconds

---

## 🛠️ Key Features

### 1. Customer Management
**Endpoints:** `/api/customers/**`

- **Registration** (`POST /api/customers/register`)
  - Validates: name, username, email, phone, password
  - Password confirmation check
  - BCrypt password hashing
  - Returns AuthResponseDto with token & customer info

- **Login** (`POST /api/customers/login`)
  - Accepts username OR email + password
  - Creates HTTP session with customerId & username
  - Returns AuthResponseDto

- **Profile Management** (`GET /api/customers/{customerId}`)
  - Retrieve customer profile
  
- **Update Profile** (`PUT /api/customers/{customerId}`)
  - Update customer information

- **Validation Endpoints:**
  - `GET /api/customers/check-username` - Check username availability
  - `GET /api/customers/check-email` - Check email availability
  - `POST /api/customers/validate-password` - Validate password strength

### 2. Shopping Cart Management
**Endpoints:** `/api/cart/**`

- **Add to Cart** (`POST /api/cart/add`)
  - Parameters: customerId, itemName, itemPrice, quantity
  - Merges quantities if item already exists
  - Max quantity: 99 per item
  
- **Update Quantity** (`PUT /api/cart/update/{cartItemId}`)
  - Parameters: customerId, quantity
  - Validates ownership before update
  
- **Remove Item** (`DELETE /api/cart/remove/{cartItemId}`)
  - Validates customer ownership
  
- **Get Cart** (`GET /api/cart/customer/{customerId}`)
  - Returns all cart items for customer
  
- **Get Single Item** (`GET /api/cart/{cartItemId}`)
  - Requires customerId for authorization
  
- **Clear Cart** (`DELETE /api/cart/clear/{customerId}`)
  - Removes all items from customer's cart
  
- **Get Total** (`GET /api/cart/total/{customerId}`)
  - Calculates total price of all items

### 3. Page Navigation
**Controller:** `PageController.java`

- `GET /` → index.html (landing page)
- `GET /login` → login.html
- `GET /register` → register.html
- `POST /register` → Process registration form
- Customer-specific pages with authentication

---

## ✅ Validation Rules

### Customer Entity
- **Name:** 2-50 chars, letters & spaces only
- **Username:** 3-20 chars, alphanumeric + underscore, unique
- **Email:** Valid email format, max 100 chars, unique
- **Phone:** Thai format (0XX-XXX-XXXX or +66XX-XXX-XXXX)
- **Password:** Min 8 chars (stored as BCrypt hash)

### Cart Item Entity
- **Item Name:** 1-100 chars, required
- **Item Price:** 0.01 to 9999.99, max 4 integer digits, 2 decimal places
- **Quantity:** 1-100, integer

### API Validation
- All request parameters validated with Jakarta Bean Validation
- `@Valid` annotation on request bodies
- `@Validated` on controller classes
- Custom error responses via `GlobalExceptionHandler`

---

## 🧪 Testing Infrastructure

### Configuration
- **Base Class:** `BaseIntegrationTest.java`
- **Test Profile:** `@ActiveProfiles("test")`
- **Test Database:** H2 in-memory database
- **Transaction Management:** `@Transactional` with rollback after each test

### Test Properties
Located in `src/test/resources/application-test.properties`

### Test Structure (Placeholders)
- `controller/` - Controller integration tests (empty)
- `service/` - Service unit tests (empty)
- `fixtures/` - Test data factories (empty)
- `utils/` - Test helper utilities (empty)

---

## 🚀 Running the Application

### Prerequisites
- Java 17
- Maven
- MySQL Server (running on localhost:3306)
- Database: `restaurant_db`

### Database Setup
```sql
-- Run database-setup.sql in MySQL
-- Creates tables: customers, cart_items
-- Inserts sample test data
```

### Start Application
```bash
cd "Project Principle/demo"
mvn spring-boot:run
```

### Access
- **URL:** http://localhost:8080
- **Default Port:** 8080

---

## 🔍 Important Implementation Details

### Session Management
- `CustomerController.loginCustomer()` stores `customerId` and `username` in HTTP session
- Session timeout: 30 minutes
- Logout invalidates session and deletes JSESSIONID cookie

### Cart Operations
- **Ownership Validation:** All cart operations verify customer ownership
- **Duplicate Detection:** Adding existing items increases quantity
- **Quantity Limits:** 1-99 per item, enforced at service layer
- **Cascade Delete:** Deleting a customer removes all their cart items

### Password Security
- Passwords never stored in plain text
- BCrypt hashing with Spring Security's PasswordEncoder
- Login accepts either username OR email
- CustomUserDetailsService integrates with Spring Security

### Error Handling
- **GlobalExceptionHandler:** Centralized exception handling
- **Custom Exceptions:** Domain-specific exceptions for clear error context
- **Error Responses:** Structured JSON error responses with ErrorResponse & ValidationErrorResponse DTOs

### Frontend Architecture
- **customer.js:** Modular ES6 functions for cart operations
- **AJAX Integration:** Fetch API for REST calls
- **CSRF Protection:** Meta tags for CSRF tokens in forms
- **Session-based Auth:** Server-rendered pages with session validation

---

## 📝 Configuration Files

### application.properties
```properties
# MySQL Connection
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_db
spring.datasource.username=root
spring.datasource.password=Learnforfuture_01

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Session
server.servlet.session.timeout=30m
```

---

## 🔄 Recent Changes (Feature Branch)

This branch (`feature/seperate-customer-cart`) likely includes:
1. **Separated Cart Management:** Independent CartController & CartService
2. **Customer-specific Cart Operations:** All cart endpoints require customerId
3. **Enhanced Validation:** Comprehensive validation on cart operations
4. **Session Integration:** HTTP session storage for authenticated users
5. **Ownership Checks:** Security layer preventing unauthorized cart access

---

## ⚠️ Potential Merge Conflict Areas

### High Risk Files
1. **pom.xml** - Dependency version conflicts
2. **SecurityConfig.java** - Security rule changes
3. **CustomerController.java** - New endpoints or authentication logic
4. **CartController.java** - This entire controller may be new
5. **CartService.java** - Major refactoring likely
6. **Customer.java / CartItem.java** - Entity relationship changes
7. **application.properties** - Configuration differences
8. **database-setup.sql** - Schema modifications

### Medium Risk Files
1. **PageController.java** - New route mappings
2. **customer.js** - Frontend cart integration
3. **customer.html** - UI changes for cart display
4. **GlobalExceptionHandler.java** - New exception handlers

### Low Risk Files
1. Static assets (CSS, other JS files)
2. Other templates (employee.html, manager.html)
3. Test configuration files

---

## 🎯 Merge Strategy Recommendations

1. **Review Main Branch First:** Understand what changed in main since branching
2. **Start with Configuration:** Resolve pom.xml and application.properties first
3. **Entity Layer Next:** Ensure database schema is compatible
4. **Repository Layer:** Check for query method conflicts
5. **Service Layer:** CartService likely has major changes - review carefully
6. **Controller Layer:** CartController is new - ensure no endpoint conflicts
7. **Security Config:** Merge carefully to maintain authentication rules
8. **Frontend Last:** Resolve JavaScript and template conflicts

---

## 📚 Additional Resources

- **AI Task Lists:** Located in `/AI_List/` directory
  - `create-prd.md` - PRD generation guidelines
  - `generate-tasks.md` - Task breakdown process
  - `process-task-list.md` - Task processing workflow

- **Project Documentation:**
  - `README.md` - Basic project description
  - `Project Principle/Readme.txt.txt` - Thai instructions for running

---

## 📞 Key Technical Contacts
- **Database:** MySQL on localhost:3306
- **Application:** Spring Boot on localhost:8080
- **Repository:** Aex1010th/Bamee5num

---

## 🔧 Next Steps for Merge

1. **Backup Current Branch:** Ensure all work is committed
2. **Fetch Latest Main:** `git fetch origin main`
3. **Check Conflicts:** `git merge --no-commit --no-ff origin/main`
4. **Use This Document:** Reference entity structures and API endpoints during conflict resolution
5. **Test Thoroughly:** Run integration tests after merge
6. **Verify Database:** Ensure schema migrations are compatible

---

## 🔄 Merge Status Update

**Date:** October 2, 2025

The main branch has been analyzed and compared with the `feature/seperate-customer-cart` branch. Key findings:

- **Main branch is AHEAD** of feature branch by 12 commits
- **Main branch added:** Employee/Manager functionality, Menu management, 6-digit employee login
- **Feature branch has:** Enhanced customer cart with validation and security
- **Critical conflicts identified:** CartController, CartService, pom.xml, CartItem

### Merge Documents Created:
1. ✅ **tasks/prd-merge-feature-branch-to-main.md** - Complete PRD for merge
2. ✅ **tasks/tasks-prd-merge-feature-branch-to-main.md** - Detailed task list (190+ sub-tasks)
3. ✅ **tasks/MERGE_SUMMARY.md** - Quick reference and decision guide

### Recommended Next Steps:
1. Review the MERGE_SUMMARY.md for quick decisions
2. Follow the detailed task list for step-by-step merge
3. Use this overview to understand the feature branch structure
4. Test thoroughly after merge completion

---

**Document Status:** ✅ Ready for merge conflict resolution  
**Generated:** October 2, 2025  
**Last Updated:** October 2, 2025 (Added merge analysis)  
**AI Assistant:** GitHub Copilot
