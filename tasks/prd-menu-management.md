# Product Requirements Document: Menu Management Feature

## Introduction/Overview

The Manager Dashboard currently lacks the ability to add and edit menu items through the web interface. Managers can view the menu but cannot create new items or modify existing ones, requiring direct database access to manage the restaurant's menu. This PRD defines the requirements for implementing a complete menu management system that allows managers to create, edit, and manage menu item availability through the manager dashboard.

**Problem Statement:** Managers cannot add or edit menu items from the dashboard, limiting their ability to update the menu in real-time without technical assistance.

**Goal:** Enable managers to fully manage the restaurant's menu (add, edit, toggle availability) through an intuitive web interface on the manager dashboard.

## Goals

1. **Enable Menu Creation:** Managers can add new menu items with all necessary information (name, price, category, description, availability status)
2. **Enable Menu Editing:** Managers can modify existing menu items' details
3. **Enable Availability Management:** Managers can toggle menu items between active and inactive status (soft delete)
4. **Maintain Data Integrity:** Ensure all menu data follows validation rules (positive prices, required fields)
5. **Provide Clear Feedback:** Show success/error messages in Thai language consistent with the existing UI

## User Stories

1. **As a manager**, I want to add a new menu item so that customers can see and order new dishes we're offering.

2. **As a manager**, I want to edit existing menu items so that I can update prices, descriptions, or fix errors without technical assistance.

3. **As a manager**, I want to mark menu items as unavailable (inactive) so that customers cannot order items that are temporarily out of stock, without permanently deleting them.

4. **As a manager**, I want to reactivate previously inactive menu items so that customers can order them again when they're back in stock.

5. **As a manager**, I want to see clear success messages when I add or edit menu items so that I know my changes were saved successfully.

## Functional Requirements

### Backend Requirements

#### 1. API Endpoints

**1.1** Create a `POST /api/manager/menu-items` endpoint that:
   - Accepts menu item data (name, price, category, description, active status)
   - Validates input data
   - Saves the new menu item to the database
   - Returns the created menu item with HTTP 201 status on success
   - Returns validation errors with HTTP 400 status on failure

**1.2** Create a `PUT /api/manager/menu-items/{id}` endpoint that:
   - Accepts menu item ID as path parameter
   - Accepts updated menu item data in request body
   - Validates input data
   - Updates the existing menu item in the database
   - Returns the updated menu item with HTTP 200 status on success
   - Returns HTTP 404 if menu item not found
   - Returns validation errors with HTTP 400 status on failure

**1.3** Create a `GET /api/manager/menu-items/{id}` endpoint that:
   - Returns a single menu item by ID
   - Returns HTTP 404 if not found
   - Used to populate the edit form

**1.4** Ensure existing `GET /api/manager/menu-items` endpoint returns all menu items (both active and inactive) for management purposes

#### 2. Request/Response DTOs

**2.1** Create `MenuItemRequest` DTO with fields:
   - `name` (String, required, max 100 characters)
   - `price` (Double, required, must be positive)
   - `category` (String, required, allowed values: "Noodles", "Beverages", "Desserts")
   - `description` (String, optional, max 500 characters)
   - `active` (Boolean, required, defaults to true)

**2.2** Create `MenuItemResponse` DTO with fields:
   - `id` (Long)
   - `name` (String)
   - `price` (Double)
   - `category` (String)
   - `description` (String)
   - `active` (Boolean)

#### 3. Validation Rules

**3.1** Name validation:
   - Required field
   - Maximum 100 characters
   - Cannot be empty or only whitespace

**3.2** Price validation:
   - Required field
   - Must be a positive number (> 0)
   - Maximum value: 9999.99

**3.3** Category validation:
   - Required field
   - Must be one of: "Noodles", "Beverages", "Desserts"

**3.4** Description validation:
   - Optional field
   - Maximum 500 characters if provided

**3.5** Active status validation:
   - Required field
   - Must be boolean (true or false)

#### 4. Service Layer

**4.1** Create `MenuItemService` methods:
   - `createMenuItem(MenuItemRequest request)` - Creates and saves new menu item
   - `updateMenuItem(Long id, MenuItemRequest request)` - Updates existing menu item
   - `getMenuItemById(Long id)` - Retrieves single menu item
   - Includes validation logic and business rules

#### 5. Security

**5.1** All menu management endpoints must require manager authentication
**5.2** Use existing security configuration to protect endpoints
**5.3** Return HTTP 403 for unauthorized access attempts

### Frontend Requirements

#### 6. Add Menu Item UI (Modal/Form)

**6.1** Create "เพิ่มเมนู" (Add Menu) button on manager dashboard
   - Styled consistently with existing orange buttons
   - Opens add menu form/modal when clicked

**6.2** Add menu form should include:
   - "ชื่อเมนูใหม่" (Menu Name) - Text input field
   - "ราคา" (Price) - Number input field
   - "ประเภท" (Category) - Dropdown with options: "Noodles", "Beverages", "Desserts"
   - "รายละเอียดเมนู" (Menu Description) - Textarea field
   - "สถานะ" (Status) - Checkbox or toggle for active/inactive (default: active/checked)
   - "ยกเลิก" (Cancel) button - Closes form without saving
   - "เพิ่มเมนู" (Add Menu) button - Submits the form

**6.3** Form validation (client-side):
   - Display error messages in Thai
   - Prevent submission if required fields are empty
   - Show error if price is not a positive number

#### 7. Edit Menu Item UI

**7.1** Add "แก้ไข" (Edit) button/icon for each menu item in the menu list
   - Opens edit form pre-populated with current item data

**7.2** Edit menu form should:
   - Display the same fields as add form
   - Pre-populate all fields with current menu item data
   - Update button text to "บันทึก" (Save)
   - Use `PUT` request to update the item

#### 8. User Feedback

**8.1** Success feedback:
   - Show modal dialog with message "เพิ่มเมนูสำเร็จ" (Menu added successfully) for add operation
   - Show modal dialog with message "แก้ไขเมนูสำเร็จ" (Menu updated successfully) for edit operation
   - Style consistent with existing success dialog (like "โหลดคอมเมนต์สำเร็จ")
   - Auto-refresh menu list after closing success dialog

**8.2** Error feedback:
   - Show alert/modal with error message in Thai
   - Display validation errors clearly
   - Keep form open with entered data so user can correct errors

#### 9. Menu List Display

**9.1** Update menu list to show both active and inactive items
**9.2** Visually distinguish inactive items (e.g., grayed out, with "ไม่พร้อมจำหน่าย" badge)
**9.3** Show edit button for all items (both active and inactive)

## Non-Goals (Out of Scope)

1. **Image Upload:** This version will not include image upload functionality for menu items
2. **Menu Item Deletion:** No permanent delete function; only soft delete (marking as inactive)
3. **Bulk Operations:** No bulk add, edit, or delete operations
4. **Menu Item Ordering:** No drag-and-drop or custom ordering of menu items
5. **Price History:** No tracking of historical prices
6. **Custom Categories:** Managers cannot create new categories; limited to predefined three categories
7. **Nutritional Information:** No fields for calories, allergens, or nutritional data
8. **Multi-language Support:** Menu items stored in Thai only (or single language)

## Design Considerations

### UI/UX Requirements

1. **Consistency:** Follow existing design patterns from the manager dashboard
   - Use existing color scheme (orange buttons, blue/green cards)
   - Match existing modal/dialog styling
   - Use Thai language for all labels and messages

2. **Modal/Form Layout:**
   - Clean, uncluttered form design matching the existing "เพิ่มเมนูใหม่" modal shown in the screenshot
   - Fields should be vertically stacked
   - Clear labels in Thai
   - Sufficient padding and spacing

3. **Responsive Design:**
   - Form should work on desktop screens
   - Follow existing responsive patterns in the application

4. **Accessibility:**
   - Form fields should have proper labels
   - Required fields clearly marked
   - Error messages clearly associated with fields

## Technical Considerations

### Backend Architecture

1. **Existing Structure:** Integrate with existing Spring Boot architecture
   - Use existing packages: `controller`, `service`, `repository`, `dto`, `model`
   - Follow existing naming conventions

2. **Database:** The `menu_items` table already exists with correct schema:
   ```sql
   id BIGINT PRIMARY KEY
   name VARCHAR(100)
   price DOUBLE
   category VARCHAR(50)
   description TEXT
   active BOOLEAN
   ```

3. **Entity:** The `MenuItem` entity already exists with all required fields

4. **Repository:** Ensure `MenuItemRepository` exists with CRUD methods and `findByActiveTrue()` method

5. **Validation:** Use Jakarta Bean Validation annotations (`@NotBlank`, `@Positive`, etc.)

6. **Error Handling:** Use existing exception handling patterns in the application

### Frontend Architecture

1. **Technology Stack:** Plain JavaScript (no framework) as used in existing files
   - File: `manager.js` - Add new functions here
   - Use `fetch()` API for HTTP requests

2. **Integration Points:**
   - `manager.html` - Add UI elements
   - `manager.js` - Add JavaScript functions

3. **API Communication:**
   - Use existing authentication/authorization patterns
   - Handle CSRF tokens if applicable
   - Include proper headers in fetch requests

## Success Metrics

1. **Functionality:** Managers can successfully add and edit menu items without errors
2. **Data Integrity:** All menu items in database pass validation rules
3. **User Satisfaction:** Managers report ease of use and no need for technical assistance to update menu
4. **Error Rate:** Less than 5% of menu add/edit operations result in errors
5. **Response Time:** Menu operations complete within 2 seconds

## Open Questions

1. **File Upload Preparation:** While images are out of scope now, should we design the database/entity to support an image field for future enhancement?
2. **Duplicate Names:** Should the system prevent duplicate menu item names, or allow them (e.g., different sizes of the same item)?
3. **Category Display Order:** Should categories be displayed in a specific order (Noodles, Beverages, Desserts)?
4. **Permissions:** Should all managers have menu management access, or do we need role-based restrictions in the future?
5. **Menu Item Count Limits:** Is there a maximum number of menu items that should be supported?

## Acceptance Criteria

### Add Menu Feature
- [ ] Manager can click "เพิ่มเมนู" button to open add menu form
- [ ] Manager can enter name, price, category, description, and set active status
- [ ] Form validates all required fields before submission
- [ ] Form validates price is positive number
- [ ] Successful submission shows "เพิ่มเมนูสำเร็จ" dialog
- [ ] New menu item appears in menu list after adding
- [ ] Backend creates menu item in database with all fields

### Edit Menu Feature
- [ ] Manager can click "แก้ไข" button on any menu item
- [ ] Edit form opens pre-populated with current item data
- [ ] Manager can modify any field (name, price, category, description, active status)
- [ ] Form validates all changes before submission
- [ ] Successful update shows "แก้ไขเมนูสำเร็จ" dialog
- [ ] Updated data reflects in menu list immediately
- [ ] Backend updates menu item in database

### Validation & Error Handling
- [ ] Empty required fields prevent form submission
- [ ] Zero or negative prices show error message
- [ ] Invalid category selection is prevented
- [ ] Server validation errors display clearly to user
- [ ] Network errors are handled gracefully

### Security
- [ ] Only authenticated managers can access menu management endpoints
- [ ] Unauthorized access attempts return 403 error
- [ ] CSRF protection is implemented if applicable

## Implementation Notes for Junior Developer

### Implementation Order (Suggested)

1. **Backend First:**
   - Create DTOs (`MenuItemRequest`, `MenuItemResponse`)
   - Create service methods in `MenuItemService`
   - Create controller endpoints in `MenuItemController` or new `ManagerMenuController`
   - Test endpoints with Postman or similar tool

2. **Frontend Second:**
   - Add "Add Menu" button and form HTML in `manager.html`
   - Implement add menu JavaScript function in `manager.js`
   - Test adding menu items
   - Add "Edit" buttons and edit form HTML
   - Implement edit menu JavaScript function
   - Test editing menu items

3. **Polish:**
   - Add validation messages
   - Improve error handling
   - Ensure UI consistency
   - Test all edge cases

### Key Files to Modify

**Backend:**
- `src/main/java/com/restaurant/demo/dto/` - Create new DTOs
- `src/main/java/com/restaurant/demo/service/MenuItemService.java` - Add/create methods
- `src/main/java/com/restaurant/demo/controller/ManagerApiController.java` or create `MenuItemController.java` - Add endpoints

**Frontend:**
- `src/main/resources/templates/manager.html` - Add UI elements
- `src/main/resources/static/js/manager.js` - Add JavaScript functions

### Testing Checklist

- [ ] Test adding menu item with valid data
- [ ] Test adding menu item with missing required fields
- [ ] Test adding menu item with negative price
- [ ] Test adding menu item with invalid category
- [ ] Test editing menu item with valid changes
- [ ] Test editing non-existent menu item (should return 404)
- [ ] Test toggling active status
- [ ] Test with unauthenticated user (should be blocked)
- [ ] Verify database persistence
- [ ] Verify menu list updates correctly

---

**Document Version:** 1.0  
**Created:** October 4, 2025  
**Status:** Ready for Implementation
