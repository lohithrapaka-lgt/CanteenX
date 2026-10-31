package com.canteenx.controller.admin;

import com.canteenx.dao.AdminDAO;
import com.canteenx.dao.UserDAO;
import com.canteenx.model.Admin;
import com.canteenx.model.Cart;
import com.canteenx.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller handling dedicated Admin Login.
 */
@WebServlet(name = "AdminLoginServlet", urlPatterns = {"/admin/login", "/admin-login"})
public class AdminLoginServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            }
        }
        request.getRequestDispatcher("/admin-login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required.");
            request.getRequestDispatcher("/admin-login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.authenticate(email.trim(), password.trim());
        if (user != null && user.isAdmin()) {
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            if (session.getAttribute("cart") == null) {
                session.setAttribute("cart", new Cart());
            }

            if ("on".equalsIgnoreCase(rememberMe) || "true".equalsIgnoreCase(rememberMe)) {
                Cookie cookie = new Cookie("canteenx_admin_email", user.getEmail());
                cookie.setMaxAge(7 * 24 * 60 * 60);
                cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                response.addCookie(cookie);
            }

            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            request.setAttribute("errorMessage", "Invalid administrator credentials or unauthorized role.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/admin-login.jsp").forward(request, response);
        }
    }
}

