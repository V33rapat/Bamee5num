# Validation Test Scenarios for Menu Management (Task 7.9)

## Client-Side Validation Tests

### Required Field Validation
- [ ] Test 1: Submit form with empty name field
  - Expected: Error modal displays "กรุณาระบุชื่อเมนู"
  
- [ ] Test 2: Submit form with empty price field
  - Expected: Error modal displays "กรุณาระบุราคาที่มากกว่า 0"
  
- [ ] Test 3: Submit form without selecting category
  - Expected: Error modal displays "กรุณาเลือกหมวดหมู่"

### Length Validation Tests
- [ ] Test 4: Submit form with name > 100 characters
  - Expected: Error modal displays "ชื่อเมนูต้องไม่เกิน 100 ตัวอักษร"
  
- [ ] Test 5: Submit form with description > 500 characters
  - Expected: Error modal displays "รายละเอียดเมนูต้องไม่เกิน 500 ตัวอักษร"

### Price Validation Tests
- [ ] Test 6: Submit form with negative price
  - Expected: Error modal displays "กรุณาระบุราคาที่มากกว่า 0"
  
- [ ] Test 7: Submit form with zero price
  - Expected: Error modal displays "กรุณาระบุราคาที่มากกว่า 0"
  
- [ ] Test 8: Submit form with price > 9999.99
  - Expected: Error modal displays "ราคาต้องไม่เกิน 9999.99 บาท"

## Backend Validation Tests

### API Endpoint Tests
- [ ] Test 9: POST valid menu item to /api/manager/menu-items
  - Expected: HTTP 201, menu item created successfully
  
- [ ] Test 10: POST menu item with invalid data (empty name)
  - Expected: HTTP 400, error response with field-level errors
  
- [ ] Test 11: PUT valid menu item to /api/manager/menu-items/{id}
  - Expected: HTTP 200, menu item updated successfully
  
- [ ] Test 12: PUT menu item with non-existent ID
  - Expected: HTTP 404, error response "ไม่พบเมนูที่ต้องการแก้ไข"

### Security Tests
- [ ] Test 13: Access menu management endpoints without authentication
  - Expected: HTTP 403 or redirect to login

## Error Display Tests

### Modal Display Tests
- [ ] Test 14: Verify error modal appears with correct styling
  - Red icon (fa-exclamation-circle)
  - Title "เกิดข้อผิดพลาด" in red
  - Error message displayed correctly
  - "ปิด" button works
  
- [ ] Test 15: Verify error modal closes properly
  - Modal hidden after clicking "ปิด"
  - Form data preserved

### Network Error Tests
- [ ] Test 16: Simulate network timeout
  - Expected: Error modal displays "คำขอหมดเวลา กรุณาลองใหม่อีกครั้ง"
  
- [ ] Test 17: Simulate network connection error
  - Expected: Error modal displays "เกิดข้อผิดพลาดในการเชื่อมต่อกับเซิร์ฟเวอร์"

## Integration Tests

### Add Menu Flow
- [ ] Test 18: Complete add menu flow with valid data
  - Fill all fields correctly
  - Submit form
  - Success modal appears
  - Menu list refreshes with new item
  
- [ ] Test 19: Add menu flow with validation error
  - Fill form with invalid data
  - Submit form
  - Error modal appears
  - Form stays open with data preserved

### Edit Menu Flow
- [ ] Test 20: Complete edit menu flow
  - Click edit button on existing menu item
  - Form pre-populated with current data
  - Modify data
  - Submit form
  - Success modal appears
  - Menu list refreshes with updated data
  
- [ ] Test 21: Edit menu flow with validation error
  - Click edit button
  - Modify data to be invalid
  - Submit form
  - Error modal appears
  - Form stays open with modified data

## Test Results Summary

Date Tested: ___________
Tester: ___________

Passed Tests: ___ / 21
Failed Tests: ___ / 21

Notes:
