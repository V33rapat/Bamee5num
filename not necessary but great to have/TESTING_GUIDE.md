# Testing Guide for Tasks 16-21

**For:** Testing Team  
**Created:** October 3, 2025  
**Status:** Ready for Testing  
**Prerequisite:** All merge conflicts resolved, documentation updated

---

## 📋 Overview

This guide covers manual testing for tasks 16.0 through 21.0 of the merge process. All code conflicts have been resolved, and the application should be ready for functional testing.

**Your Mission:** Verify that all features work correctly after the merge, focusing on:
1. Application startup
2. Customer features (registration, login, cart)
3. Employee features (login, dashboard)
4. Manager features (dashboard, menu management)
5. Security and access control

---

## ✅ Prerequisites Checklist

Before starting testing, verify:
- [x] All merge conflicts resolved (no `<<<<<<<` markers)
- [x] Application compiles: `mvn clean compile`
- [x] Database created: `restaurant_db` exists in MySQL
- [x] All 5 tables exist: customers, cart_items, employees, managers, menu_items
- [x] Sample data loaded from `database-setup.sql`
- [x] Documentation updated: README, API_DOCUMENTATION, SETUP_GUIDE

---

## 🚀 Task 16.0: Application Startup Testing

### 16.1 Ensure MySQL is Running

**Windows:**
```powershell
# Check if MySQL service is running
Get-Service | Where-Object {$_.Name -like "*mysql*"}

# If not running, start it
net start MySQL80
```

**Verification:**
- Service status should be "Running"
- Port 3306 should be listening

---

### 16.2 Start the Application

**Method 1 (Maven):**
```bash
cd "Project Principle/demo"
mvn spring-boot:run
```

**Method 2 (PowerShell Script):**
```powershell
cd "Project Principle\demo"
.\run-server.ps1
```

**Expected Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.6)

...
Started DemoApplication in X.XXX seconds
```

**Success Criteria:**
- ✅ No errors in console
- ✅ "Started DemoApplication" message appears
- ✅ Tomcat started on port 8080

---

### 16.3-16.9: Access Pages

Open browser and test these URLs:

| URL | Expected Result | Status |
|-----|-----------------|--------|
| http://localhost:8080 | Landing page loads | [ ] |
| http://localhost:8080/login | Customer login form | [ ] |
| http://localhost:8080/register | Customer registration form | [ ] |
| http://localhost:8080/manager | Manager dashboard | [ ] |
| http://localhost:8080/employee | Employee dashboard | [ ] |

**For each page, verify:**
- Page loads without errors
- CSS styling applied (Tailwind CSS)
- No console errors in browser DevTools (F12)
- Images/icons load correctly

---

## 👤 Task 17.0: Customer Features Testing

### Test Data
Use these credentials or create new:

**Existing Customers (from database-setup.sql):**
- Username: `john_doe`, Password: `password123`
- Username: `jane_smith`, Password: `password456`

**Sample Menu Items:**
- Pad Thai (120.00 THB)
- Tom Yum Soup (80.00 THB)
- Green Curry (150.00 THB)

---

### 17.1 Test Customer Registration

1. Go to: http://localhost:8080/register
2. Fill in form:
   ```
   Name: Test Customer
   Username: testcust123
   Email: test@example.com
   Phone: 0812345678
   Password: Test123!@#
   ```
3. Click "Register"

**Expected:**
- ✅ Validation messages for incorrect input
- ✅ Success message on valid submission
- ✅ Redirect to customer dashboard
- ✅ New customer in database

**Verify in MySQL:**
```sql
SELECT * FROM customers WHERE username = 'testcust123';
```

---

### 17.2 Test Customer Login

1. Go to: http://localhost:8080/login
2. Enter credentials:
   ```
   Username: john_doe
   Password: password123
   ```
3. Click "Login"

**Expected:**
- ✅ Login successful with correct credentials
- ✅ Error message with wrong password
- ✅ Session created (check browser cookies)
- ✅ Redirect to customer dashboard

---

### 17.3 Test Adding Items to Cart

Using the logged-in customer:

1. Browse menu items
2. Click "Add to Cart" for Pad Thai (quantity: 2)
3. Click "Add to Cart" for Tom Yum (quantity: 1)

**Expected:**
- ✅ Items added to cart
- ✅ Cart count updates
- ✅ Total price calculated correctly (2×120 + 1×80 = 320 THB)

**API Test (using cURL):**
```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "customerId": 1,
    "itemName": "Pad Thai",
    "itemPrice": 120.00,
    "quantity": 2
  }'
```

---

### 17.4 Test Viewing Cart

1. Go to cart page or click cart icon
2. Verify all items displayed

**Expected:**
- ✅ All cart items visible
- ✅ Correct quantities
- ✅ Correct prices
- ✅ Correct subtotal

**API Test:**
```bash
curl http://localhost:8080/api/cart/customer/1
```

---

### 17.5 Test Updating Cart Quantity

1. Find cart item (e.g., Pad Thai)
2. Change quantity from 2 to 3
3. Click "Update"

**Expected:**
- ✅ Quantity updated
- ✅ Total recalculated (3×120 = 360)
- ✅ Database updated

**API Test:**
```bash
curl -X PUT http://localhost:8080/api/cart/update/1 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 3,
    "customerId": 1
  }'
```

---

### 17.6 Test Removing Items from Cart

1. Select item to remove
2. Click "Remove" or trash icon
3. Confirm deletion

**Expected:**
- ✅ Item removed from cart
- ✅ Total updated
- ✅ Database updated

**API Test:**
```bash
curl -X DELETE http://localhost:8080/api/cart/remove/1?customerId=1
```

---

### 17.7 Test Clearing Cart

1. Click "Clear Cart" button
2. Confirm action

**Expected:**
- ✅ All items removed
- ✅ Cart shows empty
- ✅ Database reflects empty cart

**API Test:**
```bash
curl -X DELETE http://localhost:8080/api/cart/clear/1
```

---

### 17.8 Test Cart Total Calculation

After adding multiple items:

**Expected:**
- ✅ Subtotal correct (sum of all items)
- ✅ Tax calculated (if implemented)
- ✅ Grand total correct

**API Test:**
```bash
curl http://localhost:8080/api/cart/total/1
```

---

### 17.9 Test Customer Profile View

1. Go to profile/account page
2. View customer details

**Expected:**
- ✅ Name displayed correctly
- ✅ Email displayed
- ✅ Phone displayed
- ✅ Username displayed
- ✅ Created date shown

---

### 17.10 Test Ownership Validation

**Important Security Test:**

1. Login as customer ID 1
2. Try to modify cart of customer ID 2

**Expected:**
- ❌ Should fail with 403 Forbidden
- ❌ Should NOT allow unauthorized access

**API Test:**
```bash
# Login as customer 1, try to access customer 2's cart
curl -X PUT http://localhost:8080/api/cart/update/5 \
  -H "Content-Type: application/json" \
  -b customer1_cookies.txt \
  -d '{
    "quantity": 999,
    "customerId": 2
  }'
```

**Expected Response:** `403 Forbidden` or similar error

---

## 👨‍💼 Task 18.0: Employee Features Testing

### Test Data

**Sample Employees (from database-setup.sql):**
- ID: 1, Name: Somchai Phuket, Position: Server
- ID: 2, Name: Niran Bangkok, Position: Chef

**6-Digit Login Codes:**
These are generated dynamically. Check application logs or use in-memory directory.

---

### 18.1 Test Employee Login

1. Go to: http://localhost:8080/employee
2. Enter 6-digit code (check DataService or logs)
3. Click "Login"

**Expected:**
- ✅ Valid code logs in successfully
- ✅ Invalid code shows error
- ✅ Redirect to employee dashboard

**API Test:**
```bash
curl -X POST http://localhost:8080/api/employees/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginCode": "123456"
  }'
```

---

### 18.2 Test Employee Dashboard Access

**Expected:**
- ✅ Dashboard loads after login
- ✅ Employee name displayed
- ✅ Position shown
- ✅ Order management interface visible

---

### 18.3 Test Viewing Orders (if implemented)

**Expected:**
- ✅ Can view assigned orders
- ✅ Order details displayed correctly

---

### 18.4 Test Employee Logout

**Expected:**
- ✅ Logout button works
- ✅ Session cleared
- ✅ Redirect to login page

---

## 📊 Task 19.0: Manager Features Testing

### 19.1 Test Manager Dashboard Access

1. Go to: http://localhost:8080/manager
2. Verify manager login (if required)

**Expected:**
- ✅ Dashboard loads successfully
- ✅ Navigation tabs visible: Menu, Employees, Reports
- ✅ Statistics displayed (if implemented)

---

### 19.2 Test Menu Item Creation

1. Go to "Menu" tab
2. Click "Add Menu Item"
3. Fill in form:
   ```
   Category: Curry
   Name: Massaman Curry
   Price: 140.00
   Description: Rich and creamy curry with peanuts
   Active: Yes
   ```
4. Click "Save"

**Expected:**
- ✅ Menu item created
- ✅ Appears in menu list
- ✅ Database updated

**Verify in MySQL:**
```sql
SELECT * FROM menu_items WHERE name = 'Massaman Curry';
```

**API Test:**
```bash
curl -X POST http://localhost:8080/api/menu \
  -H "Content-Type: application/json" \
  -d '{
    "category": "Curry",
    "name": "Massaman Curry",
    "price": 140.00,
    "description": "Rich and creamy curry with peanuts",
    "active": true
  }'
```

---

### 19.3 Test Menu Item Update

1. Select existing menu item (e.g., "Pad Thai")
2. Click "Edit"
3. Change price from 120.00 to 130.00
4. Click "Save"

**Expected:**
- ✅ Price updated successfully
- ✅ Changes reflected immediately
- ✅ Database updated

**API Test:**
```bash
curl -X PUT http://localhost:8080/api/menu/1 \
  -H "Content-Type: application/json" \
  -d '{
    "price": 130.00
  }'
```

---

### 19.4 Test Menu Item Deactivation

1. Select menu item
2. Toggle "Active" status to OFF
3. Click "Save"

**Expected:**
- ✅ Item marked as inactive
- ✅ Does not appear in customer menu
- ✅ Still visible in manager dashboard

---

### 19.5 Test Menu Item Deletion

1. Select menu item
2. Click "Delete"
3. Confirm deletion

**Expected:**
- ✅ Confirmation dialog appears
- ✅ Item deleted from database
- ✅ Removed from menu list

**API Test:**
```bash
curl -X DELETE http://localhost:8080/api/menu/1
```

---

### 19.6 Test Employee Management (if implemented)

1. Go to "Employees" tab
2. View employee list

**Expected:**
- ✅ All employees listed
- ✅ Employee details visible
- ✅ Can add/edit/remove employees

---

## 🔒 Task 21.0: Security Verification

### 21.1 Review SecurityConfig.java

Location: `src/main/java/com/restaurant/demo/config/SecurityConfig.java`

**Check for:**
- Password encoding (BCrypt)
- Authentication providers
- Authorization rules

---

### 21.2 Test Password Hashing

**Verify passwords are NOT stored in plaintext:**

```sql
SELECT username, password_hash FROM customers LIMIT 5;
```

**Expected:**
- ✅ password_hash should be hashed (long string starting with `$2a$` or similar)
- ❌ Should NOT be plaintext like "password123"

---

### 21.3 Test Session Security

1. Login as customer
2. Copy session cookie (JSESSIONID)
3. Open incognito window
4. Manually set cookie
5. Try to access protected page

**Expected:**
- ✅ Valid session allows access
- ❌ Invalid/expired session denies access

---

### 21.4 Test CORS Configuration

**From different origin (if applicable):**

```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Origin: http://example.com" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"itemName":"Test","itemPrice":10,"quantity":1}'
```

**Expected:**
- ✅ Requests from `http://localhost:8080` allowed
- ❌ Requests from other origins blocked (unless CORS configured)

---

### 21.5 Test Input Validation

**Test with invalid data:**

```bash
# Invalid price (too high)
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "itemName": "Test",
    "itemPrice": 99999.99,
    "quantity": 1
  }'

# Invalid quantity (negative)
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "itemName": "Test",
    "itemPrice": 50.00,
    "quantity": -5
  }'
```

**Expected:**
- ❌ Should return 400 Bad Request
- ❌ Validation error message
- ❌ Data NOT saved to database

---

### 21.6 Test SQL Injection Prevention

**Try SQL injection in username:**

```bash
curl -X POST http://localhost:8080/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin'\'' OR 1=1--",
    "passwordHash": "anything"
  }'
```

**Expected:**
- ❌ Should NOT allow login
- ❌ Should treat as invalid username
- ✅ JPA should prevent SQL injection

---

### 21.7 Test Unauthorized Access

**Try accessing protected resources without authentication:**

```bash
# Try to access manager dashboard without login
curl http://localhost:8080/manager

# Try to modify cart without session
curl -X DELETE http://localhost:8080/api/cart/remove/1?customerId=1
```

**Expected:**
- ❌ Should redirect to login or return 401 Unauthorized
- ❌ Should NOT allow access to protected resources

---

## 📝 Test Results Template

Use this template to record your findings:

```markdown
## Test Results - [Your Name]
**Date:** [Date]
**Environment:** Windows/macOS/Linux
**Browser:** Chrome/Firefox/Edge [version]

### Task 16.0: Application Startup ✅/❌
- 16.1 MySQL running: ✅
- 16.2 Application starts: ✅
- 16.3 Landing page loads: ✅
- 16.4 Login page loads: ✅
- 16.5 Register page loads: ✅
- 16.6 Manager dashboard loads: ✅
- 16.7 Employee page loads: ✅
- 16.8 Console errors: None
- 16.9 Browser errors: None

### Task 17.0: Customer Features ✅/❌
- 17.1 Registration: ✅
- 17.2 Login: ✅
- 17.3 Add to cart: ✅
- 17.4 View cart: ✅
- 17.5 Update quantity: ✅
- 17.6 Remove item: ✅
- 17.7 Clear cart: ✅
- 17.8 Total calculation: ✅
- 17.9 Profile view: ✅
- 17.10 Ownership validation: ✅

### Task 18.0: Employee Features ✅/❌
- 18.1 Employee login: ⏳ (Not tested - no login codes available)
- 18.2 Dashboard access: ⏳
- 18.3 View orders: ⏳
- 18.4 Logout: ⏳

### Task 19.0: Manager Features ✅/❌
- 19.1 Dashboard access: ✅
- 19.2 Create menu item: ✅
- 19.3 Update menu item: ✅
- 19.4 Deactivate item: ✅
- 19.5 Delete item: ✅
- 19.6 Employee management: ⏳

### Task 21.0: Security ✅/❌
- 21.1 SecurityConfig reviewed: ✅
- 21.2 Password hashing: ✅
- 21.3 Session security: ✅
- 21.4 CORS config: ✅
- 21.5 Input validation: ✅
- 21.6 SQL injection prevention: ✅
- 21.7 Unauthorized access blocked: ✅

### Issues Found:
1. [Issue description]
2. [Issue description]

### Notes:
- [Additional observations]
```

---

## 🐛 If You Find Bugs

1. **Document the issue:**
   - What you did (steps to reproduce)
   - What you expected
   - What actually happened
   - Screenshots if applicable

2. **Check logs:**
   - Application console output
   - Browser console (F12)
   - MySQL error log

3. **Report format:**
```markdown
## Bug Report

**Title:** [Brief description]
**Severity:** Critical/High/Medium/Low
**Task:** 17.3 (Add to cart)

**Steps to Reproduce:**
1. Login as john_doe
2. Click "Add to Cart" for Pad Thai
3. ...

**Expected:** Item should be added to cart
**Actual:** Error 500 Internal Server Error

**Console Output:**
```
[paste error logs]
```

**Screenshots:** [attach if applicable]
```

---

## ✅ Final Checklist

After completing all tests:

- [ ] All pages load without errors
- [ ] Customer registration works
- [ ] Customer login works
- [ ] Cart operations work (add, update, remove, clear)
- [ ] Manager dashboard accessible
- [ ] Menu CRUD operations work
- [ ] Security measures in place
- [ ] No SQL injection vulnerabilities
- [ ] Input validation working
- [ ] Test results documented
- [ ] Bugs reported (if any)

---

## 📞 Support

**If you need help:**
1. Check SETUP_GUIDE.md for installation issues
2. Check API_DOCUMENTATION.md for endpoint details
3. Review MERGE_NOTES.md for architectural decisions
4. Contact: [Your contact info]

---

**Good luck with testing! 🚀**

**Remember:** The goal is to verify that the merge was successful and all features work as expected. Don't hesitate to explore and try edge cases!
