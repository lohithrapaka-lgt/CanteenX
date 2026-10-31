<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.canteenx.model.User"%>
<%@ page import="com.canteenx.model.Cart"%>
<%
    // Demonstration of JSP implicit objects: request, session, response
    User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
    Cart cart = (session != null) ? (Cart) session.getAttribute("cart") : null;
    int cartCount = (cart != null) ? cart.getTotalItemCount() : 0;
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CanteenX - College Canteen Ordering System</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="<%= ctx %>/assets/css/bootstrap.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="<%= ctx %>/assets/css/custom.css">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
    <div class="container">
        <!-- Demonstration of URL Rewriting with response.encodeURL -->
        <a class="navbar-brand text-warning" href="<%= response.encodeURL(ctx + "/menu") %>">
            🍔 Canteen<span class="text-white">X</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent"
                aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="<%= response.encodeURL(ctx + "/menu") %>">Browse Menu</a>
                </li>
                <% if (currentUser != null && currentUser.isStudent()) { %>
                    <li class="nav-item">
                        <a class="nav-link" href="<%= response.encodeURL(ctx + "/order-history") %>">My Orders</a>
                    </li>
                <% } %>
                <% if (currentUser != null && currentUser.isAdmin()) { %>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle text-info fw-bold" href="#" id="adminDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            Admin Portal
                        </a>
                        <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="adminDropdown">
                            <li><a class="dropdown-item" href="<%= response.encodeURL(ctx + "/admin/dashboard") %>">📊 Dashboard</a></li>
                            <li><a class="dropdown-item" href="<%= response.encodeURL(ctx + "/admin/orders") %>">📋 Order Lifecycle</a></li>
                            <li><a class="dropdown-item" href="<%= response.encodeURL(ctx + "/admin/menu") %>">🍲 Menu Management</a></li>
                        </ul>
                    </li>
                <% } %>
            </ul>

            <div class="d-flex align-items-center gap-3">
                <!-- Cart Icon for Student -->
                <% if (currentUser == null || currentUser.isStudent()) { %>
                    <a href="<%= response.encodeURL(ctx + "/cart") %>" class="btn btn-outline-warning position-relative btn-sm px-3">
                        🛒 Cart
                        <span id="cart-badge-count" class="badge rounded-pill bg-danger position-absolute top-0 start-100 translate-middle <%= (cartCount > 0) ? "" : "d-none" %>">
                            <%= cartCount %>
                        </span>
                    </a>
                <% } %>

                <!-- User Session State -->
                <% if (currentUser != null) { %>
                    <div class="dropdown">
                        <button class="btn btn-sm btn-outline-light dropdown-toggle" type="button" id="userMenu" data-bs-toggle="dropdown" aria-expanded="false">
                            👤 <%= currentUser.getFullName() %> (<%= currentUser.getRole() %>)
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userMenu">
                            <li><span class="dropdown-item-text text-muted small"><%= currentUser.getEmail() %></span></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="<%= response.encodeURL(ctx + "/logout") %>">Logout</a></li>
                        </ul>
                    </div>
                <% } else { %>
                    <a href="<%= response.encodeURL(ctx + "/login") %>" class="btn btn-sm btn-outline-light">Login</a>
                    <a href="<%= response.encodeURL(ctx + "/register") %>" class="btn btn-sm btn-warning">Register</a>
                <% } %>
            </div>
        </div>
    </div>
</nav>

<main class="py-4">
    <div class="container">
