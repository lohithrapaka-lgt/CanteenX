package com.canteenx.controller.student;

import com.canteenx.dao.MenuItemDAO;
import com.canteenx.dao.OrderDAO;
import com.canteenx.model.Cart;
import com.canteenx.model.CartItem;
import com.canteenx.model.MenuItem;
import com.canteenx.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller handling checkout and transactional order placement.
 * Demonstrates:
 * - Session access to Cart and logged-in User
 * - Server-side validation of prices, stock availability, and quantities against database
 * - Calling DAO layer running JDBC Transactions (commit/rollback)
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

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
        HttpSession session = request.getSession(false);
        Cart cart = (session != null) ? (Cart) session.getAttribute("cart") : null;

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(response.encodeURL(request.getContextPath() + "/cart?error=" +
                java.net.URLEncoder.encode("Your cart is empty. Please add food items before checkout.", "UTF-8")));
            return;
        }

        // Server-side synchronization of prices & stock verification
        for (CartItem ci : cart.getItems()) {
            MenuItem dbItem = menuItemDAO.getMenuItemById(ci.getMenuItem().getItemId());
            if (dbItem != null) {
                ci.getMenuItem().setPrice(dbItem.getPrice());
                ci.getMenuItem().setAvailable(dbItem.isAvailable());
            }
        }

        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/views/student/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=" + 
                java.net.URLEncoder.encode("Please log in to place an order.", "UTF-8"));
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Strict Server-Side Validation: Verify item availability, quantities, and current prices
        for (CartItem ci : cart.getItems()) {
            if (ci.getQuantity() <= 0 || ci.getQuantity() > 50) {
                request.setAttribute("errorMessage", "Invalid quantity (" + ci.getQuantity() + ") for item: " + ci.getMenuItem().getItemName());
                request.setAttribute("cart", cart);
                request.getRequestDispatcher("/WEB-INF/views/student/checkout.jsp").forward(request, response);
                return;
            }

            MenuItem dbItem = menuItemDAO.getMenuItemById(ci.getMenuItem().getItemId());
            if (dbItem == null) {
                request.setAttribute("errorMessage", "Item '" + ci.getMenuItem().getItemName() + "' is no longer available in the canteen.");
                request.setAttribute("cart", cart);
                request.getRequestDispatcher("/WEB-INF/views/student/checkout.jsp").forward(request, response);
                return;
            }

            if (!dbItem.isAvailable()) {
                request.setAttribute("errorMessage", "Item '" + dbItem.getItemName() + "' just sold out. Please remove it from your cart.");
                request.setAttribute("cart", cart);
                request.getRequestDispatcher("/WEB-INF/views/student/checkout.jsp").forward(request, response);
                return;
            }

            // Authoritative server-side price override to prevent client tampering
            ci.getMenuItem().setPrice(dbItem.getPrice());
            ci.getMenuItem().setItemName(dbItem.getItemName());
        }

        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.trim().isEmpty() ||
            (!"CASH_AT_COUNTER".equalsIgnoreCase(paymentMethod.trim()) && !"CAMPUS_WALLET".equalsIgnoreCase(paymentMethod.trim()))) {
            paymentMethod = "CASH_AT_COUNTER";
        } else {
            paymentMethod = paymentMethod.trim().toUpperCase();
        }

        String specialInstructions = request.getParameter("specialInstructions");
        if (specialInstructions != null && specialInstructions.length() > 255) {
            specialInstructions = specialInstructions.substring(0, 255);
        }

        try {
            // Atomic transaction execution in OrderDAO
            int orderId = orderDAO.placeOrder(user.getUserId(), cart, paymentMethod, specialInstructions);
            
            // Order placed successfully: reset student's cart
            cart.clear();

            // Redirect to live order status tracker
            response.sendRedirect(response.encodeURL(request.getContextPath() + "/order-status?orderId=" + orderId + 
                "&placed=true"));

        } catch (SQLException e) {
            System.err.println("[CanteenX] Checkout transaction failed: " + e.getMessage());
            request.setAttribute("errorMessage", "Failed to place order due to a database transaction error. Please try again.");
            request.setAttribute("cart", cart);
            request.getRequestDispatcher("/WEB-INF/views/student/checkout.jsp").forward(request, response);
        }
    }
}
