package com.canteenx.controller.student;

import com.canteenx.dao.CategoryDAO;
import com.canteenx.dao.MenuItemDAO;
import com.canteenx.model.Category;
import com.canteenx.model.MenuItem;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for browsing food menu, searching items, and viewing item details.
 * Demonstrates:
 * - MVC Controller orchestrating DAO models and forwarding to JSP view
 * - URL Rewriting with response.encodeURL
 */
@WebServlet(name = "MenuServlet", urlPatterns = {"/menu", "/item-details"})
public class MenuServlet extends HttpServlet {

    private MenuItemDAO menuItemDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        this.menuItemDAO = new MenuItemDAO();
        this.categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        // 1. Single Item Detail View
        if ("/item-details".equals(servletPath)) {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    int itemId = Integer.parseInt(idParam);
                    MenuItem item = menuItemDAO.getMenuItemById(itemId);
                    if (item != null) {
                        request.setAttribute("item", item);
                        request.getRequestDispatcher("/WEB-INF/views/student/item-details.jsp").forward(request, response);
                        return;
                    }
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(response.encodeURL(request.getContextPath() + "/menu"));
            return;
        }

        // 2. Menu Catalog with Category Filtering & Search
        String categoryParam = request.getParameter("category");
        String searchParam = request.getParameter("q");

        Integer selectedCategoryId = null;
        if (categoryParam != null && !categoryParam.trim().isEmpty()) {
            try {
                selectedCategoryId = Integer.parseInt(categoryParam.trim());
            } catch (NumberFormatException ignored) {}
        }

        List<Category> categories = categoryDAO.getAllCategories();
        List<MenuItem> menuItems = menuItemDAO.searchAndFilter(searchParam, selectedCategoryId, true);

        request.setAttribute("categories", categories);
        request.setAttribute("menuItems", menuItems);
        request.setAttribute("selectedCategory", selectedCategoryId);
        request.setAttribute("searchQuery", searchParam != null ? searchParam.trim() : "");

        // Demonstration of URL Rewriting: provide an encoded self-link in the request
        String encodedMenuUrl = response.encodeURL(request.getContextPath() + "/menu");
        request.setAttribute("encodedMenuUrl", encodedMenuUrl);

        request.getRequestDispatcher("/WEB-INF/views/student/menu.jsp").forward(request, response);
    }
}

