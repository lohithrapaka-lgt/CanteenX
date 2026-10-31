
# CanteenX — College Canteen Management System

A web-based canteen ordering and management system built using Java, JSP, Servlets, JDBC, and MySQL.

CanteenX allows students to browse food items, manage their shopping cart, place orders, and track order status. Administrators can manage food items, menus, and customer orders through a dedicated dashboard.

## Features

### Student Module
- Student registration and login
- Browse food items and categories
- View food item details
- Add and manage cart items
- Place food orders
- View order history
- Track order status

### Admin Module
- Administrator login
- Admin dashboard
- Manage food items and menu
- Manage customer orders
- XML menu management and validation

## Technology Stack

| Category | Technology |
|---|---|
| Backend | Java 17 |
| Web Technologies | JSP, HTML, CSS, JavaScript |
| Server-side | Jakarta Servlets |
| Database | MySQL 8 |
| Database Connectivity | JDBC |
| Build Tool | Apache Maven |
| Web Server | Apache Tomcat 10.1 |
| Data Formats | XML, DTD, XSD |

## Project Structure

```text
CanteenX/
├── src/
│   └── main/
│       ├── java/com/canteenx/
│       │   ├── controller/
│       │   ├── dao/
│       │   ├── filter/
│       │   ├── model/
│       │   └── util/
│       └── resources/
│           ├── db/
│           └── xml/
├── webapp/
│   ├── WEB-INF/
│   │   └── views/
│   ├── assets/
│   │   ├── css/
│   │   ├── js/
│   │   └── images/
│   └── index.jsp
├── screenshots/
├── pom.xml
└── README.md
```

## Application Screenshots

### 1. Home Page — Food Menu

Students and visitors can browse available food items, explore categories, and search for their favourite meals.

![CanteenX Home Page](screenshots/01-home-menu.png)

### 2. Student Food Menu

Authenticated students can explore the food catalog and add selected items to their shopping cart.

![Student Menu](screenshots/02-student-menu.png)

### 3. Shopping Cart

Students can review selected items, update quantities, remove products, and view the total order amount.

![Shopping Cart](screenshots/03-shopping-cart.png)

### 4. Checkout

Students can review their details, choose a payment method, add special instructions, and confirm their order.

![Checkout Page](screenshots/04-checkout.png)

### 5. Order Confirmation and Tracking

After placing an order, students can view order details, payment information, and order progress.

![Order Confirmation](screenshots/05-order-confirmation.png)

### 6. Admin Dashboard

Administrators can monitor revenue, order statistics, kitchen activity, and recent orders from a centralized dashboard.

![Admin Dashboard](screenshots/06-admin-dashboard.png)

### 7. Admin Order Management

Administrators can view incoming orders, filter them by status, and manage the order lifecycle.

![Admin Order Management](screenshots/07-admin-order-management.png)

## Prerequisites

Install the following software:

- JDK 17
- Apache Maven
- MySQL 8
- Apache Tomcat 10.1

## Database Setup

1. Start your MySQL server.
2. Open MySQL Workbench or MySQL command line.
3. Execute the SQL script:

```text
src/main/resources/db/schema.sql
```

This script creates the `canteen_db` database, its tables, and sample data.

4. Configure the database connection using environment variables:

```text
CANTEEN_DB_URL=jdbc:mysql://localhost:3306/canteen_db
CANTEEN_DB_USER=your_mysql_username
CANTEEN_DB_PASSWORD=your_mysql_password
```

Use your own local database credentials. Never commit passwords to GitHub.

## Build and Run

1. Clone the repository:

```bash
git clone https://github.com/lohithrapaka-lgt/CanteenX.git
```

2. Open the project directory:

```bash
cd CanteenX
```

3. Build the project:

```bash
mvn clean package
```

4. Deploy `target/CanteenX.war` to the `webapps` directory of Apache Tomcat 10.1.

5. Start Tomcat and open:

```text
http://localhost:8080/CanteenX/
```

## Project Information

- **Project:** CanteenX — College Canteen Management System
- **Architecture:** Java Web Application (Maven WAR)
- **Database:** MySQL
- **Application Server:** Apache Tomcat 10.1

---

Developed as a college web technology project.
