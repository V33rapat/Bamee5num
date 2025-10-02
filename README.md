# Bamee5num Restaurant Management System
**เว็ปไซต์ร้านบะหมี่ห้าน้ำ** - Thai Restaurant Management & E-Commerce Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Database Setup](#database-setup)
- [API Endpoints](#api-endpoints)
- [User Roles](#user-roles)
- [Development](#development)
- [Testing](#testing)
- [Recent Updates](#recent-updates)
- [Contributing](#contributing)

## 🎯 Overview

Bamee5num is a comprehensive restaurant management system built with Spring Boot, designed for Thai restaurants. The system integrates customer-facing e-commerce features with internal employee and manager tools, providing a complete solution for restaurant operations.

**Key Capabilities:**
- 🛒 **Customer Portal**: Browse menu, manage cart, place orders
- 👔 **Employee System**: 6-digit code login, order management
- 📊 **Manager Dashboard**: Menu management, employee oversight, sales reports
- 🔐 **Secure Authentication**: Role-based access control with Spring Security
- 💾 **MySQL Database**: Persistent data storage with JPA/Hibernate

## ✨ Features

### Customer Features
- ✅ User registration and authentication
- ✅ Session-based login management
- ✅ Browse restaurant menu
- ✅ Shopping cart with full CRUD operations
- ✅ Cart ownership validation and security
- ✅ Real-time cart total calculation
- ✅ Input validation (Jakarta Bean Validation)

### Employee Features
- ✅ 6-digit login code authentication
- ✅ Employee-specific dashboard
- ✅ Order management interface
- ✅ In-memory employee directory

### Manager Features
- ✅ Comprehensive management dashboard
- ✅ Menu item CRUD operations (Create, Read, Update, Delete)
- ✅ Employee management
- ✅ Sales reporting and analytics
- ✅ Real-time menu status updates (active/inactive items)
- ✅ Category-based menu organization

### Security Features
- 🔒 Spring Security integration
- 🔒 Password hashing (BCrypt)
- 🔒 Session management
- 🔒 CORS configuration for API access
- 🔒 Customer cart ownership validation

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Security**: Spring Security
- **Validation**: Jakarta Bean Validation (Hibernate Validator)
- **Database**: MySQL 8.0+ with JPA/Hibernate
- **Connection Pool**: HikariCP

### Frontend
- **Template Engine**: Thymeleaf
- **Styling**: Tailwind CSS
- **JavaScript**: ES6 Modules
- **Icons**: Font Awesome

### Build Tool
- **Maven**: Dependency management and build automation

### Dependencies
```xml
- spring-boot-starter-web
- spring-boot-starter-thymeleaf
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- spring-boot-starter-security
- mysql-connector-j (9.4.0)
- spring-boot-devtools (development)
```

## 📁 Project Structure

```
Bamee5num/
├── Project Principle/
│   └── demo/
│       ├── src/main/java/com/restaurant/demo/
│       │   ├── controller/          # REST API Controllers
│       │   │   ├── AuthController.java
│       │   │   ├── CartController.java
│       │   │   ├── CustomerController.java
│       │   │   ├── EmployeeApiController.java
│       │   │   ├── ManagerApiController.java
│       │   │   ├── MenuItemController.java
│       │   │   └── auth/            # Auth DTOs
│       │   ├── model/               # JPA Entities
│       │   │   ├── CartItem.java
│       │   │   ├── Customer.java
│       │   │   ├── Employee.java
│       │   │   ├── Manager.java
│       │   │   ├── MenuItem.java
│       │   │   └── User.java
│       │   ├── repository/          # Data Access Layer
│       │   │   ├── CartItemRepository.java
│       │   │   ├── CustomerRepository.java
│       │   │   └── MenuItemRepo.java
│       │   ├── service/             # Business Logic
│       │   │   ├── CartService.java
│       │   │   ├── CustomerService.java
│       │   │   ├── DataService.java
│       │   │   ├── MenuItemService.java
│       │   │   ├── employee/        # Employee services
│       │   │   ├── manager/         # Manager services
│       │   │   └── user/            # User auth services
│       │   └── DemoApplication.java # Main entry point
│       ├── src/main/resources/
│       │   ├── application.properties
│       │   ├── static/
│       │   │   ├── css/style.css
│       │   │   └── js/              # Frontend scripts
│       │   │       ├── customer.js
│       │   │       ├── employee.js
│       │   │       ├── landing.js
│       │   │       └── manager.js
│       │   └── templates/           # Thymeleaf templates
│       │       ├── index.html
│       │       ├── login.html
│       │       ├── register.html
│       │       ├── customer.html
│       │       ├── employee.html
│       │       └── manager.html
│       ├── database-setup.sql       # Database initialization
│       ├── run-server.ps1          # PowerShell startup script
│       └── pom.xml                 # Maven configuration
├── tasks/                          # Project documentation
├── README.md
└── .gitignore
```

## 🚀 Quick Start

### Prerequisites
- ☕ Java 17 or higher
- 🔧 Maven 3.6+
- 🗄️ MySQL 8.0+
- 💻 Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

### Installation Steps

1. **Clone the repository**
```bash
git clone https://github.com/Aex1010th/Bamee5num.git
cd Bamee5num
```

2. **Set up MySQL Database**
```bash
# Login to MySQL
mysql -u root -p

# Run the database setup script
source "Project Principle/demo/database-setup.sql"
```

3. **Configure Application**

Edit `Project Principle/demo/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_db
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

4. **Build the Project**
```bash
cd "Project Principle/demo"
mvn clean install
```

5. **Run the Application**

**Option A: Using Maven**
```bash
mvn spring-boot:run
```

**Option B: Using PowerShell Script** (Windows)
```powershell
.\run-server.ps1
```

**Option C: Using JAR**
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

6. **Access the Application**

Open your browser and navigate to:
- **Landing Page**: http://localhost:8080
- **Customer Login**: http://localhost:8080/login
- **Customer Registration**: http://localhost:8080/register
- **Manager Dashboard**: http://localhost:8080/manager
- **Employee Login**: http://localhost:8080/employee

## 🗄️ Database Setup

The application uses MySQL with 5 main tables:

### Tables
1. **customers** - Customer account information
2. **cart_items** - Shopping cart items with customer association
3. **employees** - Employee records (base table for inheritance)
4. **managers** - Manager records (extends employees)
5. **menu_items** - Restaurant menu with pricing and categories

### Sample Data
The `database-setup.sql` script includes:
- 2 sample customers (for testing login)
- 3 sample employees (1 manager, 2 regular employees)
- 5 sample Thai menu items (pad thai, tom yum, etc.)

### Database Schema Highlights
- **JOINED inheritance**: Manager extends Employee
- **CASCADE DELETE**: Cart items deleted when customer is removed
- **Validation constraints**: Price ranges, quantity limits
- **Indexes**: Optimized queries on username, email, customer_id

## 🔌 API Endpoints

### Customer API
```
POST   /api/customers/register        - Register new customer
POST   /api/customers/login           - Customer login (creates session)
GET    /api/customers/{id}            - Get customer details
GET    /api/customers/check-username  - Check username availability
GET    /api/customers/check-email     - Check email availability
POST   /api/customers/validate-password - Validate password strength
```

### Cart API
```
POST   /api/cart/add                  - Add item to cart
PUT    /api/cart/update/{id}          - Update cart item quantity
DELETE /api/cart/remove/{id}          - Remove item from cart
GET    /api/cart/customer/{customerId} - Get customer's cart
GET    /api/cart/{id}                 - Get specific cart item
DELETE /api/cart/clear/{customerId}   - Clear entire cart
GET    /api/cart/total/{customerId}   - Calculate cart total
```

### Menu API
```
GET    /api/menu                      - Get all menu items
GET    /api/menu/{id}                 - Get menu item by ID
POST   /api/menu                      - Create new menu item (Manager)
PUT    /api/menu/{id}                 - Update menu item (Manager)
DELETE /api/menu/{id}                 - Delete menu item (Manager)
```

### Employee API
```
POST   /api/employees/login           - Employee login with 6-digit code
GET    /api/employees                 - Get all employees (Manager)
POST   /api/employees                 - Create new employee (Manager)
```

### Authentication API
```
POST   /api/auth/login                - Unified authentication endpoint
POST   /api/auth/logout               - Logout current user
GET    /api/auth/current-user         - Get current authenticated user
```

For detailed API documentation with request/response examples, see [API_DOCUMENTATION.md](./API_DOCUMENTATION.md).

## 👥 User Roles

### Customer
- Self-registration capability
- Browse and order from menu
- Manage personal cart
- View order history

### Employee
- Login with 6-digit employee code
- View and manage assigned orders
- Update order status
- Access employee dashboard

### Manager
- Full system access
- Employee management (hire, terminate)
- Menu management (CRUD operations)
- Sales reports and analytics
- System configuration

## 💻 Development

### Running in Development Mode

Spring Boot DevTools is included for hot reload during development:

```bash
mvn spring-boot:run
```

Changes to Java files, templates, and static resources will trigger automatic restart.

### Development Configuration

Key development settings in `application.properties`:
```properties
# Database
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Disable caching for development
spring.thymeleaf.cache=false
spring.web.resources.cache.period=0

# Connection pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

### Building for Production

```bash
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## 🧪 Testing

### Running Tests
```bash
mvn test
```

### Test Coverage
- Unit tests for service layer
- Integration tests for repositories
- Controller tests with MockMvc
- Validation tests for entities

**Note**: Test implementation is currently in progress. Test files exist but need to be populated with test cases.

## 📝 Recent Updates

### Merge: feature/seperate-customer-cart → main (October 2025)

**What Changed:**
- ✅ Merged enhanced customer cart management from feature branch
- ✅ Integrated employee/manager features from main branch
- ✅ Resolved all merge conflicts in favor of more comprehensive implementations
- ✅ Updated dependencies to Spring Boot 3.5.6
- ✅ Database schema expanded to support all entities

**Key Decisions:**
- **CartController**: Used feature branch (comprehensive validation)
- **CartService**: Used feature branch (customer-centric API)
- **CartItem**: Used feature branch (Jakarta Bean Validation)
- **pom.xml**: Merged dependencies from both branches
- **application.properties**: Used feature branch MySQL config

**Added from Main Branch:**
- Employee authentication system
- Manager dashboard with menu CRUD
- MenuItem entity and repository
- Employee/Manager service layers
- In-memory user directories

For complete merge details, see [tasks/MERGE_CONFLICT_RESOLUTION_LOG.md](./tasks/MERGE_CONFLICT_RESOLUTION_LOG.md).

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Commit Convention
Follow [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `refactor:` - Code refactoring
- `test:` - Test additions or modifications
- `chore:` - Build process or auxiliary tool changes

## 📄 License

This project is developed as part of academic coursework.

## 📧 Contact

**Project Repository**: [https://github.com/Aex1010th/Bamee5num](https://github.com/Aex1010th/Bamee5num)

---

**Built with ❤️ using Spring Boot** 
