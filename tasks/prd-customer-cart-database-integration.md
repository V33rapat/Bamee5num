# Product Requirements Document: Customer Cart Database Integration

## Introduction/Overview

This feature implements database connectivity for the customer cart functionality in the restaurant management system. The system will support registered customers who can add items to their shopping cart, with cart data persisted across sessions. The focus is on providing a seamless cart experience for registered customers ordering from the restaurant's menu.

## Goals

1. Implement MySQL database integration for customer and cart data persistence
2. Enable customers to register with basic information (name, email, phone) without profile pictures
3. Provide cart operations (add, remove, update quantities) with database persistence
4. Maintain cart data during user sessions with automatic cleanup
5. Create a foundation for future order processing integration

## User Stories

**As a new customer**, I want to register with my basic information so that I can save my cart and track my orders.

**As a registered customer**, I want to log in and see my previous cart items so that I can continue where I left off.

**As a registered customer**, I want to add menu items to my cart, update quantities, and remove items so that I can customize my order.

**As a registered customer**, I want my cart to be saved across browsing sessions so that I don't lose my selections when I return to the website.

## Functional Requirements

### Database Requirements
1. The system must connect to a MySQL database for data persistence
2. The system must create and manage Customer and CartItem tables
3. The system must handle database connection pooling and error handling

### Customer Management
4. The system must allow new customers to register with name, email, and phone number
5. The system must validate email uniqueness during registration
6. The system must support customer login with email/password authentication
7. All cart operations require customer authentication

### Cart Functionality
8. The system must allow registered customers to add menu items to their cart
9. The system must store cart items with: customer ID, item name, price, quantity, and timestamp
10. The system must allow customers to update item quantities in their cart
11. The system must allow customers to remove items from their cart
12. The system must display current cart contents with total price calculation
13. The system must persist cart data in the database for registered users

### Session Management
14. The system must maintain cart data for registered users across browser sessions
15. The system must require authentication for all cart operations

### API Endpoints
16. The system must provide REST endpoints for customer registration and authentication
17. The system must provide REST endpoints for cart operations (add, update, remove, view)
18. The system must return appropriate HTTP status codes and error messages

## Non-Goals (Out of Scope)

- Guest customer functionality (system requires registration)
- Customer profile pictures or advanced profile management
- Order processing and payment integration (future phase)
- Menu/product management system (managed elsewhere)
- Cart sharing between multiple customers
- Advanced promotional or discount systems
- Multiple carts per customer
- Email notifications or marketing features
- Advanced customer analytics or reporting

## Design Considerations

- Use Spring Boot's standard MVC architecture with Controller-Service-Repository pattern
- Implement RESTful API endpoints following REST conventions
- Use JPA/Hibernate for database operations with proper entity relationships
- Follow consistent JSON response formats for frontend integration
- Implement proper validation for all user inputs
- Use appropriate HTTP status codes for different response scenarios
- Require customer authentication for all cart operations

## Technical Considerations

- **Database**: MySQL 8.0+ with proper indexing on frequently queried fields
- **Framework**: Spring Boot with Spring Data JPA
- **Dependencies**: Add MySQL connector, Spring Data JPA, Spring Web, and validation dependencies
- **Security**: Basic password hashing (BCrypt) for customer authentication
- **Session Management**: Database persistence for registered users only
- **Error Handling**: Implement global exception handling for database and validation errors
- **Performance**: Consider connection pooling and query optimization for cart operations

## Success Metrics

1. **Functionality**: All cart operations (add/remove/update) work correctly with database persistence
2. **Performance**: Cart operations complete within 200ms for 95% of requests
3. **Reliability**: Zero data loss for cart items during normal operations
4. **User Experience**: Seamless cart experience for registered users
5. **Data Integrity**: Proper foreign key relationships and data validation

## Database Schema

### Customer Table
- id (Primary Key, Auto-increment)
- name (VARCHAR, NOT NULL)
- email (VARCHAR, UNIQUE, NOT NULL)
- phone (VARCHAR)
- password_hash (VARCHAR, NOT NULL)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

### CartItem Table
- id (Primary Key, Auto-increment)
- customer_id (Foreign Key, NOT NULL)
- item_name (VARCHAR, NOT NULL)
- item_price (DECIMAL, NOT NULL)
- quantity (INT, NOT NULL)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

## Open Questions

1. Should we implement rate limiting for cart operations to prevent abuse?
2. What should be the maximum number of items allowed in a single cart?
3. Should we log cart activities for analytics purposes?
4. How should we handle concurrent cart modifications from the same user?
