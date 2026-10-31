package com.canteenx.controller.auth;

import jakarta.servlet.annotation.WebServlet;

/**
 * RegistrationServlet alias mapping for /registration.
 */
@WebServlet(name = "RegistrationServlet", urlPatterns = {"/registration"})
public class RegistrationServlet extends RegisterServlet {
    private static final long serialVersionUID = 1L;
}

