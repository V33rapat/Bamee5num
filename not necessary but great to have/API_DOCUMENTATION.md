# API Documentation - Bamee5num Restaurant System

**Version:** 1.0  
**Base URL:** `http://localhost:8080`  
**Last Updated:** October 3, 2025

---

## 📋 Table of Contents
- [Authentication](#authentication)
- [Customer API](#customer-api)
- [Cart API](#cart-api)
- [Menu API](#menu-api)
- [Employee API](#employee-api)
- [Manager API](#manager-api)
- [Error Handling](#error-handling)
- [Status Codes](#status-codes)

---

## 🔐 Authentication

The API uses session-based authentication. After successful login, a session cookie is created and must be included in subsequent requests.

### CORS Configuration
- **Allowed Origins**: `http://localhost:8080`
- **Credentials**: `true` (cookies allowed)
- **Max Age**: 3600 seconds

---

## 👤 Customer API

Base path: `/api/customers`

### Register New Customer

Creates a new customer account.

**Endpoint:** `POST /api/customers/register`

**Request Body:**
```json
{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "phone": "0812345678",
  "passwordHash": "securePassword123"
}
```

**Validation Rules:**
- `name`: 2-50 characters, letters and spaces only
- `username`: 3-20 characters, alphanumeric
- `email`: Valid email format, max 100 characters
- `phone`: 10-15 digits
- `passwordHash`: 8+ characters, mixed case, numbers, special chars

**Success Response (201 Created):**
```json
{
  "id": 1,
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "phone": "0812345678",
  "createdAt": "2025-10-03T10:30:00",
  "updatedAt": "2025-10-03T10:30:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Username already exists",
  "timestamp": "2025-10-03T10:30:00"
}
```

---

### Customer Login

Authenticates customer and creates session.

**Endpoint:** `POST /api/customers/login`

**Request Body:**
```json
{
  "username": "johndoe",
  "passwordHash": "securePassword123"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "phone": "0812345678"
}
```
*Session cookie `JSESSIONID` is set in response headers*

**Error Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password"
}
```

---

### Get Customer Details

Retrieves customer information by ID.

**Endpoint:** `GET /api/customers/{customerId}`

**Path Parameters:**
- `customerId` (required): Customer ID (positive integer)

**Example Request:**
```
GET /api/customers/1
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "phone": "0812345678",
  "createdAt": "2025-10-03T10:30:00",
  "updatedAt": "2025-10-03T10:30:00"
}
```

**Error Response (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 1"
}
```

---

### Check Username Availability

**Endpoint:** `GET /api/customers/check-username?username={username}`

**Query Parameters:**
- `username` (required): Username to check

**Success Response (200 OK):**
```json
{
  "available": true
}
```

---

### Check Email Availability

**Endpoint:** `GET /api/customers/check-email?email={email}`

**Query Parameters:**
- `email` (required): Email to check

**Success Response (200 OK):**
```json
{
  "available": false,
  "message": "Email already registered"
}
```

---

### Validate Password

**Endpoint:** `POST /api/customers/validate-password`

**Request Body:**
```json
{
  "password": "Test123!@#"
}
```

**Success Response (200 OK):**
```json
{
  "valid": true,
  "strength": "strong",
  "suggestions": []
}
```

**Weak Password Response (200 OK):**
```json
{
  "valid": false,
  "strength": "weak",
  "suggestions": [
    "Add uppercase letters",
    "Add special characters",
    "Minimum 8 characters required"
  ]
}
```

---

## 🛒 Cart API

Base path: `/api/cart`

### Add Item to Cart

**Endpoint:** `POST /api/cart/add`

**Request Body:**
```json
{
  "customerId": 1,
  "itemName": "Pad Thai",
  "itemPrice": 120.00,
  "quantity": 2
}
```

**Validation Rules:**
- `customerId`: Required, positive integer
- `itemName`: 1-100 characters
- `itemPrice`: 0.01 - 9999.99 (DECIMAL 6,2)
- `quantity`: 1-100

**Success Response (201 Created):**
```json
{
  "id": 1,
  "customerId": 1,
  "itemName": "Pad Thai",
  "itemPrice": 120.00,
  "quantity": 2,
  "totalPrice": 240.00,
  "createdAt": "2025-10-03T10:30:00",
  "updatedAt": "2025-10-03T10:30:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Validation Error",
  "message": "Item price must be between 0.01 and 9999.99"
}
```

---

### Update Cart Item Quantity

**Endpoint:** `PUT /api/cart/update/{cartItemId}`

**Path Parameters:**
- `cartItemId` (required): Cart item ID

**Request Body:**
```json
{
  "quantity": 3,
  "customerId": 1
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "customerId": 1,
  "itemName": "Pad Thai",
  "itemPrice": 120.00,
  "quantity": 3,
  "totalPrice": 360.00,
  "updatedAt": "2025-10-03T10:35:00"
}
```

**Error Response (403 Forbidden):**
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to modify this cart item"
}
```

---

### Remove Cart Item

**Endpoint:** `DELETE /api/cart/remove/{cartItemId}?customerId={customerId}`

**Path Parameters:**
- `cartItemId` (required): Cart item ID

**Query Parameters:**
- `customerId` (required): Customer ID for ownership validation

**Success Response (200 OK):**
```json
{
  "message": "Cart item removed successfully"
}
```

**Error Response (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Cart item not found"
}
```

---

### Get Customer Cart

Retrieves all cart items for a customer.

**Endpoint:** `GET /api/cart/customer/{customerId}`

**Path Parameters:**
- `customerId` (required): Customer ID

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "customerId": 1,
    "itemName": "Pad Thai",
    "itemPrice": 120.00,
    "quantity": 3,
    "totalPrice": 360.00,
    "createdAt": "2025-10-03T10:30:00"
  },
  {
    "id": 2,
    "customerId": 1,
    "itemName": "Tom Yum Soup",
    "itemPrice": 80.00,
    "quantity": 1,
    "totalPrice": 80.00,
    "createdAt": "2025-10-03T10:32:00"
  }
]
```

---

### Get Cart Item by ID

**Endpoint:** `GET /api/cart/{cartItemId}`

**Success Response (200 OK):**
```json
{
  "id": 1,
  "customerId": 1,
  "itemName": "Pad Thai",
  "itemPrice": 120.00,
  "quantity": 3,
  "totalPrice": 360.00
}
```

---

### Clear Customer Cart

Removes all items from customer's cart.

**Endpoint:** `DELETE /api/cart/clear/{customerId}`

**Success Response (200 OK):**
```json
{
  "message": "Cart cleared successfully",
  "itemsRemoved": 5
}
```

---

### Calculate Cart Total

**Endpoint:** `GET /api/cart/total/{customerId}`

**Success Response (200 OK):**
```json
{
  "customerId": 1,
  "totalItems": 5,
  "subtotal": 840.00,
  "tax": 58.80,
  "grandTotal": 898.80
}
```

---

## 🍽️ Menu API

Base path: `/api/menu`

### Get All Menu Items

**Endpoint:** `GET /api/menu`

**Query Parameters:**
- `active` (optional): Filter by active status (true/false)
- `category` (optional): Filter by category

**Example Request:**
```
GET /api/menu?active=true&category=Noodles
```

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "category": "Noodles",
    "name": "Pad Thai",
    "price": 120.00,
    "description": "Classic Thai stir-fried noodles with shrimp, tofu, and peanuts",
    "active": true,
    "createdAt": "2025-10-01T00:00:00"
  },
  {
    "id": 2,
    "category": "Noodles",
    "name": "Bamee Moo Deng",
    "price": 60.00,
    "description": "Egg noodles with red pork and vegetables",
    "active": true,
    "createdAt": "2025-10-01T00:00:00"
  }
]
```

---

### Get Menu Item by ID

**Endpoint:** `GET /api/menu/{menuItemId}`

**Success Response (200 OK):**
```json
{
  "id": 1,
  "category": "Noodles",
  "name": "Pad Thai",
  "price": 120.00,
  "description": "Classic Thai stir-fried noodles with shrimp, tofu, and peanuts",
  "active": true
}
```

---

### Create Menu Item (Manager Only)

**Endpoint:** `POST /api/menu`

**Request Body:**
```json
{
  "category": "Soup",
  "name": "Tom Kha Gai",
  "price": 90.00,
  "description": "Coconut milk soup with chicken and galangal",
  "active": true
}
```

**Validation Rules:**
- `category`: Required, 1-50 characters
- `name`: Required, 1-100 characters
- `price`: Required, positive decimal (max 2 decimal places)
- `description`: Optional, max 500 characters
- `active`: Default true

**Success Response (201 Created):**
```json
{
  "id": 6,
  "category": "Soup",
  "name": "Tom Kha Gai",
  "price": 90.00,
  "description": "Coconut milk soup with chicken and galangal",
  "active": true,
  "createdAt": "2025-10-03T11:00:00"
}
```

---

### Update Menu Item (Manager Only)

**Endpoint:** `PUT /api/menu/{menuItemId}`

**Request Body:**
```json
{
  "price": 95.00,
  "active": false
}
```
*Supports partial updates*

**Success Response (200 OK):**
```json
{
  "id": 6,
  "category": "Soup",
  "name": "Tom Kha Gai",
  "price": 95.00,
  "description": "Coconut milk soup with chicken and galangal",
  "active": false,
  "updatedAt": "2025-10-03T11:05:00"
}
```

---

### Delete Menu Item (Manager Only)

**Endpoint:** `DELETE /api/menu/{menuItemId}`

**Success Response (200 OK):**
```json
{
  "message": "Menu item deleted successfully"
}
```

---

## 👨‍💼 Employee API

Base path: `/api/employees`

### Employee Login

**Endpoint:** `POST /api/employees/login`

**Request Body:**
```json
{
  "loginCode": "123456"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "name": "Somchai Phuket",
  "position": "Server",
  "loginCode": "123456"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid employee login code"
}
```

---

### Get All Employees (Manager Only)

**Endpoint:** `GET /api/employees`

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Somchai Phuket",
    "position": "Server"
  },
  {
    "id": 2,
    "name": "Niran Bangkok",
    "position": "Chef"
  }
]
```

---

### Create Employee (Manager Only)

**Endpoint:** `POST /api/employees`

**Request Body:**
```json
{
  "name": "Jane Smith",
  "position": "Server"
}
```

**Success Response (201 Created):**
```json
{
  "id": 4,
  "name": "Jane Smith",
  "position": "Server",
  "loginCode": "789012",
  "createdAt": "2025-10-03T11:10:00"
}
```
*Login code is auto-generated (6 digits)*

---

## 📊 Manager API

Base path: `/api/manager`

### Get Sales Report

**Endpoint:** `GET /api/manager/sales-report`

**Query Parameters:**
- `startDate` (optional): Start date (ISO 8601)
- `endDate` (optional): End date (ISO 8601)
- `period` (optional): day, week, month, year

**Example Request:**
```
GET /api/manager/sales-report?period=week
```

**Success Response (200 OK):**
```json
{
  "period": "week",
  "startDate": "2025-09-27",
  "endDate": "2025-10-03",
  "totalOrders": 156,
  "totalRevenue": 45680.00,
  "averageOrderValue": 292.82,
  "topItems": [
    {
      "itemName": "Pad Thai",
      "orderCount": 45,
      "revenue": 5400.00
    },
    {
      "itemName": "Tom Yum Soup",
      "orderCount": 38,
      "revenue": 3040.00
    }
  ]
}
```

---

### Get Manager Dashboard Stats

**Endpoint:** `GET /api/manager/dashboard`

**Success Response (200 OK):**
```json
{
  "todayOrders": 23,
  "todayRevenue": 6740.00,
  "activeEmployees": 8,
  "activeMenuItems": 15,
  "pendingOrders": 5,
  "completedOrdersToday": 18
}
```

---

## ⚠️ Error Handling

### Common Error Response Format

```json
{
  "status": 400,
  "error": "Error Type",
  "message": "Detailed error message",
  "timestamp": "2025-10-03T10:30:00",
  "path": "/api/cart/add",
  "details": {
    "field": "itemPrice",
    "rejectedValue": -10,
    "reason": "must be greater than or equal to 0.01"
  }
}
```

### Validation Errors

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "errors": [
    {
      "field": "username",
      "message": "Username must be between 3 and 20 characters"
    },
    {
      "field": "email",
      "message": "Invalid email format"
    }
  ]
}
```

---

## 📊 Status Codes

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, PUT, DELETE requests |
| 201 | Created | Successful POST request creating new resource |
| 204 | No Content | Successful DELETE with no response body |
| 400 | Bad Request | Validation errors, malformed request |
| 401 | Unauthorized | Authentication required or failed |
| 403 | Forbidden | Authenticated but not authorized for resource |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Resource already exists (duplicate) |
| 500 | Internal Server Error | Unexpected server error |

---

## 🔍 Testing the API

### Using cURL

**Register Customer:**
```bash
curl -X POST http://localhost:8080/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "username": "testuser",
    "email": "test@example.com",
    "phone": "0812345678",
    "passwordHash": "Test123!@#"
  }'
```

**Customer Login:**
```bash
curl -X POST http://localhost:8080/api/customers/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "testuser",
    "passwordHash": "Test123!@#"
  }'
```

**Add to Cart (with session):**
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

### Using Postman

1. Import the API endpoints
2. Set base URL: `http://localhost:8080`
3. Enable cookie management for session handling
4. Use the provided JSON examples

---

## 📝 Notes

- **Session Management**: After login, include session cookie in subsequent requests
- **CORS**: Frontend must run on `http://localhost:8080` or update CORS config
- **Timestamps**: All timestamps in ISO 8601 format (UTC)
- **Decimal Precision**: Prices use DECIMAL(6,2) - max 9999.99
- **Validation**: All input validated with Jakarta Bean Validation
- **Ownership**: Cart operations validate customer ownership to prevent unauthorized access

---

**Last Updated:** October 3, 2025  
**Maintained by:** Bamee5num Development Team