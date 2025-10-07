# Setup Guide - Bamee5num Restaurant System

Complete installation and configuration guide for developers and system administrators.

**Version:** 1.0  
**Last Updated:** October 3, 2025

---

## 📋 Table of Contents
1. [System Requirements](#system-requirements)
2. [Pre-Installation](#pre-installation)
3. [Database Setup](#database-setup)
4. [Application Configuration](#application-configuration)
5. [Building the Project](#building-the-project)
6. [Running the Application](#running-the-application)
7. [Verification Steps](#verification-steps)
8. [Troubleshooting](#troubleshooting)
9. [Production Deployment](#production-deployment)
10. [Development Tools](#development-tools)

---

## 💻 System Requirements

### Minimum Requirements
- **Operating System**: Windows 10+, macOS 10.14+, or Linux (Ubuntu 20.04+)
- **Java**: JDK 17 or higher
- **Maven**: 3.6.0 or higher
- **MySQL**: 8.0 or higher
- **RAM**: 4GB minimum, 8GB recommended
- **Disk Space**: 500MB for application + database

### Recommended Development Setup
- **IDE**: IntelliJ IDEA Ultimate, Eclipse IDE, or VS Code with Java extensions
- **Browser**: Chrome, Firefox, or Edge (latest versions)
- **Git**: For version control
- **Postman** or **Insomnia**: For API testing

---

## 🔧 Pre-Installation

### 1. Install Java Development Kit (JDK)

#### Windows
1. Download JDK 17 from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
2. Run the installer
3. Set JAVA_HOME environment variable:
   ```powershell
   # Open PowerShell as Administrator
   [System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "Machine")
   [System.Environment]::SetEnvironmentVariable("Path", $env:Path + ";%JAVA_HOME%\bin", "Machine")
   ```
4. Verify installation:
   ```powershell
   java -version
   ```

#### macOS
```bash
# Using Homebrew
brew install openjdk@17

# Add to PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify
java -version
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk

# Verify
java -version
```

---

### 2. Install Apache Maven

#### Windows
1. Download Maven from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Extract to `C:\Program Files\Apache\maven`
3. Set environment variables:
   ```powershell
   [System.Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\Program Files\Apache\maven", "Machine")
   [System.Environment]::SetEnvironmentVariable("Path", $env:Path + ";%MAVEN_HOME%\bin", "Machine")
   ```
4. Verify:
   ```powershell
   mvn -version
   ```

#### macOS
```bash
brew install maven
mvn -version
```

#### Linux
```bash
sudo apt install maven
mvn -version
```

---

### 3. Install MySQL Server

#### Windows
1. Download MySQL Installer from [https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/)
2. Run installer, choose "Developer Default"
3. Set root password during installation (remember this!)
4. Start MySQL service:
   ```powershell
   net start MySQL80
   ```

#### macOS
```bash
brew install mysql
brew services start mysql

# Secure installation
mysql_secure_installation
```

#### Linux
```bash
sudo apt update
sudo apt install mysql-server

# Start service
sudo systemctl start mysql
sudo systemctl enable mysql

# Secure installation
sudo mysql_secure_installation
```

---

### 4. Clone the Repository

```bash
# Using HTTPS
git clone https://github.com/Aex1010th/Bamee5num.git

# Or using SSH
git clone git@github.com:Aex1010th/Bamee5num.git

# Navigate to project
cd Bamee5num
```

---

## 🗄️ Database Setup

### Step 1: Login to MySQL

```bash
# Login as root
mysql -u root -p
```

Enter your root password when prompted.

---

### Step 2: Run Database Setup Script

#### Method A: From MySQL Command Line

```sql
-- Inside MySQL shell
source 'Project Principle/demo/database-setup.sql';

-- Or specify full path (Windows example)
source 'C:/path/to/Bamee5num/Project Principle/demo/database-setup.sql';
```

#### Method B: Using MySQL Workbench

1. Open MySQL Workbench
2. Connect to your local MySQL instance
3. File → Open SQL Script
4. Navigate to `Project Principle/demo/database-setup.sql`
5. Click Execute (lightning bolt icon)

#### Method C: From Command Line

**Windows PowerShell:**
```powershell
Get-Content "Project Principle\demo\database-setup.sql" | mysql -u root -p
```

**macOS/Linux:**
```bash
mysql -u root -p < "Project Principle/demo/database-setup.sql"
```

---

### Step 3: Verify Database Creation

```sql
-- Login to MySQL
mysql -u root -p

-- Check database exists
SHOW DATABASES;

-- Use the database
USE restaurant_db;

-- Check tables
SHOW TABLES;

-- Should show:
-- +-------------------------+
-- | Tables_in_restaurant_db |
-- +-------------------------+
-- | cart_items              |
-- | customers               |
-- | employees               |
-- | managers                |
-- | menu_items              |
-- +-------------------------+

-- Check sample data
SELECT * FROM customers;
SELECT * FROM menu_items;
```

---

### Step 4: Create Application Database User (Recommended)

For security, create a dedicated user instead of using root:

```sql
-- Create user
CREATE USER 'restaurant_app'@'localhost' IDENTIFIED BY 'your_secure_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON restaurant_db.* TO 'restaurant_app'@'localhost';

-- Flush privileges
FLUSH PRIVILEGES;

-- Verify
SHOW GRANTS FOR 'restaurant_app'@'localhost';
```

---

## ⚙️ Application Configuration

### Step 1: Configure Database Connection

Edit: `Project Principle/demo/src/main/resources/application.properties`

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_db
spring.datasource.username=restaurant_app
spring.datasource.password=your_secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# HikariCP Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000

# Server Configuration
server.port=8080

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Static Resources
spring.web.resources.cache.period=0
spring.web.resources.static-locations=classpath:/static/

# Logging
logging.level.com.restaurant.demo=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

### Step 2: Verify Configuration

Check that all required files exist:

```bash
# Windows PowerShell
Test-Path "Project Principle\demo\src\main\resources\application.properties"
Test-Path "Project Principle\demo\pom.xml"
Test-Path "Project Principle\demo\database-setup.sql"

# macOS/Linux
ls -la "Project Principle/demo/src/main/resources/application.properties"
ls -la "Project Principle/demo/pom.xml"
ls -la "Project Principle/demo/database-setup.sql"
```

---

## 🔨 Building the Project

### Step 1: Navigate to Project Directory

```bash
cd "Project Principle/demo"
```

---

### Step 2: Clean Previous Builds

```bash
mvn clean
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 2.5 s
```

---

### Step 3: Compile the Project

```bash
mvn compile
```

This will:
- Download all dependencies (first time only)
- Compile Java source files
- Process resources

Expected output:
```
[INFO] Changes detected - recompiling the module!
[INFO] Compiling XX source files to target/classes
[INFO] BUILD SUCCESS
```

---

### Step 4: Run Tests (Optional)

```bash
mvn test
```

**Note:** Test implementation is currently in progress. This step may be skipped.

---

### Step 5: Package the Application

```bash
mvn package
```

This creates: `target/demo-0.0.1-SNAPSHOT.jar`

Expected output:
```
[INFO] Building jar: target/demo-0.0.1-SNAPSHOT.jar
[INFO] BUILD SUCCESS
[INFO] Total time: 45.5 s
```

---

## 🚀 Running the Application

### Method 1: Using Maven (Development)

```bash
cd "Project Principle/demo"
mvn spring-boot:run
```

### Method 2: Using PowerShell Script (Windows)

```powershell
cd "Project Principle\demo"
.\run-server.ps1
```

### Method 3: Using JAR File

```bash
cd "Project Principle/demo"
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Method 4: Using IDE

**IntelliJ IDEA:**
1. Open project: `Project Principle/demo/pom.xml`
2. Wait for Maven import
3. Find `DemoApplication.java`
4. Right-click → Run 'DemoApplication'

**Eclipse:**
1. Import → Existing Maven Projects
2. Select `Project Principle/demo`
3. Right-click `DemoApplication.java` → Run As → Java Application

**VS Code:**
1. Open folder: `Project Principle/demo`
2. Install "Spring Boot Extension Pack"
3. Press F5 or use launch configuration

---

### Expected Startup Output

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.6)

2025-10-03T10:30:00.123  INFO --- [main] c.r.demo.DemoApplication : Starting DemoApplication
2025-10-03T10:30:01.234  INFO --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat initialized with port 8080
2025-10-03T10:30:02.345  INFO --- [main] c.r.demo.DemoApplication : Started DemoApplication in 3.5 seconds
```

---

## ✅ Verification Steps

### Step 1: Check Application is Running

Open browser and navigate to:
```
http://localhost:8080
```

You should see the landing page.

---

### Step 2: Test Customer Registration

1. Go to: `http://localhost:8080/register`
2. Fill in the form:
   - Name: Test User
   - Username: testuser
   - Email: test@example.com
   - Phone: 0812345678
   - Password: Test123!@#
3. Click Register
4. Should redirect to customer dashboard

---

### Step 3: Test Customer Login

1. Go to: `http://localhost:8080/login`
2. Use credentials from database:
   - Username: `john_doe` or `jane_smith`
   - Password: Check `database-setup.sql` for hashed passwords
3. Should successfully login

---

### Step 4: Test Manager Dashboard

1. Go to: `http://localhost:8080/manager`
2. Verify dashboard loads with:
   - Menu items list
   - Employee management tab
   - Reports section

---

### Step 5: Test API Endpoints

**Using cURL:**

```bash
# Check health
curl http://localhost:8080/api/menu

# Register customer
curl -X POST http://localhost:8080/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "API Test",
    "username": "apitest",
    "email": "api@test.com",
    "phone": "0899999999",
    "passwordHash": "Test123!@#"
  }'
```

**Using Browser:**
- Menu API: http://localhost:8080/api/menu
- Sample customer: http://localhost:8080/api/customers/1

---

### Step 6: Check Database Connectivity

```sql
-- Login to MySQL
mysql -u root -p

USE restaurant_db;

-- Check if new data was added
SELECT * FROM customers ORDER BY created_at DESC LIMIT 5;

-- Check cart items
SELECT c.username, ci.item_name, ci.quantity 
FROM customers c 
JOIN cart_items ci ON c.id = ci.customer_id;
```

---

## 🔧 Troubleshooting

### Issue 1: MySQL Connection Failed

**Error:**
```
Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago
```

**Solution:**
```bash
# Check MySQL is running
# Windows:
net start | findstr MySQL

# macOS/Linux:
systemctl status mysql

# Verify port 3306 is open
netstat -an | findstr 3306

# Check credentials in application.properties match MySQL user
```

---

### Issue 2: Port 8080 Already in Use

**Error:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solution:**

**Windows:**
```powershell
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID)
taskkill /PID <PID> /F

# Or change port in application.properties
server.port=8081
```

**macOS/Linux:**
```bash
# Find and kill process
lsof -i :8080
kill -9 <PID>
```

---

### Issue 3: Maven Build Fails

**Error:**
```
Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

**Solution:**
```bash
# Clear Maven cache
mvn clean
rm -rf ~/.m2/repository

# Or Windows:
Remove-Item -Recurse -Force $env:USERPROFILE\.m2\repository

# Rebuild
mvn clean install -U
```

---

### Issue 4: Database Schema Mismatch

**Error:**
```
Table 'restaurant_db.customers' doesn't exist
```

**Solution:**
```sql
-- Drop and recreate database
DROP DATABASE IF EXISTS restaurant_db;
CREATE DATABASE restaurant_db;
USE restaurant_db;

-- Re-run setup script
source 'Project Principle/demo/database-setup.sql';
```

---

### Issue 5: ClassNotFoundException

**Error:**
```
java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver
```

**Solution:**
```bash
# Check pom.xml has MySQL connector
# Should contain:
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.4.0</version>
</dependency>

# Reimport Maven dependencies
mvn clean install -U
```

---

### Issue 6: Static Resources Not Loading

**Symptom:** CSS/JS files return 404

**Solution:**
```properties
# Check application.properties has:
spring.web.resources.static-locations=classpath:/static/

# Verify file structure:
src/main/resources/static/
    css/style.css
    js/customer.js
    
# Clear browser cache (Ctrl+Shift+Delete)
```

---

## 🚢 Production Deployment

### Step 1: Build Production JAR

```bash
mvn clean package -DskipTests
```

Creates: `target/demo-0.0.1-SNAPSHOT.jar` (~40MB)

---

### Step 2: Configure Production Database

Create `application-prod.properties`:

```properties
spring.datasource.url=jdbc:mysql://your-production-host:3306/restaurant_db
spring.datasource.username=prod_user
spring.datasource.password=${DB_PASSWORD}

# Disable dev tools
spring.devtools.restart.enabled=false

# Production optimizations
spring.jpa.show-sql=false
spring.thymeleaf.cache=true
logging.level.com.restaurant.demo=INFO
```

---

### Step 3: Run in Production

```bash
# Set environment variable
export DB_PASSWORD=secure_production_password

# Run with production profile
java -jar -Dspring.profiles.active=prod target/demo-0.0.1-SNAPSHOT.jar
```

---

### Step 4: Use Process Manager (Linux)

**Using systemd:**

Create `/etc/systemd/system/bamee5num.service`:

```ini
[Unit]
Description=Bamee5num Restaurant Application
After=mysql.service

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/bamee5num
ExecStart=/usr/bin/java -jar /opt/bamee5num/demo-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable bamee5num
sudo systemctl start bamee5num
sudo systemctl status bamee5num
```

---

## 🛠️ Development Tools

### Recommended IDE Extensions

**IntelliJ IDEA:**
- Spring Boot
- Lombok (if added later)
- Database Tools

**VS Code:**
- Spring Boot Extension Pack
- Java Extension Pack
- MySQL (by Jun Han)
- Thunder Client (API testing)

---

### Useful Maven Commands

```bash
# Clean build
mvn clean

# Compile only
mvn compile

# Run tests
mvn test

# Package JAR
mvn package

# Skip tests
mvn package -DskipTests

# Update dependencies
mvn clean install -U

# Show dependency tree
mvn dependency:tree

# Run specific test
mvn test -Dtest=CustomerServiceTest
```

---

### Database Management Tools

1. **MySQL Workbench** - GUI for database management
2. **DBeaver** - Universal database tool
3. **phpMyAdmin** - Web-based MySQL admin
4. **DataGrip** - JetBrains database IDE

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)

---

## ✅ Setup Checklist

Use this checklist to verify your setup:

- [ ] JDK 17+ installed and in PATH
- [ ] Maven 3.6+ installed and in PATH
- [ ] MySQL 8.0+ installed and running
- [ ] Database `restaurant_db` created
- [ ] All 5 tables exist (customers, cart_items, employees, managers, menu_items)
- [ ] Sample data loaded
- [ ] `application.properties` configured correctly
- [ ] Project compiles successfully (`mvn compile`)
- [ ] Application starts without errors
- [ ] Can access http://localhost:8080
- [ ] Customer registration works
- [ ] Customer login works
- [ ] Manager dashboard loads
- [ ] API endpoints respond correctly

---

**Setup Complete! 🎉**

If you encounter any issues not covered in this guide, please check:
1. Application logs in terminal
2. MySQL error log
3. `tasks/MERGE_CONFLICT_RESOLUTION_LOG.md` for known issues

**Last Updated:** October 3, 2025  
**Maintained by:** Bamee5num Development Team