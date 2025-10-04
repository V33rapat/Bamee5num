# Task List: Menu Management Feature Implementation

Based on: `prd-menu-management.md`

## Relevant Files

- `src/main/java/com/restaurant/demo/dto/MenuItemRequest.java` - Request DTO for creating/updating menu items with validation annotations (✅ Created)
- `src/main/java/com/restaurant/demo/dto/MenuItemResponse.java` - Response DTO for menu item data sent to frontend with static factory method (✅ Created)
- `src/main/java/com/restaurant/demo/service/MenuItemService.java` - Service layer methods for menu CRUD operations (✅ Modified with createMenuItem, updateMenuItem, getMenuItemById, getAllMenuItems methods)
- `src/main/java/com/restaurant/demo/exception/MenuItemNotFoundException.java` - Custom exception for menu item not found scenarios (✅ Created)
- `src/main/java/com/restaurant/demo/exception/GlobalExceptionHandler.java` - Global exception handler with MethodArgumentNotValidException and MenuItemNotFoundException handlers (✅ Modified - Added MethodArgumentNotValidException handler for @Valid validation errors)
- `src/main/java/com/restaurant/demo/controller/ManagerApiController.java` - REST endpoints for manager menu operations (✅ Modified with POST, PUT, GET endpoints for menu management)
- `src/main/java/com/restaurant/demo/model/MenuItem.java` - Entity model (already exists, may need validation annotations)
- `src/main/java/com/restaurant/demo/repository/MenuItemRepo.java` - Repository interface (already exists)
- `src/main/resources/templates/manager.html` - Manager dashboard HTML with menu management UI (✅ Modified with updated add menu modal, success modal, and error modal)
- `src/main/resources/static/js/manager.js` - JavaScript for menu CRUD operations (✅ Modified with comprehensive validation, error display functions, and timeout handling)
- `tasks/validation-test-scenarios.md` - Comprehensive test scenarios document for validation testing (✅ Created - 21 test cases)
- `src/test/java/com/restaurant/demo/controller/ManagerApiControllerTest.java` - Integration tests for menu API endpoints
- `src/test/java/com/restaurant/demo/service/MenuItemServiceTest.java` - Unit tests for menu service methods

### Notes

- Unit tests should be placed in the `src/test/java` directory mirroring the source structure
- Use `mvnw test` (or `.\mvnw.cmd test` on Windows) to run all tests
- Use `mvnw test -Dtest=ClassName` to run specific test classes
- The existing `MenuItemService` already has basic methods that will be enhanced
- The `ManagerApiController` follows REST patterns with proper ResponseEntity usage
- Frontend uses plain JavaScript with fetch API, following existing patterns in `manager.js`

## Tasks

- [x] 1.0 Create Backend DTOs for Menu Management
  - [x] 1.1 Create `MenuItemRequest.java` DTO in `src/main/java/com/restaurant/demo/dto/` with fields: name, price, category, description, active
  - [x] 1.2 Add Jakarta validation annotations to `MenuItemRequest`: `@NotBlank` for name, `@Positive` for price, `@NotNull` for category, `@Size` constraints
  - [x] 1.3 Add custom validation for category enum values (Noodles, Beverages, Desserts)
  - [x] 1.4 Create `MenuItemResponse.java` DTO in `src/main/java/com/restaurant/demo/dto/` with fields: id, name, price, category, description, active
  - [x] 1.5 Add constructors, getters, and setters for both DTOs
  - [x] 1.6 Add utility method in `MenuItemResponse` to convert from `MenuItem` entity (static factory method)

- [x] 2.0 Implement Backend Service Layer for Menu CRUD Operations
  - [x] 2.1 Add `createMenuItem(MenuItemRequest request)` method to `MenuItemService` that validates and saves new menu items
  - [x] 2.2 Add `updateMenuItem(Long id, MenuItemRequest request)` method to `MenuItemService` that finds and updates existing menu items
  - [x] 2.3 Add `getMenuItemById(Long id)` method to `MenuItemService` that returns Optional<MenuItem>
  - [x] 2.4 Add `getAllMenuItems()` method to `MenuItemService` that returns all menu items (both active and inactive)
  - [x] 2.5 Add business logic validation in service methods (e.g., price range validation, category validation)
  - [x] 2.6 Add proper exception handling with custom exceptions (e.g., `MenuItemNotFoundException`)
  - [x] 2.7 Add mapper methods to convert between `MenuItem` entity and DTOs

- [x] 3.0 Create Manager API Endpoints for Menu Management
  - [x] 3.1 Add `POST /api/manager/menu-items` endpoint in `ManagerApiController` that accepts `MenuItemRequest` and returns `MenuItemResponse` with HTTP 201
  - [x] 3.2 Add `PUT /api/manager/menu-items/{id}` endpoint in `ManagerApiController` that updates menu item and returns `MenuItemResponse` with HTTP 200
  - [x] 3.3 Add `GET /api/manager/menu-items/{id}` endpoint in `ManagerApiController` that returns single menu item or HTTP 404
  - [x] 3.4 Add `GET /api/manager/menu-items` endpoint in `ManagerApiController` that returns all menu items (active and inactive)
  - [x] 3.5 Add `@Valid` annotation to request parameters to trigger validation
  - [x] 3.6 Add proper HTTP status codes and ResponseEntity handling for success/error cases
  - [x] 3.7 Ensure endpoints are protected by Spring Security (manager role required)

- [x] 4.0 Implement Frontend Add Menu UI and Functionality
  - [x] 4.1 Add "เพิ่มเมนู" (Add Menu) button in `manager.html` within the menu management section
  - [x] 4.2 Create modal HTML structure for add menu form with fields: name, price, category dropdown, description textarea, active checkbox
  - [x] 4.3 Style the modal consistently with existing design (orange buttons, proper spacing, Thai labels)
  - [x] 4.4 Add category dropdown options: "Noodles", "Beverages", "Desserts"
  - [x] 4.5 Add "บันทึก" (Save) and "ยกเลิก" (Cancel) buttons to the modal
  - [x] 4.6 Implement `showAddMenuModal()` function in `manager.js` to display the modal
  - [x] 4.7 Implement `hideAddMenuModal()` function in `manager.js` to close the modal and reset form
  - [x] 4.8 Implement `handleAddMenu(event)` function in `manager.js` to submit form data via POST to `/api/manager/menu-items`
  - [x] 4.9 Add client-side validation before submission (required fields, positive price)
  - [x] 4.10 Show success modal "เพิ่มเมนูสำเร็จ" after successful creation
  - [x] 4.11 Refresh menu list after successful addition
  - [x] 4.12 Handle and display error messages from backend

- [x] 5.0 Implement Frontend Edit Menu UI and Functionality
  - [x] 5.1 Add "แก้ไข" (Edit) button/icon for each menu item in the menu list table in `manager.html`
  - [x] 5.2 Create edit menu modal HTML structure (can reuse add menu modal with dynamic title)
  - [x] 5.3 Implement `showEditMenuModal(menuItemId)` function in `manager.js` that fetches menu item data via GET `/api/manager/menu-items/{id}`
  - [x] 5.4 Pre-populate form fields with current menu item data in the edit modal
  - [x] 5.5 Change modal title to "แก้ไขเมนู" (Edit Menu) when in edit mode
  - [x] 5.6 Implement `handleEditMenu(event, menuItemId)` function in `manager.js` to submit updated data via PUT to `/api/manager/menu-items/{id}`
  - [x] 5.7 Show success modal "แก้ไขเมนูสำเร็จ" after successful update
  - [x] 5.8 Refresh menu list after successful edit
  - [x] 5.9 Handle 404 error if menu item not found
  - [x] 5.10 Handle validation errors and display to user

- [x] 6.0 Enhance Menu Display to Show Active/Inactive Status
  - [x] 6.1 Update `loadMenuItems()` function in `manager.js` to call `/api/manager/menu-items` instead of `/api/menuItems` to get all items
  - [x] 6.2 Modify menu list display to show both active and inactive items
  - [x] 6.3 Add visual indicator for inactive items (e.g., gray background, "ไม่พร้อมจำหน่าย" badge)
  - [x] 6.4 Add CSS styles for inactive menu items (opacity, background color changes)
  - [x] 6.5 Ensure edit button appears for all items regardless of active status
  - [x] 6.6 Add filter/toggle option to show only active or all menu items (optional enhancement)

- [x] 7.0 Add Validation and Error Handling
  - [x] 7.1 Add client-side validation in `manager.js` for required fields before form submission
  - [x] 7.2 Add client-side validation for price (must be positive, max 9999.99)
  - [x] 7.3 Add client-side validation for name length (max 100 characters)
  - [x] 7.4 Add client-side validation for description length (max 500 characters)
  - [x] 7.5 Create error display function in `manager.js` to show validation errors in Thai
  - [x] 7.6 Add backend validation error handling in controller with proper error response format
  - [x] 7.7 Create custom exception handler (@ControllerAdvice) for menu-related exceptions if not exists
  - [x] 7.8 Add network error handling (timeout, connection issues) in frontend
  - [x] 7.9 Test all validation scenarios and ensure proper error messages display

- [ ] 8.0 Write Backend Tests for Menu Management
  - [ ] 8.1 Create `MenuItemServiceTest.java` in `src/test/java/com/restaurant/demo/service/`
  - [ ] 8.2 Write unit test for `createMenuItem()` with valid data
  - [ ] 8.3 Write unit test for `createMenuItem()` with invalid data (null values, negative price)
  - [ ] 8.4 Write unit test for `updateMenuItem()` with valid data
  - [ ] 8.5 Write unit test for `updateMenuItem()` with non-existent ID
  - [ ] 8.6 Write unit test for `getMenuItemById()` success and not found cases
  - [ ] 8.7 Create `ManagerMenuApiControllerTest.java` in `src/test/java/com/restaurant/demo/controller/`
  - [ ] 8.8 Write integration test for POST `/api/manager/menu-items` endpoint with valid request
  - [ ] 8.9 Write integration test for POST endpoint with validation errors (400 response)
  - [ ] 8.10 Write integration test for PUT `/api/manager/menu-items/{id}` endpoint with valid update
  - [ ] 8.11 Write integration test for PUT endpoint with non-existent ID (404 response)
  - [ ] 8.12 Write integration test for GET `/api/manager/menu-items/{id}` endpoint
  - [ ] 8.13 Write integration test for GET `/api/manager/menu-items` endpoint to verify it returns all items
  - [ ] 8.14 Write security test to verify endpoints require manager authentication
  - [ ] 8.15 Run all tests and ensure they pass with `mvnw test`
