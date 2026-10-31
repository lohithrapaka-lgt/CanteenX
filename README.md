
# CanteenX — College Canteen Management System

A web-based canteen ordering and management system built using Java, JSP, Servlets, JDBC, and MySQL.

CanteenX provides a platform for students to explore the food menu, add items to their cart, place orders, and track order status. Administrators can manage food items, menus, and customer orders through an administrative interface.

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
├── pom.xml
└── README.md
```

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

4. Configure your database connection using these environment variables:

```text
CANTEEN_DB_URL=jdbc:mysql://localhost:3306/canteen_db
CANTEEN_DB_USER=your_mysql_username
CANTEEN_DB_PASSWORD=your_mysql_password
```

Use your own local database credentials. Do not commit passwords to GitHub.

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

4. Deploy the generated WAR file from `target/CanteenX.war` to the `webapps` directory of Apache Tomcat 10.1.

5. Start Tomcat and open:

```text
http://localhost:8080/CanteenX/
```

## Project Information

**Project:** CanteenX — College Canteen Management System

**Architecture:** Java Web Application (Maven WAR)

**Database:** MySQL

**Application Server:** Apache Tomcat 10.1

---

Developed as a college web technology project.


![Admin Dashboard](screenshots/06-admin-dashboard.png)

### 7. Admin Order Management
Administrators can view incoming orders, filter them by status, and manage the order lifecycle from placement to completion.

![Admin Order Management](screenshots/07-admin-order-management.png)
