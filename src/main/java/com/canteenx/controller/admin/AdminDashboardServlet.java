package com.canteenx.controller.admin;

import com.canteenx.dao.MenuItemDAO;
import com.canteenx.dao.OrderDAO;
import com.canteenx.model.Order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Admin Dashboard.
 * Displays overall sales stats, order statuses, and quick management links.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private OrderDAO orderDAO;
    private MenuItemDAO menuItemDAO;

    @Override
    public void init() throws ServletException {
        this.orderDAO = new OrderDAO();
        this.menuItemDAO = new MenuItemDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> metrics = orderDAO.getSalesMetrics();
        List<Order> recentOrders = orderDAO.getAllOrders("ALL");
        int totalMenuItems = menuItemDAO.getAllMenuItems().size();

        request.setAttribute("metrics", metrics);
        request.setAttribute("recentOrders", recentOrders.size() > 10 ? recentOrders.subList(0, 10) : recentOrders);
        request.setAttribute("totalMenuItems", totalMenuItems);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}

