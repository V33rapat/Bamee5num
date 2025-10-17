// employee.js
import { orders, reservations } from "./db.js";

document.addEventListener("DOMContentLoaded", () => {
    setupEmployeeDashboard();
});

export function setupEmployeeDashboard() {
    const user = JSON.parse(localStorage.getItem("currentUser"));

    // ตรวจสอบ role
    if (!user || user.role !== "employee") {
        alert("กรุณาเข้าสู่ระบบ!");
        window.location.href = "/";
        return;
    }

    // แสดงชื่อใน Navbar
    document.getElementById("welcomeText").textContent = `สวัสดี, ${user.fullName}`;
    document.getElementById("userNav").classList.remove("hidden");
    document.getElementById("navButtons").classList.add("hidden");

    // Logout
    document.getElementById("logoutBtn").addEventListener("click", () => {
        localStorage.removeItem("currentUser");
        window.location.href = "/";
    });

    // แสดงออเดอร์
    const ordersDiv = document.getElementById("employeeOrders");
    if (orders.length === 0) {
        ordersDiv.innerHTML = `<p class="text-gray-500">ยังไม่มีออเดอร์</p>`;
    } else {
        ordersDiv.innerHTML = "";
        orders.forEach(order => {
            const div = document.createElement("div");
            div.className = "border-b py-2 flex justify-between items-center";
            div.innerHTML = `<span>${order.itemName} x${order.quantity}</span><span>฿${order.total}</span>`;
            ordersDiv.appendChild(div);
        });
    }

    // แสดงการจอง
    const reservationsDiv = document.getElementById("employeeReservations");
    if (reservations.length === 0) {
        reservationsDiv.innerHTML = `<p class="text-gray-500">ยังไม่มีการจอง</p>`;
    } else {
        reservationsDiv.innerHTML = "";
        reservations.forEach(res => {
            const div = document.createElement("div");
            div.className = "border-b py-2 flex justify-between items-center";
            div.innerHTML = `<span>${res.name} - ${res.date}</span><span>${res.people} คน</span>`;
            reservationsDiv.appendChild(div);
        });
    }
}
