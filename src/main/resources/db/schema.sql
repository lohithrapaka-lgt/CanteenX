-- ==============================================================
-- CanteenX: College Canteen Ordering & Management System
-- Database Schema for MySQL 8
-- Database Name: canteen_db
-- ==============================================================

CREATE DATABASE IF NOT EXISTS canteen_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE canteen_db;

-- --------------------------------------------------------------
-- 1. Table: users
-- Roles: STUDENT, ADMIN
-- --------------------------------------------------------------
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- --------------------------------------------------------------
-- 2. Table: categories
-- --------------------------------------------------------------
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB;

-- --------------------------------------------------------------
-- 3. Table: menu_items
-- Availability toggle, price validation
-- --------------------------------------------------------------
CREATE TABLE menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(8, 2) NOT NULL CHECK (price > 0),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(255) DEFAULT 'assets/images/food-placeholder.jpg',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_category FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- --------------------------------------------------------------
-- 4. Table: orders
-- Order statuses: PLACED, ACCEPTED, PREPARING, READY, COMPLETED, CANCELLED
-- --------------------------------------------------------------
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    order_status ENUM('PLACED', 'ACCEPTED', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PLACED',
    payment_method VARCHAR(30) NOT NULL DEFAULT 'CASH_AT_COUNTER',
    payment_status ENUM('PENDING', 'PAID') NOT NULL DEFAULT 'PENDING',
    special_instructions VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------------
-- 5. Table: order_items
-- Line items associated with each order
-- --------------------------------------------------------------
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(8, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_orderitems_order FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_orderitems_item FOREIGN KEY (item_id)
        REFERENCES menu_items(item_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ==============================================================
-- INITIAL SEED DATA
-- Passwords below are SHA-256 hashes:
-- 'admin123'   -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- 'student123' -> 703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b
-- ==============================================================

-- Seed Users
INSERT INTO users (full_name, email, phone, password_hash, role) VALUES
('Campus Canteen Admin', 'admin@canteenx.com', '9876543210', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'),
('Rahul Sharma', 'student@college.edu', '9123456780', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'STUDENT'),
('Ananya Patel', 'ananya@college.edu', '9812345678', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'STUDENT');

-- Seed Categories
INSERT INTO categories (category_id, category_name, description) VALUES
(1, 'Breakfast', 'Morning tiffins, dosas, idlis, and quick bites'),
(2, 'Meals & Thali', 'Wholesome rice bowls, curries, and full lunch combos'),
(3, 'Quick Snacks', 'Crispy evening snacks, sandwiches, and rolls'),
(4, 'Beverages', 'Refreshing hot and cold drinks, shakes, and juices'),
(5, 'Desserts', 'Sweet treats, ice creams, and baked goods');

-- Seed Menu Items
INSERT INTO menu_items (category_id, item_name, description, price, is_available, image_url) VALUES
(1, 'Masala Dosa', 'Crispy fermented crepe filled with spiced potato masala, served with sambar & chutney', 50.00, TRUE, 'assets/images/dosa.jpg'),
(1, 'Idli Vada Combo', '2 steamed fluffy rice cakes and 1 crispy medu vada served with coconut chutney and hot sambar', 40.00, TRUE, 'assets/images/idli.jpg'),
(1, 'Poha with Sev', 'Flattened rice tempered with mustard seeds, curry leaves, onions, and topped with crunchy sev', 30.00, TRUE, 'assets/images/poha.jpg'),

(2, 'Special Veg Thali', '2 Roti, Paneer Butter Masala, Dal Tadka, Jeera Rice, Salad, Pickle, and Sweet', 110.00, TRUE, 'assets/images/thali.jpg'),
(2, 'Paneer Fried Rice', 'Aromatic wok-tossed basmati rice with golden paneer cubes and crunchy vegetables', 90.00, TRUE, 'assets/images/fried_rice.jpg'),
(2, 'Chole Bhature (2 Pcs)', 'Spicy and tangy chickpea curry served with two puffy deep-fried leavened bhature', 80.00, TRUE, 'assets/images/chole.jpg'),

(3, 'Aloo Samosa (2 Pcs)', 'Golden triangular pastries stuffed with spiced potatoes and peas, served with mint chutney', 25.00, TRUE, 'assets/images/samosa.jpg'),
(3, 'Grilled Cheese Sandwich', 'Toasted bread filled with seasoned vegetables, mozzarella, and cheddar cheese', 45.00, TRUE, 'assets/images/sandwich.jpg'),
(3, 'Veg Spring Roll', 'Crispy wrappers packed with sauteed julienne vegetables and glass noodles', 50.00, TRUE, 'assets/images/spring_roll.jpg'),

(4, 'Kulhad Masala Chai', 'Rich, aromatic milk tea brewed with cardamom, ginger, and cloves, served in traditional clay cup', 15.00, TRUE, 'assets/images/chai.jpg'),
(4, 'Cold Coffee with Ice Cream', 'Thick creamy blended cold coffee topped with a scoop of vanilla ice cream', 55.00, TRUE, 'assets/images/cold_coffee.jpg'),
(4, 'Fresh Lime Soda', 'Sparkling sweet and salted lime refresher', 30.00, TRUE, 'assets/images/lime_soda.jpg'),

(5, 'Gulab Jamun (2 Pcs)', 'Soft milk-solid dumplings soaked in rose and cardamom flavored sugar syrup', 35.00, TRUE, 'assets/images/gulab_jamun.jpg'),
(5, 'Chocolate Brownie', 'Warm fudgy chocolate brownie with dark chocolate drizzle', 60.00, TRUE, 'assets/images/brownie.jpg');

-- Seed Sample Order for Demonstration
INSERT INTO orders (order_id, user_id, total_amount, order_status, payment_method, payment_status, special_instructions) VALUES
(1001, 2, 95.00, 'PREPARING', 'CASH_AT_COUNTER', 'PAID', 'Less spicy, extra green chutney please');

INSERT INTO order_items (order_id, item_id, quantity, unit_price, subtotal) VALUES
(1001, 1, 1, 50.00, 50.00),
(1001, 7, 1, 25.00, 25.00),
(1001, 10, 1, 20.00, 20.00);
