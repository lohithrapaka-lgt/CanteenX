package com.canteenx.controller.admin;

import jakarta.servlet.annotation.WebServlet;

/**
 * AdminFoodServlet alias mapping for /admin/food and /admin-food.
 */
@WebServlet(name = "AdminFoodServlet", urlPatterns = {"/admin/food", "/admin-food"})
public class AdminFoodServlet extends ManageMenuServlet {
    private static final long serialVersionUID = 1L;
}

