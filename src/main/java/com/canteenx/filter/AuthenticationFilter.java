package com.canteenx.filter;

import com.canteenx.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Filter that enforces authentication and role-based access control.
 * Protected student areas: /checkout, /order-history, /order-status
 * Protected admin areas: /admin/*, /admin-dashboard.jsp, /admin-food.jsp, /admin-orders.jsp
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
    "/admin/*", "/admin-dashboard.jsp", "/admin-food.jsp", "/admin-orders.jsp",
    "/checkout", "/checkout.jsp", "/order-history", "/order-history.jsp", "/order-status", "/order-status.jsp"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath))
                      ? uri.substring(contextPath.length()) : uri;

        // 1. Guard Admin Routes
        if (path.startsWith("/admin/") || path.startsWith("/admin-dashboard.jsp") || 
            path.startsWith("/admin-food.jsp") || path.startsWith("/admin-orders.jsp")) {
            
            // Allow admin login page and login submission
            if (path.equals("/admin/login") || path.equals("/admin-login") || path.equals("/admin-login.jsp")) {
                chain.doFilter(request, response);
                return;
            }

            if (currentUser == null) {
                res.sendRedirect(contextPath + "/admin-login.jsp?error=" + 
                    URLEncoder.encode("Please log in as an administrator to continue.", "UTF-8"));
                return;
            }

            if (!currentUser.isAdmin()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator privileges required.");
                return;
            }
        }

        // 2. Guard Authenticated Student Routes
        if (path.startsWith("/checkout") || path.startsWith("/order-history") || path.startsWith("/order-status")) {
            if (currentUser == null) {
                res.sendRedirect(contextPath + "/login?error=" + 
                    URLEncoder.encode("Please log in to manage or view your orders.", "UTF-8"));
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
