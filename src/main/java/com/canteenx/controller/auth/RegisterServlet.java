package com.canteenx.controller.auth;

import com.canteenx.dao.UserDAO;
import com.canteenx.model.User;
import com.canteenx.util.PasswordUtil;
import com.canteenx.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller handling Student Registration.
 * Demonstrates:
 * - Regular Expression Validation (Server-side)
 * - Form Validation error reporting
 * - Model creation & DAO interaction
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Retain entered values for form repopulation on error
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);

        // Server-side Regex & Form Validation (WT Syllabus Requirement)
        if (!ValidationUtil.isValidName(fullName)) {
            request.setAttribute("errorMessage", "Full Name must contain 2-100 alphabetic characters and spaces only.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("errorMessage", "Please provide a valid email address (e.g., student@college.edu).");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            request.setAttribute("errorMessage", "Phone number must be a valid 10-digit mobile number.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            request.setAttribute("errorMessage", "Password must be at least 6 characters long.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            request.setAttribute("errorMessage", "Passwords do not match. Please verify.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (userDAO.isEmailRegistered(email)) {
            request.setAttribute("errorMessage", "An account with this email already exists. Please log in.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Create User JavaBean
        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPhone(phone.trim());
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        newUser.setRole("STUDENT");

        boolean success = userDAO.registerUser(newUser);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/login?success=" + 
                java.net.URLEncoder.encode("Registration successful! You may now log in.", "UTF-8"));
        } else {
            request.setAttribute("errorMessage", "Database error occurred during registration. Please try again later.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}

