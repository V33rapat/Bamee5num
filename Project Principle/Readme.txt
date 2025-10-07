รันในvs code ด้วยcommand prompt
mvn spring-boot:run เพื่อรัน
ส่วนถ้าต้องติดตั้งเดี๋ยวแนบคลิปให้ แต่ถ้ามีอยู้่แล้วก็รันเลย

==============================
Manager Dashboard Overview
==============================
- หน้า /manager ใช้ localStorage.currentUser เพื่อตรวจ role ผู้ใช้ ต้องตั้งค่า role เป็น "manager" ก่อนเข้าหน้า (เช่นผ่านหน้า login หรือ DevTools)
- ด้านบนแสดงสรุปยอดขายวันนี้ โดยเรียก REST API /api/reports/sales ซึ่งดึงข้อมูลจากตะกร้าสินค้า (CartItem) และ Feedback เพื่อคำนวณ orderCount, revenue, newCustomers, avgRating
- แท็บ "จัดการเมนู" โหลดข้อมูลจาก /api/menuItems และรองรับเพิ่ม/ลบเมนูผ่าน REST (POST/DELETE /api/menuItems)
- แท็บ "จัดการพนักงาน" โหลดรายการจาก /api/employees และสามารถเพิ่ม/แก้ไข/ลบได้ผ่าน modal ซึ่งเรียก POST /api/employees, PUT /api/employees/{id}, DELETE /api/employees/{id}
- มี helper etchCartForUser(userId) ที่เรียก /api/carts/{userId} สำหรับดูตะกร้าของลูกค้าแต่ละคน
- ระบบ seed ตัวอย่าง CartItem/พนักงาน/feedback ด้วย DataInitializer ทำให้สามารถทดสอบ Dashboard ได้ทันทีหลังรัน


กูไม่ไหวแล้วช่วยกูด้วยยย ตอนนี้ยังstackอยู่
Fix CartService and CartItemRepository: 
- Correct JPA repository methods to match entity fields
- Replace findByCustomerId / findByCustomerIdAndItemName with findByCustomer_Id
- Adjust CartService methods to use correct repository methods"