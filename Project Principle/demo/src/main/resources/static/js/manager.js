import { users, menuItems, orders} from "./db.js";

export function setupManagerDashboard() {
    const user = JSON.parse(localStorage.getItem("currentUser"));
    if (!user || user.role !== "manager") {
        alert("กรุณาเข้าสู่ระบบ!");
        window.location.href = "/";
        return;
    }

    document.getElementById("welcomeText").textContent = `สวัสดี, ${user.fullName}`;
    document.getElementById("userNav").classList.remove("hidden");
    document.getElementById("navButtons").classList.add("hidden");
    
    // Add Menu Button
    document.getElementById('addMenuBtn').addEventListener('click', showAddMenuModal);

    // Logout
    document.getElementById("logoutBtn").addEventListener("click", () => {
        localStorage.removeItem("currentUser");
        window.location.href = "/";
    });

    const menuList = document.getElementById("menuManagementList");
    menuList.innerHTML = "";
    menuItems.forEach(item => {
        const div = document.createElement("div");
        div.className = "flex justify-between bg-gray-100 p-4 rounded-lg";
        div.innerHTML = `
            <span>${item.name} - ฿${item.price}</span>
            <button class="bg-red-500 text-white px-3 py-1 rounded delete-menu" data-id="${item.id}">ลบ</button>
        `;
        menuList.appendChild(div);
    });

    document.querySelectorAll(".delete-menu").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = parseInt(btn.dataset.id);
            const idx = menuItems.findIndex(i => i.id === id);
            if(idx !== -1) menuItems.splice(idx,1);
            setupManagerDashboard(); // Refresh
        });
    });

    loadEmployeeManagement();
    switchManagerTab();
    hideAddMenuModal();
    updateManagerStats();
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById('cancelAddMenu').addEventListener('click', hideAddMenuModal);
    document.getElementById('addMenuForm').addEventListener('submit', handleAddMenu);
    setupManagerDashboard();
});

///ต้องกลับมาทดลองใช้งาน ว่าใช้ได้จริงมั้ย
function updateManagerStats() {
    const today = new Date().toDateString();

    // นับออเดอร์วันนี้
    const todayOrders = orders.filter(order =>
        new Date(order.orderTime).toDateString() === today
    );

    document.getElementById('todayOrders').textContent = todayOrders.length;

    // คำนวณรายได้วันนี้
    const todayRevenue = todayOrders.reduce((sum, order) => sum + order.total, 0);
    document.getElementById('todayRevenue').textContent = `฿${todayRevenue}`;

    // ลูกค้าใหม่วันนี้
    const newCustomers = users.filter(user =>
        user.role === 'customer' && new Date(user.createdAt).toDateString() === today
    ).length;
    document.getElementById('newCustomers').textContent = newCustomers;

    // ค่าเฉลี่ย Rating
    const avgRating = feedback.length > 0
        ? (feedback.reduce((sum, fb) => sum + fb.rating, 0) / feedback.length).toFixed(1)
        : '0.0';
    document.getElementById('avgRating').textContent = avgRating;
}

function showAddMenuModal() {
    document.getElementById('addMenuModal').classList.remove('hidden');
}

function hideAddMenuModal() {
    document.getElementById('addMenuModal').classList.add('hidden');
}

function handleAddMenu(e) {
        e.preventDefault();
        const menuData = {
            name: document.getElementById('menuName').value,
            price: parseInt(document.getElementById('menuPrice').value),
            category: document.getElementById('menuCategory').value,
            description: document.getElementById('menuDescription').value
        };
        
        //ใช้ทดสอบก่อน
        menuItems.push(menuData); // แทนที่จะใช้ db.addMenuItem
        setupManagerDashboard(); // refresh หน้า

        hideAddMenuModal();
        alert("เพิ่มเมนูใหม่สำเร็จ!");
        e.target.reset();
        /*
        db.addMenuItem(menuData);
        this.loadMenuManagement();
        this.loadMenu(); // Refresh customer menu if visible
        this.hideAddMenuModal();
        this.showNotification('เพิ่มเมนูใหม่สำเร็จ!', 'success');
        e.target.reset();
        */
}

//แก้ให้สามารถเปลี่ยนไปหน้าจัดการพนักงานได้
function switchManagerTab(tabName) {
    // Update tab buttons
    document.querySelectorAll('.manager-tab').forEach(tab => {
        tab.classList.remove('border-orange-500', 'text-orange-600');
        tab.classList.add('text-gray-500');
    });
    event.target.classList.add('border-orange-500', 'text-orange-600');
    event.target.classList.remove('text-gray-500');

    // Show/hide content
    document.querySelectorAll('.manager-content').forEach(content => {
        content.classList.add('hidden');
    });
    
    switch(tabName) {
        case 'menu':
            document.getElementById('menuManagement').classList.remove('hidden');
            break;
        case 'employees':
            document.getElementById('employeeManagement').classList.remove('hidden');
            break;
        case 'reports':
            document.getElementById('reportsManagement').classList.remove('hidden');
            break;
    }
}

function loadEmployeeManagement() {
        const employeeList = document.getElementById('employeeList');
        employeeList.innerHTML = '';

        const employees = db.users.filter(user => user.role === 'employee' || user.role === 'manager');
        
        employees.forEach(employee => {
            const employeeItem = document.createElement('div');
            employeeItem.className = 'flex items-center justify-between p-4 bg-gray-50 rounded-lg';
            employeeItem.innerHTML = `
                <div>
                    <h5 class="font-semibold">${employee.name}</h5>
                    <p class="text-sm text-gray-600">@${employee.username} - ${employee.role === 'manager' ? 'ผู้จัดการ' : 'พนักงาน'}</p>
                </div>
                <div class="flex items-center space-x-2">
                    <span class="px-2 py-1 text-xs rounded-full ${employee.role === 'manager' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'}">
                        ${employee.role === 'manager' ? 'ผู้จัดการ' : 'พนักงาน'}
                    </span>
                </div>
            `;
            employeeList.appendChild(employeeItem);
        });
    }