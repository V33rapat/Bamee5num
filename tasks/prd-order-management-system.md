# Product Requirements Document: Order Management System Enhancement

## 1. Introduction/Overview

This feature enhances the restaurant management system by transforming it from a simple cart system into a full order management system. The main changes include:

- **Customer Side**: Converting the payment flow to an order reservation system where customers can place orders (marked as "Pending") and view their pending orders
- **Manager Side**: Adding employee registration functionality to onboard new staff members
- **Employee Side**: Creating a dedicated order management interface where employees can view, process, and complete customer orders

**Problem Solved**: Currently, the system lacks proper order workflow management. Customers can add items to cart but there's no tracking of order status. Employees have no interface to manage incoming orders, and managers cannot easily add new employees to the system.

**Goal**: Create a complete order lifecycle management system from order placement to completion, with appropriate interfaces for each user role.

## 2. Goals

1. Enable customers to place orders and track their pending orders
2. Provide managers with the ability to register new employees with credentials
3. Create an employee interface for order management and processing
4. Implement order status tracking throughout the order lifecycle
5. Add notifications to alert employees of new incoming orders
6. Maintain role-based access control (employees cannot access manager features)

## 3. User Stories

### Customer Stories
- **US-C1**: As a customer, I want to place an order (instead of just paying) so that the restaurant can prepare my food
- **US-C2**: As a customer, I want to view my pending orders so that I know what I've ordered and its status
- **US-C3**: As a customer, I want to see all items in my pending orders along with the total cost

### Manager Stories
- **US-M1**: As a manager, I want to register new employees with their credentials so that they can access the employee system
- **US-M2**: As a manager, I want to keep track of order statistics on my dashboard

### Employee Stories
- **US-E1**: As an employee, I want to see all pending orders from all customers so that I can process them
- **US-E2**: As an employee, I want to see each customer's order details including items and total price
- **US-E3**: As an employee, I want to update order status to "Finish" when completed
- **US-E4**: As an employee, I want to view customer bills regardless of status
- **US-E5**: As an employee, I want to be notified when new orders come in so I don't miss them
- **US-E6**: As an employee, I want to see orders grouped by customer with clear identification

## 4. Functional Requirements

### 4.1 Database Changes

**FR-DB1**: Modify `cart_items` table to add a `status` column
- Column name: `status`
- Type: VARCHAR or ENUM
- Possible values: "Pending", "In Progress", "Cancelled", "Finish"
- Default value: "Pending"
- Status applies to all items in one order (order-level status)

**FR-DB2**: Ensure `cart_items` table properly links to `customers` table via `customer_id` for order grouping

**FR-DB3**: Modify `employees` table to support authentication
- Ensure it has fields for: `username` and `password`
- Keep existing fields: `name`, `position`

### 4.2 Customer Side Features

**FR-C1**: Change button text from "ชำระเงิน" (Pay) to "สั่งจอง" (Order/Reserve)

**FR-C2**: When customer clicks "สั่งจอง":
- Submit the order to the database
- Set all cart items status to "Pending"
- Do NOT process payment
- Clear the current cart UI
- Show confirmation message

**FR-C3**: Create a new page/view for customers to see their pending orders
- Display only orders with status "Pending"
- Show all items grouped by order
- Display: item name, quantity, price per item, subtotal
- Show order total price
- Show order timestamp/ID
- Display current status ("Pending")

**FR-C4**: Add navigation to the pending orders page from the customer interface

### 4.3 Manager Side Features

**FR-M1**: Create employee registration form/interface
- Required fields: Name, Position, Username, Password
- Position should be selectable (e.g., Cleaner, Cashier, Cook, Waiter)
- Username must be unique
- Password should be securely hashed before storage

**FR-M2**: Add validation for employee registration
- Username cannot be duplicate
- All fields are required
- Provide clear error messages

**FR-M3**: Display success message after successful employee registration

**FR-M4**: Update manager dashboard statistics (if applicable)
- Show total pending orders
- Show total completed orders
- Keep existing statistics

### 4.4 Employee Side Features

**FR-E1**: Create employee order management page
- Display all pending orders from all customers
- Show orders in chronological order (newest first or oldest first)
- Group items by customer/order

**FR-E2**: For each order display:
- Customer ID and/or Customer Name
- List of all items with quantities and prices
- Order total price
- Current status
- Action button to update status

**FR-E3**: Display cart_items data with the following columns:
- Customer identification (customer_id, name)
- Item name
- Quantity
- Price
- Subtotal
- Status column (at the end)

**FR-E4**: Allow employees to update order status
- Provide button/dropdown to change status
- Allow changing from "Pending" → "In Progress"
- Allow changing from "In Progress" → "Finish"
- Allow changing to "Cancelled" from any status
- Update all items in the order when status changes

**FR-E5**: Create bill viewing functionality for employees
- Employees can select which orders to view (any status)
- Display complete order details including items and totals
- Show order status and timestamp

**FR-E6**: Implement notification system for new orders
- Alert/notification when a new order is placed (status = "Pending")
- Visual indicator (badge, popup, or sound)
- Clear notification after employee views the order

**FR-E7**: Access control
- Employees can only access employee pages
- Employees CANNOT access manager registration or manager dashboard
- Implement proper session/authentication checks

### 4.5 Authentication & Access Control

**FR-AUTH1**: Maintain existing simple login system for managers

**FR-AUTH2**: Implement employee login system
- Username and password authentication
- Match against employees table

**FR-AUTH3**: Implement role-based access control
- Manager role: Access to manager dashboard and employee registration
- Employee role: Access to order management and bill viewing only
- Customer role: Access to menu, cart, and their pending orders

## 5. Non-Goals (Out of Scope)

1. **Payment Processing**: This feature does NOT implement actual payment gateway integration
2. **Advanced Authentication**: No JWT, OAuth, or complex session management (use simple session)
3. **Real-time Notifications**: No WebSocket or Server-Sent Events (simple polling or page refresh is acceptable)
4. **Order Editing**: Customers cannot edit orders after placing them
5. **Manager Order Management**: Managers do not have a separate order management interface (only employees)
6. **Multi-restaurant Support**: System remains single-restaurant focused
7. **Delivery/Pickup Options**: No delivery address or pickup time selection
8. **Employee Performance Metrics**: No detailed tracking of which employee completed which order
9. **Customer Registration Changes**: No changes to customer registration/login process
10. **Database Migration**: Assume fresh database or manual migration (no automated migration scripts required)

## 6. Design Considerations

### 6.1 User Interface

**Customer Pending Orders Page**:
- Similar design language to existing customer page
- Card-based layout showing each order
- Clear status indicators with color coding (yellow for pending)
- Easy-to-read item list with totals

**Manager Employee Registration**:
- Simple form interface similar to manager registration
- Clear field labels in Thai language
- Success/error feedback messages

**Employee Order Management Page**:
- Dashboard-style layout
- Order cards or table view grouped by customer
- Status badges with color coding:
  - Pending: Yellow/Orange
  - In Progress: Blue
  - Finish: Green
  - Cancelled: Red
- Action buttons prominent and clear
- Notification badge in header or sidebar

### 6.2 Technical Approach

**Frontend**:
- Use existing JavaScript files and HTML templates pattern
- Create new HTML pages: `customer-orders.html`, `employee-orders.html`
- Create new JS files: `customer-orders.js`, `employee-orders.js`, `manager-employee-register.js`
- Update existing files: `customer.js` (change button), `manager.js` (add registration)

**Backend**:
- Create new REST API endpoints for order management
- Create EmployeeController for employee operations
- Create OrderController for order status updates
- Update existing controllers as needed
- Add DTOs for request/response handling

**Database**:
- Use ALTER TABLE to add status column to cart_items
- Ensure proper foreign key relationships
- Add indexes on status and customer_id for performance

## 7. Technical Considerations

### 7.1 Dependencies
- Existing Spring Boot application framework
- Existing database schema (restaurant_db)
- Existing authentication mechanism for managers

### 7.2 API Endpoints (Suggested)

**Customer APIs**:
- `GET /api/customers/{customerId}/pending-orders` - Get pending orders for a customer
- `POST /api/customers/{customerId}/place-order` - Place order (update cart to pending)

**Manager APIs**:
- `POST /api/managers/employees` - Register new employee
- `GET /api/managers/employees` - List all employees

**Employee APIs**:
- `POST /api/employees/login` - Employee login
- `GET /api/employees/orders` - Get all orders (filterable by status)
- `GET /api/employees/orders/{orderId}` - Get specific order details
- `PUT /api/employees/orders/{orderId}/status` - Update order status
- `GET /api/employees/orders/pending/count` - Get count of pending orders (for notifications)

**Cart Items APIs**:
- `GET /api/cart-items/by-customer/{customerId}` - Get cart items by customer
- `PUT /api/cart-items/status` - Update status for multiple items

### 7.3 Security Considerations
- Hash employee passwords using BCrypt or similar
- Implement proper session management
- Validate user roles on every protected endpoint
- Prevent SQL injection through prepared statements/JPA
- Sanitize user inputs

### 7.4 Data Model Considerations

**Status Transitions**:
```
Pending → In Progress → Finish
   ↓
Cancelled (from any state)
```

**Order Grouping**:
- Orders are identified by grouping cart_items by customer_id and timestamp/order_id
- Consider adding an `order_id` or `order_date` field if needed for better grouping

### 7.5 Notification Strategy
- Simple polling approach: Frontend calls pending orders count API every 10-30 seconds
- Compare with previous count to detect new orders
- Show browser notification or visual alert
- Alternative: Page refresh button with manual check

## 8. Success Metrics

1. **Order Placement Success Rate**: 95%+ of customer order placements succeed without errors
2. **Order Processing Time**: Employees can update order status within 5 seconds
3. **System Usability**: Junior developers can understand and implement the requirements with this PRD
4. **Employee Registration**: Managers can successfully register new employees with 100% success rate
5. **Notification Response**: Employees are notified of new orders within 30 seconds of placement
6. **Role Enforcement**: 0% unauthorized access (employees cannot access manager pages)

## 9. Open Questions

1. **Order ID Generation**: Should we add a separate `orders` table with unique order IDs, or continue using customer_id + timestamp grouping for cart_items?

2. **Concurrent Order Handling**: What happens if a customer places a new order while they still have pending orders? Should we allow multiple pending orders per customer?

3. **Order Cancellation**: Who can cancel orders - only employees, or customers too? What happens to cancelled orders (delete or keep for records)?

4. **Historical Data**: How long should completed/cancelled orders remain in the system? Should there be an archive mechanism?

5. **Employee Login Page**: Should employees use the same login page as managers with role detection, or have a separate `/employee-login` page?

6. **Error Handling**: What should happen if order status update fails? Should there be retry mechanism or just show error?

7. **Browser Compatibility**: Should notifications work on all browsers, or can we use modern browser APIs that might not work on older browsers?

8. **Testing Requirements**: Should we create integration tests for the new endpoints, or is manual testing sufficient?

## 10. Implementation Notes for Junior Developers

### Getting Started
1. First, modify the database schema (add status column)
2. Update the entity models (Employee, CartItem)
3. Create new DTOs for API requests/responses
4. Implement backend controllers and services
5. Create frontend HTML pages
6. Implement JavaScript for frontend functionality
7. Test each feature individually
8. Test the complete workflow

### Key Files to Modify/Create

**Backend**:
- `model/Employee.java` - Add username/password fields
- `model/CartItem.java` - Add status field
- `controller/EmployeeController.java` - NEW
- `controller/OrderController.java` - NEW
- `service/EmployeeService.java` - NEW
- `service/OrderService.java` - NEW
- `repository/EmployeeRepository.java` - Update
- `repository/CartItemRepository.java` - Update

**Frontend**:
- `templates/customer-orders.html` - NEW
- `templates/employee-orders.html` - NEW
- `templates/employee-login.html` - NEW (if separate)
- `templates/manager-employee-register.html` - NEW
- `static/js/customer-orders.js` - NEW
- `static/js/employee-orders.js` - NEW
- `static/js/manager-employee-register.js` - NEW
- `static/js/customer.js` - MODIFY (change button)

**Database**:
- Add status column to cart_items table
- Ensure employees table has username and password columns

### Testing Checklist
- [ ] Customer can place order successfully
- [ ] Customer can view their pending orders
- [ ] Manager can register new employee
- [ ] Employee can login with credentials
- [ ] Employee can see all pending orders
- [ ] Employee can update order status to "Finish"
- [ ] Employee receives notification for new orders
- [ ] Employee cannot access manager pages
- [ ] Order totals calculate correctly
- [ ] Multiple customers can place orders independently

---

**Document Version**: 1.0  
**Created**: October 6, 2025  
**Status**: Ready for Implementation
