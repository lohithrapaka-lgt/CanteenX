package com.canteenx.controller.student;

import com.canteenx.dao.OrderDAO;
import com.canteenx.model.Order;
import com.canteenx.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Controller for tracking live order lifecycle status.
 * Demonstrates:
 * - Order lifecycle state tracking (PLACED, ACCEPTED, PREPARING, READY, COMPLETED, CANCELLED)
 * - AJAX JSON response for dynamic status polling without page reloads
 */
@WebServlet(name = "OrderStatusServlet", urlPatterns = {"/order-status"})
public class OrderStatusServlet extends HttpServlet {

    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        this.orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr.trim());
            Order order = orderDAO.getOrderById(orderId);

            if (order == null || (!currentUser.isAdmin() && order.getUserId() != currentUser.getUserId())) {
                response.sendRedirect(request.getContextPath() + "/order-history?error=Order+not+found");
                return;
            }

            // Check if AJAX poll request
            String isAjax = request.getHeader("X-Requested-With");
            boolean ajax = "XMLHttpRequest".equalsIgnoreCase(isAjax) || "true".equalsIgnoreCase(request.getParameter("ajax"));
            if (ajax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(String.format("{\"orderId\":%d,\"status\":\"%s\",\"updatedAt\":\"%s\"}",
                    order.getOrderId(), order.getOrderStatus(), order.getUpdatedAt()));
                out.flush();
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/student/order-status.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/order-history");
        }
    }
}

