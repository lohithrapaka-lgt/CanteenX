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
import java.net.URLEncoder;
import java.util.List;

/**
 * Controller displaying order history for the logged-in student.
 */
@WebServlet(name = "OrderHistoryServlet", urlPatterns = {"/order-history"})
public class OrderHistoryServlet extends HttpServlet {

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

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=" +
                URLEncoder.encode("Please log in to view your orders.", "UTF-8"));
            return;
        }

        List<Order> userOrders = orderDAO.getOrdersByUser(currentUser.getUserId());
        request.setAttribute("orders", userOrders);
        request.getRequestDispatcher("/WEB-INF/views/student/order-history.jsp").forward(request, response);
    }
}
