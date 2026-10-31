package com.canteenx.controller.auth;

import com.canteenx.dao.UserDAO;
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
 * Controller handling user authentication.
 * Demonstrates:
 * - HttpServlet doGet / doPost
 * - HttpSession creation & attributes
 * - Cookies ("Remember Me" feature)
 * - URL Encoding & Request Dispatching
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

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
            } else {
                response.sendRedirect(request.getContextPath() + "/menu");
            }
            return;
        }

        // WT Syllabus Feature: Reading Cookies
        String rememberedEmail = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("canteenx_remembered_email".equals(c.getName())) {
                    rememberedEmail = c.getValue();
                    break;
                }
            }
        }
        request.setAttribute("rememberedEmail", rememberedEmail);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.authenticate(email.trim(), password.trim());
        if (user != null) {
            // WT Syllabus Feature: HTTP Session management
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            
            // Initialize empty Cart if not present
            if (session.getAttribute("cart") == null) {
                session.setAttribute("cart", new Cart());
            }

            // WT Syllabus Feature: Writing Cookies
            if ("on".equalsIgnoreCase(rememberMe) || "true".equalsIgnoreCase(rememberMe)) {
                Cookie cookie = new Cookie("canteenx_remembered_email", user.getEmail());
                cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                response.addCookie(cookie);
            } else {
                // Clear remembered cookie if unchecked
                Cookie cookie = new Cookie("canteenx_remembered_email", "");
                cookie.setMaxAge(0);
                cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                response.addCookie(cookie);
            }

            // Redirect according to role
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/menu");
            }
        } else {
            request.setAttribute("errorMessage", "Invalid email or password. Please try again.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}

