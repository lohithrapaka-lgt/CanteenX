package com.canteenx.controller.admin;

import jakarta.servlet.annotation.WebServlet;

/**
 * AdminOrderServlet alias mapping for /admin-orders.
 */
@WebServlet(name = "AdminOrderServlet", urlPatterns = {"/admin-orders"})
public class AdminOrderServlet extends ManageOrdersServlet {
    private static final long serialVersionUID = 1L;
}

