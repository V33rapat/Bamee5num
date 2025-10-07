# 🧪 Quick Start Testing Guide

## Before You Start

### 1. Run Database Migrations (if using existing database)

```sql
-- Connect to your MySQL database
USE restaurant_db;

-- Fix orders table constraint
ALTER TABLE orders DROP CHECK orders_chk_1;
ALTER TABLE orders ADD CONSTRAINT orders_chk_status 
    CHECK (status IN ('Pending', 'In Progress', 'Finish', 'Cancelled'));

-- Fix cart_items constraint
ALTER TABLE cart_items DROP CHECK cart_items_chk_1;
ALTER TABLE cart_items ADD CONSTRAINT cart_items_chk_status 
    CHECK (status IN ('Pending'));
```

### 2. Start the Application

```powershell
cd "d:\FireFox Files\Marinara's Essentials\Bamee5num-fix_cart_and_order\Project Principle\demo"
.\mvnw.cmd spring-boot:run
```

Wait for the message: "Started DemoApplication in X.XXX seconds"

### 3. Open Your Browser

Navigate to: http://localhost:8080

---

## 🎯 5-Minute Test Scenario

### **Scenario: Complete Order Lifecycle**

#### **Step 1: Customer Places Order (2 minutes)**

1. **Register/Login as Customer**
   - Go to http://localhost:8080/register
   - Create account: name="Test User", username="testuser", email="test@test.com"
   - Or login if you already have an account

2. **Add Items to Cart**
   - Click "ใส่ตะกร้า" (Add to Cart) on 2-3 menu items
   - Click shopping cart icon (top right)
   - Verify items appear with correct prices

3. **Place Order**
   - Click "สั่งจอง" (Place Order) button
   - ✅ **EXPECTED**: Success message appears
   - ✅ **EXPECTED**: Cart becomes empty
   - ❌ **BUG IF**: Cart still shows items after order

4. **Check Order History**
   - Click "คำสั่งซื้อของฉัน" (My Orders) in nav menu
   - ✅ **EXPECTED**: Your order appears with unique Order ID
   - ✅ **EXPECTED**: Status shows "Pending" (yellow badge)
   - Note the Order ID number (e.g., #1, #2, etc.)

#### **Step 2: Employee Manages Order (2 minutes)**

1. **Login as Employee**
   - Go to http://localhost:8080/employee-login
   - Default credentials: username="alice_manager", password="password123"
   - (Or register an employee from manager dashboard first)

2. **View Pending Orders**
   - ✅ **EXPECTED**: Dashboard shows your order in "Pending" section
   - ✅ **EXPECTED**: Shows correct Order ID (matches customer's order)
   - ✅ **EXPECTED**: Shows customer name
   - ✅ **EXPECTED**: Shows correct date/time
   - ❌ **BUG IF**: Order doesn't appear
   - ❌ **BUG IF**: Date shows "ไม่ระบุวันที่" (No date)

3. **Update Order Status**
   - Click "▶️ เริ่มดำเนินการ" (Start) button
   - ✅ **EXPECTED**: Confirmation dialog appears
   - Click "OK"
   - ✅ **EXPECTED**: Order moves to "In Progress" section
   - ✅ **EXPECTED**: Badge turns blue
   - ✅ **EXPECTED**: Only "Finish" and "Cancel" buttons show

4. **Complete Order**
   - Click "✅ เสร็จสิ้น" (Finish) button
   - ✅ **EXPECTED**: Order moves to "Finish" section
   - ✅ **EXPECTED**: Badge turns green
   - ✅ **EXPECTED**: No action buttons (shows "ไม่สามารถเปลี่ยนสถานะได้")

5. **View Bill**
   - Click "📄 ดูใบเสร็จ" (View Receipt) button
   - ✅ **EXPECTED**: Modal shows order details
   - ✅ **EXPECTED**: Shows correct Order ID
   - ✅ **EXPECTED**: Shows customer name
   - ✅ **EXPECTED**: Shows all items with correct totals

#### **Step 3: Customer Verifies (1 minute)**

1. **Switch back to customer browser tab**
2. **Refresh "My Orders" page**
   - ✅ **EXPECTED**: Order status updated to "Finish" (green)
   - ✅ **EXPECTED**: Same Order ID
   - ✅ **EXPECTED**: Total matches what you ordered

---

## 🔍 Database Verification (Optional)

```sql
-- Check latest order
SELECT o.id, o.customer_id, c.name as customer_name, o.status, o.total_amount, o.created_at
FROM orders o
JOIN customers c ON o.customer_id = c.id
ORDER BY o.id DESC
LIMIT 1;

-- Check order items
SELECT oi.id, oi.order_id, oi.item_name, oi.quantity, oi.item_price, oi.total
FROM order_items oi
WHERE oi.order_id = <YOUR_ORDER_ID>;

-- Verify cart is empty
SELECT * FROM cart_items WHERE customer_id = <YOUR_CUSTOMER_ID>;
-- Should return 0 rows after placing order!
```

---

## ✅ Success Checklist

- [ ] Customer can place order
- [ ] Cart clears after order placement
- [ ] Order appears on employee dashboard
- [ ] Order shows correct unique ID (not customer ID)
- [ ] Order date displays correctly
- [ ] Employee can update status (Pending → In Progress → Finish)
- [ ] Status buttons follow correct flow
- [ ] Customer sees updated status
- [ ] Bill modal shows correct information
- [ ] Database tables are populated correctly

---

## ❌ Known Issues to Look For

### **Issue 1: Orders Not Appearing on Employee Dashboard**
**Symptoms**: Customer places order but employee sees nothing  
**Cause**: Database constraint blocking order creation  
**Fix**: Run migration scripts above

### **Issue 2: Date Shows "ไม่ระบุวันที่"**
**Symptoms**: Order date displays as "No date specified"  
**Cause**: Frontend not reading `orderDate` field  
**Fix**: Already fixed in code, but verify by checking browser console for errors

### **Issue 3: Status Update Uses Customer ID**
**Symptoms**: Multiple orders for same customer all update together  
**Cause**: Old code still using customer ID instead of order ID  
**Fix**: Already fixed, but verify each order updates independently

### **Issue 4: Cart Items Accumulate**
**Symptoms**: Cart items stay after placing order  
**Cause**: cartItemRepository.deleteAll() not being called  
**Fix**: Already fixed, verify cart is empty after order

---

## 🐛 If Something Fails

### **1. Check Browser Console**
- Open Developer Tools (F12)
- Look for red errors in Console tab
- Common errors:
  - `404 Not Found` → Endpoint missing or typo
  - `500 Internal Server Error` → Backend error (check logs)
  - `400 Bad Request` → Validation error

### **2. Check Spring Boot Logs**
Look for errors in the terminal where you ran `mvnw spring-boot:run`:
- `ConstraintViolationException` → Database constraint issue (run migrations)
- `NullPointerException` → Missing data
- `IllegalArgumentException` → Invalid status transition

### **3. Check Database**
```sql
-- See all tables
SHOW TABLES;

-- Check if orders table exists
DESC orders;

-- Check if order_items table exists
DESC order_items;

-- See constraint definitions
SHOW CREATE TABLE orders;
SHOW CREATE TABLE cart_items;
```

### **4. Restart Application**
Sometimes a restart helps:
```powershell
# Press Ctrl+C to stop
# Then restart
.\mvnw.cmd spring-boot:run
```

---

## 📊 Expected Results Summary

| Test | Expected Result | Time |
|------|----------------|------|
| Customer places order | ✅ Success message, cart clears | 30s |
| Order appears on employee dashboard | ✅ Shows with correct ID | 10s |
| Employee updates status | ✅ Transitions correctly | 20s |
| Customer sees updated status | ✅ Status badge matches | 10s |
| Database populated correctly | ✅ Orders & items saved | 30s |

**Total Test Time**: ~5 minutes

---

## 🎉 Success!

If all checks pass, the implementation is working correctly!

**Next Steps**:
1. Test with multiple customers
2. Test with multiple employees
3. Test edge cases (empty cart, cancelled orders, etc.)
4. Deploy to production (after thorough testing)

---

**Last Updated**: October 8, 2025  
**Implementation**: Option A - Complete Order System  
**Status**: ✅ Ready for Testing

