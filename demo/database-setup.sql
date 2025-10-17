-- MySQL Database Setup Script for Restaurant Application
-- Run this script in MySQL Workbench or MySQL command line

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS restaurant_db;
USE restaurant_db;

-- Set character set and collation
ALTER DATABASE restaurant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Drop existing tables if they exist (to recreate with correct structure)
-- Order matters due to foreign key constraints
DROP TABLE IF EXISTS managers;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS menu_item;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS customer;

-- Create customers table (plural to match entity)
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
);

-- Create cart_items table (plural to match potential entity naming)
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    item_price DECIMAL(6,2) NOT NULL CHECK (item_price >= 0.01 AND item_price <= 9999.99),
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity >= 1 AND quantity <= 100),
    status VARCHAR(20) NOT NULL DEFAULT 'Pending' CHECK (status IN ('Pending')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_created_at (created_at),
    INDEX idx_status (status)
);

-- Create employees table (base table for Employee entity with JOINED inheritance)
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    INDEX idx_position (position),
    INDEX idx_username (username)
);

-- Create managers table (extends employees with JOINED inheritance strategy)
CREATE TABLE IF NOT EXISTS managers (
    id BIGINT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_email (email)
);

-- Create menu_items table (for MenuItem entity)
CREATE TABLE IF NOT EXISTS menu_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(50),
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL CHECK (price >= 0),
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_active (active),
    INDEX idx_category (category)
);
-- ============================================================================
-- ORDER MANAGEMENT SYSTEM TABLES
-- ============================================================================

-- Drop existing tables if they exist (order matters)
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    employee_id BIGINT NULL,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'Pending'
        CHECK (status IN ('Pending', 'In Progress', 'Finish', 'Cancelled')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE SET NULL,
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    item_price DECIMAL(10, 2) NOT NULL CHECK (item_price >= 0.01 AND item_price <= 9999.99),
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity >= 1 AND quantity <= 100),
    total DECIMAL(10, 2) NOT NULL CHECK (total >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    
    INDEX idx_order_id (order_id),
    INDEX idx_created_at (created_at)
);

-- ============================================================================
-- SAMPLE INSERTION FOR TESTING (Optional)
-- ============================================================================
-- Sample: create a test order for customer 1
-- INSERT INTO orders (customer_id, total_amount, status) VALUES (1, 130.00, 'Pending');
-- SET @order_id = LAST_INSERT_ID();
-- INSERT INTO order_items (order_id, item_name, item_price, quantity, total)
-- VALUES 
-- (@order_id, 'กะเพราเนื้อหมา', 50, 1, 50),
-- (@order_id, 'กะเพราเนื้อแมว', 40, 2, 80);



-- Insert some sample data for testing (optional)
-- INSERT INTO customers (name, username, email, phone, password_hash) VALUES
-- ('John Doe', 'johndoe', 'john@example.com', '081-234-5678', '$2a$10$example_hash'),
-- ('Jane Smith', 'janesmith', 'jane@example.com', '089-876-5432', '$2a$10$example_hash');

-- Insert sample manager (requires employee record first due to JOINED inheritance)
INSERT INTO employees (id, name, position, username, password) VALUES 
(1001, 'Alice Manager', 'Manager', 'alice_manager', '$2a$12$eJtOxgKnHNmUBfe72eOhEOjsaUqsX5YRb0uuQmhnRrDONGcv.z.EG');

INSERT INTO managers (id, email, created_at, updated_at) VALUES 
(1001, 'alice@restaurant.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SHOW TABLES;

-- ============================================================================
-- SCHEMA VERIFICATION NOTES
-- ============================================================================
-- Database schema changes for Order Management System:
-- 1. ✓ cart_items.status - VARCHAR(20), default 'Pending', with CHECK constraint
-- 2. ✓ cart_items.idx_status - Index for performance on status queries
-- 3. ✓ employees.username - VARCHAR(50), UNIQUE, NOT NULL for authentication
-- 4. ✓ employees.password - VARCHAR(255), NOT NULL for BCrypt hashes
-- 5. ✓ employees.idx_username - Index for username lookups
-- 
-- All schema changes are backward compatible with migration scripts provided below.
-- ============================================================================

-- ============================================================================
-- MIGRATION SCRIPTS FOR EXISTING DATABASES
-- ============================================================================
-- If you already have an existing database with the old schema, run these
-- ALTER TABLE statements to migrate to the new schema with manager authentication
-- ============================================================================

-- ⚠️ IMPORTANT: Run these migrations for existing databases

-- Step 1: Fix orders table CHECK constraint (change 'Completed' to 'Finish')
-- First, drop the existing constraint
ALTER TABLE orders DROP CHECK orders_chk_1;
-- Add the corrected constraint
ALTER TABLE orders ADD CONSTRAINT orders_chk_status 
    CHECK (status IN ('Pending', 'In Progress', 'Finish', 'Cancelled'));

-- Step 2: Simplify cart_items status constraint (only 'Pending' needed now)
-- Drop old constraint
ALTER TABLE cart_items DROP CHECK cart_items_chk_1;
-- Add simplified constraint (cart items are only for shopping, not order tracking)
ALTER TABLE cart_items ADD CONSTRAINT cart_items_chk_status 
    CHECK (status IN ('Pending'));

-- Step 3: Add authentication fields to employees table (if not exists)
-- ALTER TABLE employees ADD COLUMN username VARCHAR(50) UNIQUE NOT NULL;
-- ALTER TABLE employees ADD COLUMN password VARCHAR(255) NOT NULL;
-- ALTER TABLE employees ADD INDEX idx_username (username);


