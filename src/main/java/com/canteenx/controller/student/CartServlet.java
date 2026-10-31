package com.canteenx.controller.student;

import com.canteenx.dao.MenuItemDAO;
import com.canteenx.model.Cart;
import com.canteenx.model.MenuItem;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Controller for cart management (Add, Update, Remove, Clear).
 * Demonstrates:
 * - Session-based state management (Cart in HttpSession)
 * - Hidden form fields processing (action, itemId)
 * - Support for both dynamic jQuery AJAX calls and standard HTTP Postbacks
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private MenuItemDAO menuItemDAO;

    @Override
    public void init() throws ServletException {
        this.menuItemDAO = new MenuItemDAO();
    }

    private Cart getOrCreateCart(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = getOrCreateCart(request);
        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/views/student/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = getOrCreateCart(request);
        
        // Processing hidden form field parameter 'action'
        String action = request.getParameter("action");
        String isAjax = request.getHeader("X-Requested-With");
        boolean ajaxRequest = "XMLHttpRequest".equalsIgnoreCase(isAjax) || "true".equalsIgnoreCase(request.getParameter("ajax"));

        if ("ADD".equalsIgnoreCase(action)) {
            String itemIdStr = request.getParameter("itemId");
            String qtyStr = request.getParameter("quantity");
            int quantity = 1;
            try {
                if (qtyStr != null) quantity = Integer.parseInt(qtyStr);
                int itemId = Integer.parseInt(itemIdStr);
                MenuItem item = menuItemDAO.getMenuItemById(itemId);
                if (item != null && item.isAvailable()) {
                    cart.addItem(item, quantity);
                }
            } catch (Exception ignored) {}

        } else if ("UPDATE".equalsIgnoreCase(action)) {
            String itemIdStr = request.getParameter("itemId");
            String qtyStr = request.getParameter("quantity");
            try {
                int itemId = Integer.parseInt(itemIdStr);
                int quantity = Integer.parseInt(qtyStr);
                cart.updateQuantity(itemId, quantity);
            } catch (Exception ignored) {}

        } else if ("REMOVE".equalsIgnoreCase(action)) {
            String itemIdStr = request.getParameter("itemId");
            try {
                int itemId = Integer.parseInt(itemIdStr);
                cart.removeItem(itemId);
            } catch (Exception ignored) {}

        } else if ("CLEAR".equalsIgnoreCase(action)) {
            cart.clear();
        }

        if (ajaxRequest) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(String.format("{\"success\":true,\"totalCount\":%d,\"totalAmount\":%.2f}", 
                cart.getTotalItemCount(), cart.getTotalAmount()));
            out.flush();
        } else {
            response.sendRedirect(response.encodeURL(request.getContextPath() + "/cart"));
        }
    }
}

