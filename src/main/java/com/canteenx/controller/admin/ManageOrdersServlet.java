package com.canteenx.controller.admin;

import com.canteenx.dao.OrderDAO;
import com.canteenx.model.Order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for canteen staff to manage orders and update lifecycle statuses.
 * Supports statuses: PLACED, ACCEPTED, PREPARING, READY, COMPLETED, CANCELLED.
 */
@WebServlet(name = "ManageOrdersServlet", urlPatterns = {"/admin/orders"})
public class ManageOrdersServlet extends HttpServlet {

    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        this.orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String filter = request.getParameter("filter");
        if (filter == null || filter.trim().isEmpty()) {
            filter = "ALL";
        }

        List<Order> orders = orderDAO.getAllOrders(filter);
        request.setAttribute("orders", orders);
        request.setAttribute("currentFilter", filter);
        request.getRequestDispatcher("/WEB-INF/views/admin/order-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hidden Form Field processing
        String action = request.getParameter("action");
        String orderIdStr = request.getParameter("orderId");
        String newStatus = request.getParameter("newStatus");
        String currentFilter = request.getParameter("currentFilter");

        if ("UPDATE_STATUS".equalsIgnoreCase(action) && orderIdStr != null && newStatus != null) {
            try {
                int orderId = Integer.parseInt(orderIdStr);
                orderDAO.updateOrderStatus(orderId, newStatus.trim().toUpperCase());
            } catch (NumberFormatException ignored) {}
        }

        String redirectUrl = request.getContextPath() + "/admin/orders";
        if (currentFilter != null && !currentFilter.trim().isEmpty()) {
            redirectUrl += "?filter=" + currentFilter;
        }
        response.sendRedirect(redirectUrl);
    }
}

