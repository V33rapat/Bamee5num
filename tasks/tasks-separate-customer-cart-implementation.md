# Tasks for Separate Customer Cart Implementation

## Relevant Files

- `src/main/resources/static/js/db.js` - Frontend data layer that still uses shared cart array, needs to be updated or removed
- `src/main/resources/static/js/customer.js` - Customer dashboard with cart functionality (already integrated with backend)
- `src/main/java/com/restaurant/demo/controller/CartController.java` - REST API controller for cart operations (implemented)
- `src/main/java/com/restaurant/demo/model/CartItem.java` - CartItem entity model (implemented)
- `src/main/java/com/restaurant/demo/repository/CartItemRepository.java` - JPA repository for cart operations (implemented)
- `src/main/java/com/restaurant/demo/service/CartService.java` - Service layer with hardcoded test data, needs cleanup
- `src/main/resources/application.properties` - Database configuration (H2 configured)
- `src/main/resources/templates/customer.html` - Customer UI template
- `src/test/java/com/restaurant/demo/CartControllerTest.java` - Unit tests for cart controller (needs to be created)
- `src/test/java/com/restaurant/demo/CartServiceTest.java` - Unit tests for cart service (needs to be created)

### Notes

- The system already uses H2 database with JPA/Hibernate instead of localStorage
- Backend REST API is functional with proper customer ID separation
- Frontend is integrated with backend API calls
- Unit tests should be created for cart functionality
- Use `npx jest [optional/path/to/test/file]` to run tests, or `mvn test` for Java tests

## Tasks

- [x] 1.0 Clean Up Legacy Frontend Code
  - [x] 1.1 Remove or update shared cart array in db.js since backend now handles cart storage
  - [x] 1.2 Review and remove localStorage cart functions that are no longer needed
  - [x] 1.3 Update logout function in db.js to not clear cart (since it's now persisted in database)

- [x] 2.0 Enhance Backend Cart Service
  - [x] 2.1 Remove hardcoded test data from CartService constructor
  - [x] 2.2 Integrate CartService with CartController (controller currently bypasses service)
  - [x] 2.3 Add business logic methods in CartService for cart operations
  - [x] 2.4 Add cart clearing functionality (clear all items for a customer)

- [x] 3.0 Improve Cart Quantity Management
  - [x] 3.1 Add quantity increment/decrement endpoints in CartController
  - [x] 3.2 Update frontend to show quantity controls in cart UI
  - [x] 3.3 Modify addToCart logic to increase quantity if item already exists instead of creating duplicate entries
  - [x] 3.4 Add quantity validation (minimum 1, maximum reasonable limit)

- [ ] 4.0 Add Session Management and Security
  - [ ] 4.1 Implement proper session handling in CartController to verify customer identity
  - [ ] 4.2 Add security checks to prevent customers from accessing other customers' carts
  - [ ] 4.3 Add CSRF protection for cart operations
  - [ ] 4.4 Implement proper error handling and HTTP status codes

- [ ] 5.0 Enhance User Experience
  - [ ] 5.1 Add loading indicators for cart operations in frontend
  - [ ] 5.2 Improve error messaging for failed cart operations
  - [ ] 5.3 Add cart item count display in navigation
  - [ ] 5.4 Add confirmation dialogs for removing items from cart
  - [ ] 5.5 Display customer ID or name in cart UI for clarity

- [ ] 6.0 Add Order Processing Integration
  - [ ] 6.1 Create checkout functionality that converts cart to order
  - [ ] 6.2 Clear cart after successful order placement
  - [ ] 6.3 Add order history that shows converted cart items
  - [ ] 6.4 Link existing Order entity with CartItem data structure

- [ ] 7.0 Testing and Quality Assurance
  - [ ] 7.1 Create unit tests for CartController REST endpoints
  - [ ] 7.2 Create unit tests for CartService business logic
  - [ ] 7.3 Create integration tests for cart workflow (add, remove, checkout)
  - [ ] 7.4 Test with multiple customers simultaneously using different browser sessions
  - [ ] 7.5 Test cart persistence across browser sessions and application restarts

- [ ] 8.0 Database and Performance Optimization
  - [ ] 8.1 Add database indexes for customerId in CartItem table for better query performance
  - [ ] 8.2 Implement soft delete for cart items instead of hard delete (optional)
  - [ ] 8.3 Add created/updated timestamps to CartItem entity
  - [ ] 8.4 Consider adding cart expiration mechanism for abandoned carts

- [ ] 9.0 Documentation and Deployment
  - [ ] 9.1 Update API documentation for cart endpoints
  - [ ] 9.2 Create user guide for cart functionality
  - [ ] 9.3 Add database migration scripts if needed for production deployment
  - [ ] 9.4 Update README with cart feature information
