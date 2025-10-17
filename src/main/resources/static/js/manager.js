let employeeModal;
let employeeForm;
let employeeModalTitle;
let employeeNameInput;
let employeePositionInput;
let employeeUsernameInput;
let employeePasswordInput;
let currentMenuFilter = 'all'; // Track current filter: 'all' or 'active'
let salesChart = null; // To hold the Chart.js instance

// Utility functions to get CSRF token and header from meta tags
function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]')?.content;
}

function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]')?.content;
}

// Utility function to add timeout to fetch requests
async function fetchWithTimeout(url, options = {}, timeout = 10000) {
    const controller = new AbortController();
    const id = setTimeout(() => controller.abort(), timeout);
    
    try {
        const response = await fetch(url, {
            ...options,
            signal: controller.signal
        });
        clearTimeout(id);
        return response;
    } catch (error) {
        clearTimeout(id);
        if (error.name === 'AbortError') {
            throw new Error('คำขอหมดเวลา กรุณาลองใหม่อีกครั้ง');
        }
        throw error;
    }
}

// Parse a Response safely and provide helpful error text when it's not JSON
async function parseJsonSafe(response) {
    const text = await response.text();
    const contentType = (response.headers.get('content-type') || '').toLowerCase();
    const trimmed = text.trim();

    // If content-type indicates JSON or the body looks like JSON, try to parse
    if (contentType.includes('application/json') || trimmed.startsWith('{') || trimmed.startsWith('[')) {
        try {
            return JSON.parse(trimmed);
        } catch (e) {
            // Include a snippet of the invalid JSON to help debugging
            throw new Error('Invalid JSON response from ' + response.url + ': ' + (trimmed.length > 300 ? trimmed.slice(0, 300) + '...' : trimmed));
        }
    }

    // Not JSON: return a helpful error including the status and beginning of body
    throw new Error('Expected JSON response but got status ' + response.status + ' from ' + response.url + '. Response preview: ' + (trimmed.length > 300 ? trimmed.slice(0, 300) + '...' : trimmed));
}

// Main setup function for manager dashboard
export async function setupManagerDashboard() {
    // Check if manager is authenticated via server session
    // If not authenticated, user would be redirected by Spring Security or controller
    // We can verify by checking if the welcome text has a username
    const welcomeSpan = document.querySelector('#welcomeText span[th\\:text]');
    
    // Hide the nav buttons since we're authenticated
    const navButtons = document.getElementById("navButtons");
    if (navButtons) {
        navButtons.classList.add("hidden");
    }

    document.getElementById('addMenuBtn').addEventListener('click', showAddMenuModal);

    const addEmployeeBtn = document.getElementById('addEmployeeBtn');
    if (addEmployeeBtn) {
        addEmployeeBtn.addEventListener('click', () => openEmployeeDialog());
    }

    await loadMenuItems();
    await loadEmployeeManagement();
    await updateManagerStats();
    initManagerTabs();
    hideAddMenuModal();
}

// Event listeners for DOMContentLoaded
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById('cancelAddMenu').addEventListener('click', hideAddMenuModal);
    document.getElementById('addMenuForm').addEventListener('submit', handleAddMenu);

    const closeSuccessBtn = document.getElementById('closeSuccessModal');
    if (closeSuccessBtn) {
        closeSuccessBtn.addEventListener('click', hideSuccessModal);
    }

    const closeErrorBtn = document.getElementById('closeErrorModal');
    if (closeErrorBtn) {
        closeErrorBtn.addEventListener('click', hideErrorModal);
    }

    // Filter button event listeners
    const filterAllBtn = document.getElementById('filterAllBtn');
    const filterActiveBtn = document.getElementById('filterActiveBtn');
    
    if (filterAllBtn) {
        filterAllBtn.addEventListener('click', () => {
            currentMenuFilter = 'all';
            filterAllBtn.classList.add('active');
            filterActiveBtn.classList.remove('active');
            loadMenuItems();
        });
    }
    
    if (filterActiveBtn) {
        filterActiveBtn.addEventListener('click', () => {
            currentMenuFilter = 'active';
            filterActiveBtn.classList.add('active');
            filterAllBtn.classList.remove('active');
            loadMenuItems();
        });
    }

    employeeModal = document.getElementById('employeeModal');
    employeeForm = document.getElementById('employeeForm');
    employeeModalTitle = document.getElementById('employeeModalTitle');
    employeeNameInput = document.getElementById('employeeName');
    employeePositionInput = document.getElementById('employeePosition');
    employeeUsernameInput = document.getElementById('employeeUsername');
    employeePasswordInput = document.getElementById('employeePassword');
    const cancelEmployeeBtn = document.getElementById('cancelEmployee');

    if (employeeForm) {
        employeeForm.addEventListener('submit', handleEmployeeSubmit);
    }
    if (cancelEmployeeBtn) {
        cancelEmployeeBtn.addEventListener('click', hideEmployeeModal);
    }

    setupManagerDashboard();
});

// Setup report controls after DOM is ready: populate year options and attach click handler
document.addEventListener('DOMContentLoaded', () => {
    const monthSelect = document.getElementById('reportMonth');
    const yearSelect = document.getElementById('reportYear');
    const loadReportBtn = document.getElementById('loadReportBtn');

    if (yearSelect) {
        const currentYear = new Date().getFullYear();
        // populate recent 6 years
        for (let y = currentYear; y >= currentYear - 5; y--) {
            const opt = document.createElement('option');
            opt.value = y;
            opt.textContent = y;
            yearSelect.appendChild(opt);
        }
    }

    if (!loadReportBtn) return;

    loadReportBtn.addEventListener('click', async () => {
        const month = monthSelect ? monthSelect.value : 'all';
        const year = yearSelect ? yearSelect.value : new Date().getFullYear();

        try {
            const resp = await fetch(`/api/reports/monthly?month=${month}&year=${year}`);
            if (!resp.ok) throw new Error('โหลดข้อมูลไม่สำเร็จ: ' + resp.status + ' ' + resp.statusText);

            const data = await parseJsonSafe(resp);

            const resultEl = document.getElementById('reportResult');
            if (resultEl) resultEl.classList.remove('hidden');

            const monthNameEl = document.getElementById('monthName');
            const yearNameEl = document.getElementById('yearName');
            if (monthNameEl) monthNameEl.textContent = month === 'all' ? 'ทั้งหมด' : getMonthName(month);
            if (yearNameEl) yearNameEl.textContent = year;

            const totalRevenueEl = document.getElementById('totalRevenue');
            const totalOrdersEl = document.getElementById('totalOrders');
            const topMenuEl = document.getElementById('topMenu');
            const topCountEl = document.getElementById('topCount');
            const topRevenueEl = document.getElementById('topRevenue');

            if (totalRevenueEl) totalRevenueEl.textContent = (data.totalRevenue ?? 0).toLocaleString();
            if (totalOrdersEl) totalOrdersEl.textContent = data.totalOrders ?? '0';
            if (topMenuEl) topMenuEl.textContent = data.topMenu ?? '-';
            if (topCountEl) topCountEl.textContent = data.topCount ?? '0';
            if (topRevenueEl) topRevenueEl.textContent = (data.topRevenue ?? 0).toLocaleString();

            if (data.monthlySales) renderChart(data.monthlySales);
        } catch (err) {
            showErrorModal('เกิดข้อผิดพลาดในการโหลดรายงาน:\n' + (err.message || String(err)));
            console.error('Monthly report error:', err);
        }
    });
});

function getMonthName(month) {
    const months = ["มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม",
        "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"];
    return months[month - 1];
}

function renderChart(monthlySales) {
    // Guard: ensure Chart.js is loaded
    if (typeof Chart === 'undefined') {
        console.error('Chart.js is not loaded. Please include Chart.js before manager.js');
        showErrorModal('ไม่สามารถแสดงกราฟ: Chart.js ยังไม่ได้โหลด');
        return;
    }

    const canvas = document.getElementById("monthlySalesChart");
    if (!canvas) {
        console.warn('monthlySalesChart canvas not found');
        return;
    }
    const ctx = canvas.getContext("2d");

    // Validate monthlySales array
    if (!Array.isArray(monthlySales)) {
        console.warn('monthlySales is not an array:', monthlySales);
        return;
    }

    // ถ้ามีกราฟอยู่แล้ว ลบก่อน
    if (salesChart) salesChart.destroy();

    salesChart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: ["ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.", 
                        "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค."],
            datasets: [{
                label: "ยอดขาย (บาท)",
                data: monthlySales,
                backgroundColor: "rgba(255, 159, 64, 0.8)",
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}

async function updateManagerStats() {
    try {
        // Fetch sales report
        const resp = await fetchWithTimeout('/api/reports/sales', {}, 8000);
        if (!resp.ok) {
            throw new Error('ไม่สามารถโหลดรายงานยอดขาย: ' + resp.status + ' ' + resp.statusText);
        }
        const report = await parseJsonSafe(resp);
        console.log('sales report:', report);

        const ordersEl = document.getElementById('todayOrders');
        const revenueEl = document.getElementById('todayRevenue');
        if (ordersEl) {
            ordersEl.textContent = report.orderCount ?? 0;
        }
        if (revenueEl) {
            const revenueValue = typeof report.revenue === 'number' ? report.revenue.toFixed(2) : (report.revenue ?? 0);
            revenueEl.textContent = `฿${Number(revenueValue).toLocaleString()}`;
        }

        // Fetch order statistics (counts by status)
            try {
                const orderStatsResp = await fetchWithTimeout('/api/managers/order-stats', {}, 8000);
                if (orderStatsResp.ok) {
                    const orderStats = await parseJsonSafe(orderStatsResp);
                console.log('order stats:', orderStats);
                const pendingEl = document.getElementById('pendingOrders');
                const completedEl = document.getElementById('completedOrders');

                if (pendingEl) {
                    pendingEl.textContent = orderStats.pendingOrders ?? 0;
                }
                if (completedEl) {
                    completedEl.textContent = orderStats.completedOrders ?? 0;
                }
            } else {
                console.warn('order-stats responded with', orderStatsResp.status);
            }
        } catch (orderErr) {
            console.error('Failed to load order statistics:', orderErr);
        }
    } catch (err) {
        console.error('updateManagerStats error:', err);
    }
}

// Function to load menu items and apply filter
async function loadMenuItems() {
    const menuList = document.getElementById("menuManagementList");
    if (!menuList) {
        return;
    }
    menuList.innerHTML = "";
    let menuItems = [];
    try {
        const resp = await fetch('/api/manager/menu-items');
        if (resp.ok) {
            menuItems = await parseJsonSafe(resp);
        }
    } catch (e) {
        alert('โหลดเมนูไม่สำเร็จ');
    }
    
    // Apply filter based on currentMenuFilter
    const filteredItems = currentMenuFilter === 'active' 
        ? menuItems.filter(item => item.active) 
        : menuItems;
    
    filteredItems.forEach(item => {
        const div = document.createElement("div");
        // Add 'inactive-menu-item' class if item is not active
        const activeClass = item.active ? '' : 'inactive-menu-item';
        div.className = `flex justify-between bg-gray-100 p-4 rounded-lg ${activeClass}`;
        
        // Create badge for inactive items
        const statusBadge = item.active ? '' : '<span class="inactive-badge ml-2 px-2 py-1 text-xs bg-gray-400 text-white rounded">ไม่พร้อมจำหน่าย</span>';
        
        div.innerHTML = `
            <span>${item.name} - ฿${item.price}${statusBadge}</span>
            <div class="flex space-x-2">
                <button class="bg-blue-500 text-white px-3 py-1 rounded edit-menu" data-id="${item.id}">แก้ไข</button>
                <button class="bg-red-500 text-white px-3 py-1 rounded delete-menu" data-id="${item.id}">ลบ</button>
            </div>
        `;
        menuList.appendChild(div);
    });

    document.querySelectorAll(".edit-menu").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = parseInt(btn.dataset.id, 10);
            showEditMenuModal(id);
        });
    });

    document.querySelectorAll(".delete-menu").forEach(btn => {
        btn.addEventListener("click", async () => {
            const id = parseInt(btn.dataset.id, 10);

            // Get CSRF token
            const csrfToken = getCsrfToken();
            const csrfHeader = getCsrfHeader();

            const resp = await fetch(`/api/manager/menu-items/${id}`, { 
                method: 'DELETE', 
            headers: {
                [csrfHeader]: csrfToken
            }
        });
            if (resp.ok || resp.status === 204) {
                await loadMenuItems();
            } else {
                alert('ลบเมนูไม่สำเร็จ');
            }
        });
    });
}

// Function to show the Add Menu modal
function showAddMenuModal() {
    const modal = document.getElementById('addMenuModal');
    if (modal) {
        modal.classList.remove('hidden');
    }
}

// Reset and hide the Add Menu modal
function hideAddMenuModal() {
    const modal = document.getElementById('addMenuModal');
    const form = document.getElementById('addMenuForm');
    if (modal) {
        modal.classList.add('hidden');
    }
    if (form) {
        form.reset();
        // Reset checkbox to checked state
        const activeCheckbox = document.getElementById('menuActive');
        if (activeCheckbox) {
            activeCheckbox.checked = true;
        }
        // Remove edit mode data
        delete form.dataset.mode;
        delete form.dataset.id;
    }
    // Reset modal title to "Add Menu"
    const modalTitle = document.getElementById('menuModalTitle');
    if (modalTitle) {
        modalTitle.textContent = 'เพิ่มเมนูใหม่';
    }
}

// Function to show the Edit Menu modal with pre-populated data
async function showEditMenuModal(menuItemId) {
    try {
        // Fetch menu item data
        const resp = await fetchWithTimeout(`/api/manager/menu-items/${menuItemId}`);

        if (resp.status === 404) {
            showErrorModal('ไม่พบเมนูที่ต้องการแก้ไข');
            return;
        }

        if (!resp.ok) {
            throw new Error('Failed to fetch menu item: ' + resp.status + ' ' + resp.statusText);
        }

        const menuItem = await parseJsonSafe(resp);
        
        // Get form and modal elements
        const form = document.getElementById('addMenuForm');
        const modal = document.getElementById('addMenuModal');
        const modalTitle = document.getElementById('menuModalTitle');
        
        // Set form to edit mode
        if (form) {
            form.dataset.mode = 'edit';
            form.dataset.id = String(menuItemId);
        }
        
        // Change modal title to "Edit Menu"
        if (modalTitle) {
            modalTitle.textContent = 'แก้ไขเมนู';
        }
        
        // Pre-populate form fields
        const nameInput = document.getElementById('menuName');
        const priceInput = document.getElementById('menuPrice');
        const categorySelect = document.getElementById('menuCategory');
        const descriptionTextarea = document.getElementById('menuDescription');
        const activeCheckbox = document.getElementById('menuActive');
        
        if (nameInput) nameInput.value = menuItem.name || '';
        if (priceInput) priceInput.value = menuItem.price || '';
        if (categorySelect) categorySelect.value = menuItem.category || '';
        if (descriptionTextarea) descriptionTextarea.value = menuItem.description || '';
        if (activeCheckbox) activeCheckbox.checked = menuItem.active !== false;
        
        // Show modal
        if (modal) {
            modal.classList.remove('hidden');
        }
    } catch (err) {
        console.error('Error fetching menu item:', err);
        showErrorModal('เกิดข้อผิดพลาดในการโหลดข้อมูลเมนู');
    }
}

// Handle Add/Edit Menu form submission
async function handleAddMenu(event) {
    event.preventDefault();
    
    // Get form and determine mode
    const form = document.getElementById('addMenuForm');
    const mode = form.dataset.mode || 'add';
    const menuItemId = form.dataset.id;
    
    // Get form values
    const name = document.getElementById('menuName').value.trim();
    const priceValue = document.getElementById('menuPrice').value;
    const category = document.getElementById('menuCategory').value;
    const description = document.getElementById('menuDescription').value.trim();
    const active = document.getElementById('menuActive').checked;

    // Client-side validation
    if (!name) {
        showErrorModal('กรุณาระบุชื่อเมนู');
        return;
    }

    if (name.length > 100) {
        showErrorModal('ชื่อเมนูต้องไม่เกิน 100 ตัวอักษร');
        return;
    }

    if (!priceValue || parseFloat(priceValue) <= 0) {
        showErrorModal('กรุณาระบุราคาที่มากกว่า 0');
        return;
    }

    const price = parseFloat(priceValue);
    if (price > 9999.99) {
        showErrorModal('ราคาต้องไม่เกิน 9999.99 บาท');
        return;
    }

    if (!category) {
        showErrorModal('กรุณาเลือกหมวดหมู่');
        return;
    }

    if (description.length > 500) {
        showErrorModal('รายละเอียดเมนูต้องไม่เกิน 500 ตัวอักษร');
        return;
    }

    // Prepare request data
    const menuData = {
        name: name,
        price: price,
        category: category,
        description: description,
        active: active
    };

    try {
        let resp;
        let successMessage;
        
        // Get CSRF token for secure requests
        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();
        
        if (mode === 'edit' && menuItemId) {
            // Edit mode - PUT request
            resp = await fetchWithTimeout(`/api/manager/menu-items/${menuItemId}`, {
                method: 'PUT',
                headers: { 
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify(menuData)
            });
            successMessage = 'แก้ไขเมนูสำเร็จ';
        } else {
            // Add mode - POST request
            resp = await fetchWithTimeout('/api/manager/menu-items', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify(menuData)
            });
            successMessage = 'เพิ่มเมนูสำเร็จ';
        }

        if (resp.ok) {
            // Success
            hideAddMenuModal();
            showSuccessModal(successMessage);
            await loadMenuItems();
        } else if (resp.status === 404) {
            // Not found (for edit mode)
            showErrorModal('ไม่พบเมนูที่ต้องการแก้ไข');
        } else if (resp.status === 400) {
            // Validation error from backend
            let errorData = null;
            try {
                errorData = await parseJsonSafe(resp);
            } catch (parseErr) {
                console.warn('Failed to parse validation error JSON:', parseErr);
            }
            let errorMessage = 'ข้อมูลไม่ถูกต้อง:\n';
            if (errorData && errorData.message) {
                errorMessage += errorData.message;
            } else if (errorData && errorData.errors) {
                errorMessage += Object.values(errorData.errors).join('\n');
            } else {
                errorMessage += 'Server returned validation error.';
            }
            showErrorModal(errorMessage);
        } else if (resp.status === 403) {
            showErrorModal('คุณไม่มีสิทธิ์ในการจัดการเมนู');
        } else {
            showErrorModal('เกิดข้อผิดพลาดในการบันทึกเมนู');
        }
    } catch (err) {
        console.error('Error saving menu:', err);
        showErrorModal('เกิดข้อผิดพลาดในการเชื่อมต่อกับเซิร์ฟเวอร์');
    }
}

// Initialize tab functionality
function initManagerTabs() {
    document.querySelectorAll('.manager-tab').forEach(tab => {
        tab.addEventListener('click', event => {
            const tabName = tab.dataset.tab;
            switchManagerTab(event, tabName);
        });
    });
}

function switchManagerTab(event, tabName) {
    document.querySelectorAll('.manager-tab').forEach(tab => {
        tab.classList.remove('border-orange-500', 'text-orange-600');
        tab.classList.add('text-gray-500');
    });
    event.currentTarget.classList.add('border-orange-500', 'text-orange-600');
    event.currentTarget.classList.remove('text-gray-500');

    document.querySelectorAll('.manager-content').forEach(content => {
        content.classList.add('hidden');
    });

    const target = document.getElementById(`${tabName}Management`);
    if (target) {
        target.classList.remove('hidden');
    }
}

// Load and manage employee data
async function loadEmployeeManagement() {
    const employeeList = document.getElementById('employeeList');
    if (!employeeList) {
        return;
    }
    employeeList.innerHTML = '';

    let employees = [];
    try {
        const resp = await fetch('/api/employees');
        if (resp.ok) {
                employees = await parseJsonSafe(resp);
        }
    } catch (err) {
        console.error('โหลดข้อมูลพนักงานไม่สำเร็จ', err);
    }

    if (employees.length === 0) {
        employeeList.innerHTML = '<p class="text-gray-500">ยังไม่มีพนักงาน</p>';
        return;
    }

    employees.forEach(employee => {
        const employeeItem = document.createElement('div');
        employeeItem.className = 'flex items-center justify-between p-4 bg-gray-50 rounded-lg';
        
        // Display username if available
        const usernameDisplay = employee.username ? `<p class="text-xs text-gray-500">Username: ${employee.username}</p>` : '';
        
        employeeItem.innerHTML = `
            <div>
                <h5 class="font-semibold">${employee.name}</h5>
                <p class="text-sm text-gray-600">${employee.position || ''}</p>
                ${usernameDisplay}
            </div>
            <div class="flex items-center space-x-2">
                <button class="text-sm text-red-600 hover:text-red-800 delete-employee">ลบ</button>
            </div>
        `;

        employeeItem.querySelector('.delete-employee').addEventListener('click', async () => {
            const confirmDelete = confirm(`ต้องการลบ ${employee.name} หรือไม่?`);
            if (!confirmDelete) {
                return;
            }
            
            // Get CSRF token
            const csrfToken = getCsrfToken();
            const csrfHeader = getCsrfHeader();
            
            const resp = await fetch(`/api/employees/${employee.id}`, {
                method: 'DELETE',
                headers: {
                    [csrfHeader]: csrfToken
                }
            });
            if (resp.ok || resp.status === 204) {
                await loadEmployeeManagement();
                showSuccessModal('ลบพนักงานสำเร็จ');
            } else {
                showErrorModal('ลบพนักงานไม่สำเร็จ');
            }
        });

        employeeList.appendChild(employeeItem);
    });
}

// Open employee dialog for add or edit
function openEmployeeDialog(employee) {
    if (!employeeModal || !employeeForm || !employeeModalTitle || !employeeNameInput || !employeePositionInput) {
        return;
    }

    if (employee) {
        // Edit mode is not supported for employee registration with credentials
        showErrorModal('การแก้ไขพนักงานยังไม่รองรับในเวอร์ชันนี้\nกรุณาลบและสร้างใหม่หากต้องการเปลี่ยนแปลง');
        return;
    } else {
        // Create mode
        employeeForm.dataset.mode = 'create';
        delete employeeForm.dataset.id;
        employeeModalTitle.textContent = 'เพิ่มพนักงาน';
        employeeForm.reset();
        
        // Make sure username and password fields are visible and required
        if (employeeUsernameInput) {
            employeeUsernameInput.required = true;
            employeeUsernameInput.value = '';
        }
        if (employeePasswordInput) {
            employeePasswordInput.required = true;
            employeePasswordInput.value = '';
        }
    }

    showEmployeeModal();
}

function showEmployeeModal() {
    if (employeeModal) {
        employeeModal.classList.remove('hidden');
    }
}

function hideEmployeeModal() {
    if (employeeModal) {
        employeeModal.classList.add('hidden');
    }
    if (employeeForm) {
        employeeForm.reset();
        delete employeeForm.dataset.mode;
        delete employeeForm.dataset.id;
    }
}

async function handleEmployeeSubmit(event) {
    event.preventDefault();
    if (!employeeForm) {
        return;
    }

    const name = employeeNameInput.value.trim();
    const position = employeePositionInput.value.trim();
    const username = employeeUsernameInput.value.trim();
    const password = employeePasswordInput.value.trim();

    // Validation
    if (!name) {
        showErrorModal('กรุณาระบุชื่อพนักงาน');
        return;
    }

    if (!position) {
        showErrorModal('กรุณาเลือกตำแหน่ง');
        return;
    }

    if (!username) {
        showErrorModal('กรุณาระบุชื่อผู้ใช้');
        return;
    }

    if (!password) {
        showErrorModal('กรุณาระบุรหัสผ่าน');
        return;
    }

    if (password.length < 6) {
        showErrorModal('รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร');
        return;
    }

    const payload = {
        name: name,
        position: position,
        username: username,
        password: password
    };

    const mode = employeeForm.dataset.mode;
    try {
        // Get CSRF token
        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();

        let resp;
        if (mode === 'edit' && employeeForm.dataset.id) {
            // For edit mode, we might need different logic
            // For now, we'll just support creating new employees
            showErrorModal('การแก้ไขพนักงานยังไม่รองรับในเวอร์ชันนี้');
            return;
        } else {
            // Use the manager employee registration endpoint
            resp = await fetchWithTimeout('/api/managers/employees', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify(payload)
            }, 10000);
        }

        if (!resp.ok) {
            let errorData = null;
            try {
                errorData = await parseJsonSafe(resp);
            } catch (parseErr) {
                // ignore parse error, we'll show a generic message
            }
            let errorMessage = 'บันทึกข้อมูลพนักงานไม่สำเร็จ';

            if (resp.status === 409 || (errorData && errorData.message && errorData.message.includes('duplicate'))) {
                errorMessage = 'ชื่อผู้ใช้นี้มีอยู่ในระบบแล้ว กรุณาเลือกชื่อผู้ใช้อื่น';
            } else if (errorData && errorData.message) {
                errorMessage = errorData.message;
            }

            showErrorModal(errorMessage);
            return;
        }

        let result = null;
        try {
            result = await parseJsonSafe(resp);
        } catch (parseErr) {
            result = null;
        }
        await loadEmployeeManagement();
        hideEmployeeModal();
        showSuccessModal('เพิ่มพนักงานสำเร็จ!\n\nชื่อผู้ใช้: ' + username + '\nรหัสผ่าน: ' + password + '\n\nกรุณาบันทึกข้อมูลนี้');
    } catch (err) {
        console.error(err);
        showErrorModal(err.message || 'บันทึกข้อมูลพนักงานไม่สำเร็จ');
    }
}

// Utility functions to show/hide success and error modals
function showSuccessModal(message) {
    const modal = document.getElementById('successModal');
    const messageEl = document.getElementById('successMessage');
    if (modal && messageEl) {
        messageEl.textContent = message;
        modal.classList.remove('hidden');
    }
}

function hideSuccessModal() {
    const modal = document.getElementById('successModal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

function showErrorModal(message) {
    const modal = document.getElementById('errorModal');
    const messageEl = document.getElementById('errorMessage');
    if (modal && messageEl) {
        messageEl.textContent = message;
        modal.classList.remove('hidden');
    }
}

function hideErrorModal() {
    const modal = document.getElementById('errorModal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

export async function fetchCartForUser(userId) {
    const resp = await fetch(`/api/carts/${userId}`);
    if (!resp.ok) {
        throw new Error('ไม่พบตะกร้าของผู้ใช้');
    }
    try {
        return await parseJsonSafe(resp);
    } catch (err) {
        throw new Error('ไม่สามารถอ่านข้อมูลตะกร้า: ' + (err.message || err));
    }
}

// Expose fetchCartForUser globally for other scripts to use
window.fetchCartForUser = fetchCartForUser;