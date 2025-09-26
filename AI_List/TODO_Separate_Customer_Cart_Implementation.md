# TODO: แยก ID Customer สำหรับตะกร้าแต่ละคน

## ปัญหาปัจจุบัน
ระบบปัจจุบันใช้ตะกร้าร่วมกัน (shared cart) ทำให้ลูกค้าหลายคนใช้ตะกร้าเดียวกัน ต้องแก้ไขให้แต่ละ customer มีตะกร้าแยกกันตาม customer ID

## วัตถุประสงค์
- แยกตะกร้าสินค้าตาม customer ID
- ป้องกันการปะปนของข้อมูลตะกร้าระหว่างลูกค้า
- เก็บข้อมูลตะกร้าใน localStorage แยกตาม customer

## ขั้นตอนการแก้ไข

### ขั้นตอนที่ 1: แก้ไข db.js - เปลี่ยนโครงสร้างข้อมูลตะกร้า
**ไฟล์:** `src/main/resources/static/js/db.js`

**การเปลี่ยนแปลง:**
- เปลี่ยนจาก `cart = []` เป็น `carts = {}` (object ที่เก็บตะกร้าตาม customer ID)
- เพิ่มฟังก์ชันจัดการตะกร้าแยกตาม customer ID
- เพิ่มฟังก์ชันบันทึก/โหลดตะกร้าจาก localStorage

**ฟังก์ชันที่ต้องเพิ่ม:**
```javascript
// ฟังก์ชันสำหรับจัดการตะกร้าแยกตาม customer ID
export function getCustomerCart(customerId)
export function addItemToCustomerCart(customerId, item)
export function removeItemFromCustomerCart(customerId, itemIndex)
export function clearCustomerCart(customerId)
export function saveCustomerCartToStorage(customerId)
export function loadCustomerCartFromStorage(customerId)
```

### ขั้นตอนที่ 2: แก้ไข customer.js - ปรับฟังก์ชันการจัดการตะกร้า
**ไฟล์:** `src/main/resources/static/js/customer.js`

**การเปลี่ยนแปลง:**
- แก้ไขฟังก์ชัน `addToCart()` ให้รับ customer ID
- แก้ไขฟังก์ชัน `updateCartUI()` ให้แสดงข้อมูลตะกร้าของลูกค้าคนปัจจุบัน
- แก้ไขการบันทึก/โหลดตะกร้าใน localStorage ให้แยกตาม customer ID
- ปรับฟังก์ชันลบสินค้าจากตะกร้าให้ทำงานกับตะกร้าของลูกค้าคนปัจจุบัน

**ฟังก์ชันที่ต้องแก้ไข:**
- `setupCustomerDashboard()` - โหลดตะกร้าของลูกค้าคนปัจจุบัน
- `addToCart(item)` - เพิ่มสินค้าไปยังตะกร้าของลูกค้าคนปัจจุบัน
- `updateCartUI()` - แสดงตะกร้าของลูกค้าคนปัจจุบัน
- การจัดการ event ลบสินค้า

### ขั้นตอนที่ 3: เพิ่ม REST API สำหรับจัดการตะกร้า (Backend)
**ไฟล์:** `src/main/java/com/restaurant/demo/controller/CartController.java` (ไฟล์ใหม่)

**การเพิ่มเติม:**
- สร้าง Controller สำหรับจัดการตะกร้าผ่าน REST API
- เพิ่ม endpoint สำหรับ CRUD operations ของตะกร้า
- เพิ่มการตรวจสอบ customer ID ใน session

**Endpoints ที่ต้องสร้าง:**
- `GET /api/cart/{customerId}` - ดึงตะกร้าของลูกค้า
- `POST /api/cart/{customerId}/add` - เพิ่มสินค้าในตะกร้า
- `DELETE /api/cart/{customerId}/remove/{itemId}` - ลบสินค้าจากตะกร้า
- `DELETE /api/cart/{customerId}/clear` - ล้างตะกร้า

### ขั้นตอนที่ 4: สร้าง Model และ Service Classes
**ไฟล์ใหม่ที่ต้องสร้าง:**
- `src/main/java/com/restaurant/demo/model/CartItem.java`
- `src/main/java/com/restaurant/demo/model/Cart.java`
- `src/main/java/com/restaurant/demo/service/CartService.java`

**คุณสมบัติที่ต้องมี:**
- `CartItem`: id, name, price, quantity, customerId
- `Cart`: customerId, items (List<CartItem>), total
- `CartService`: ฟังก์ชันจัดการ business logic ของตะกร้า

### ขั้นตอนที่ 5: ปรับปรุง Session Management
**ไฟล์:** `src/main/java/com/restaurant/demo/controller/PageController.java`

**การเปลี่ยนแปลง:**
- เพิ่มการจัดการ session สำหรับเก็บ customer ID
- เพิ่ม method สำหรับตรวจสอบการ login
- ป้องกันการเข้าถึงตะกร้าของลูกค้าคนอื่น

### ขั้นตอนที่ 6: เพิ่ม Database Configuration (Optional)
**ไฟล์:** `src/main/resources/application.properties`

**การเพิ่มเติม (หากต้องการใช้ database):**
- เพิ่มการตั้งค่า H2 หรือ MySQL database
- เพิ่ม JPA dependencies ใน pom.xml
- สร้าง Entity classes สำหรับเก็บข้อมูลตะกร้าในฐานข้อมูล

### ขั้นตอนที่ 7: ทดสอบระบบ
**การทดสอบที่ต้องทำ:**
1. ทดสอบ login ด้วย customer ID ต่างๆ
2. ทดสอบเพิ่มสินค้าในตะกร้าแยกตาม customer
3. ทดสอบการ logout และ login ใหม่ ตะกร้าต้องยังคงอยู่
4. ทดสอบ browser ใหม่ ข้อมูลตะกร้าต้องยังคงอยู่
5. ทดสอบหลายๆ customer พร้อมกัน (หลาย browser tab)

### ขั้นตอนที่ 8: ปรับปรุง UI/UX
**ไฟล์:** `src/main/resources/templates/customer.html`

**การปรับปรุง:**
- เพิ่มการแสดง customer ID ใน header
- เพิ่มข้อความแจ้งเตือนเมื่อเปลี่ยน customer
- ปรับปรุง responsive design สำหรับตะกร้า

## ลำดับความสำคัญในการแก้ไข
1. **ขั้นตอนที่ 1** (db.js) - เป็นพื้นฐานสำคัญ
2. **ขั้นตอนที่ 2** (customer.js) - logic หลักของ frontend
3. **ขั้นตอนที่ 3** (CartController) - API backend
4. **ขั้นตอนที่ 4** (Model/Service) - โครงสร้างข้อมูล
5. **ขั้นตอนที่ 7** (Testing) - ทดสอบการทำงาน
6. **ขั้นตอนที่ 5, 6, 8** - ปรับปรุงเพิ่มเติม

## ข้อมูลสำคัญ
- Customer IDs ที่มีอยู่: 1, 2, 3 (ดูจาก db.js)
- ปัจจุบันใช้ localStorage สำหรับเก็บข้อมูล
- ระบบใช้ Spring Boot + Thymeleaf + JavaScript modules
- UI ใช้ TailwindCSS framework

## หมายเหตุ
- ควรทำ backup ก่อนเริ่มแก้ไข
- ทดสอบทีละขั้นตอนเพื่อป้องกันข้อผิดพลาด
- พิจารณาใช้ database แทน localStorage สำหรับข้อมูลที่สำคัญ
