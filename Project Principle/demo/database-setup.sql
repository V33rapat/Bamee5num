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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_created_at (created_at)
);

-- Create employees table (base table for Employee entity with JOINED inheritance)
CREATE TABLE IF NOT EXISTS employees (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(50) NOT NULL,
    INDEX idx_position (position)
);

-- Create managers table (extends employees with JOINED inheritance strategy)
CREATE TABLE IF NOT EXISTS managers (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES employees(id) ON DELETE CASCADE
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

-- Insert some sample data for testing (optional)
INSERT INTO customers (name, username, email, phone, password_hash) VALUES
('John Doe', 'johndoe', 'john@example.com', '081-234-5678', '$2a$10$example_hash'),
('Jane Smith', 'janesmith', 'jane@example.com', '089-876-5432', '$2a$10$example_hash');

-- Insert sample employees and managers (optional)
INSERT INTO employees (id, name, position) VALUES
(1001, 'Alice Manager', 'Manager'),
(1002, 'Bob Employee', 'Server'),
(1003, 'Charlie Employee', 'Cook');

INSERT INTO managers (id) VALUES (1001);

-- Insert sample menu items (optional)
INSERT INTO menu_items (name, price, category, description, active) VALUES
('Pad Thai', 120.00, 'Main Course', 'Classic Thai stir-fried noodles', TRUE),
('Tom Yum Soup', 95.00, 'Soup', 'Spicy and sour Thai soup', TRUE),
('Green Curry', 135.00, 'Main Course', 'Thai green curry with chicken', TRUE),
('Mango Sticky Rice', 85.00, 'Dessert', 'Sweet mango with sticky rice', TRUE),
('Thai Iced Tea', 45.00, 'Beverage', 'Traditional Thai tea with milk', TRUE);

SHOW TABLES;
