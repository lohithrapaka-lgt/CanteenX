package com.canteenx.controller.admin;

import com.canteenx.dao.CategoryDAO;
import com.canteenx.dao.MenuItemDAO;
import com.canteenx.model.Category;
import com.canteenx.model.MenuItem;
import com.canteenx.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for Canteen Staff/Admin Food Menu and Category Management.
 * Handles adding, editing, deleting, toggling availability, and managing categories.
 * Enforces relational integrity to prevent accidental deletion of referenced items/categories.
 */
@WebServlet(name = "ManageMenuServlet", urlPatterns = {"/admin/menu"})
public class ManageMenuServlet extends HttpServlet {

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
        List<Category> categories = categoryDAO.getAllCategories();
        List<MenuItem> menuItems = menuItemDAO.getAllMenuItems();

        request.setAttribute("categories", categories);
        request.setAttribute("menuItems", menuItems);

        // Edit mode support
        String editIdStr = request.getParameter("editId");
        if (editIdStr != null) {
            try {
                int editId = Integer.parseInt(editIdStr);
                MenuItem editItem = menuItemDAO.getMenuItemById(editId);
                request.setAttribute("editItem", editItem);
            } catch (NumberFormatException ignored) {}
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/menu-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("ADD".equalsIgnoreCase(action)) {
            String categoryIdStr = request.getParameter("categoryId");
            String itemName = request.getParameter("itemName");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String imageUrl = request.getParameter("imageUrl");

            if (itemName == null || itemName.trim().isEmpty() || !ValidationUtil.isPositiveDecimal(priceStr)) {
                response.sendRedirect(request.getContextPath() + "/admin/menu?error=" +
                    java.net.URLEncoder.encode("Invalid item name or price. Please enter valid values.", "UTF-8"));
                return;
            }

            int categoryId = 1;
            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (Exception ignored) {}

            MenuItem item = new MenuItem();
            item.setCategoryId(categoryId);
            item.setItemName(itemName.trim());
            item.setDescription(description != null ? description.trim() : "");
            item.setPrice(new BigDecimal(priceStr.trim()));
            item.setAvailable(true);
            item.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : "assets/images/food-placeholder.jpg");

            boolean success = menuItemDAO.addMenuItem(item);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/menu?success=" +
                    java.net.URLEncoder.encode("Menu item '" + item.getItemName() + "' added successfully!", "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/menu?error=" +
                    java.net.URLEncoder.encode("Failed to save menu item to database.", "UTF-8"));
            }

        } else if ("UPDATE".equalsIgnoreCase(action)) {
            String itemIdStr = request.getParameter("itemId");
            String categoryIdStr = request.getParameter("categoryId");
            String itemName = request.getParameter("itemName");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String imageUrl = request.getParameter("imageUrl");
            String isAvailableStr = request.getParameter("isAvailable");

            try {
                int itemId = Integer.parseInt(itemIdStr);
                MenuItem item = menuItemDAO.getMenuItemById(itemId);
                if (item != null) {
                    if (itemName != null && !itemName.trim().isEmpty()) {
                        item.setItemName(itemName.trim());
                    }
                    if (categoryIdStr != null) {
                        item.setCategoryId(Integer.parseInt(categoryIdStr));
                    }
                    if (description != null) {
                        item.setDescription(description.trim());
                    }
                    if (ValidationUtil.isPositiveDecimal(priceStr)) {
                        item.setPrice(new BigDecimal(priceStr.trim()));
                    }
                    item.setAvailable("true".equalsIgnoreCase(isAvailableStr) || "on".equalsIgnoreCase(isAvailableStr));
                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        item.setImageUrl(imageUrl.trim());
                    }
                    menuItemDAO.updateMenuItem(item);
                    response.sendRedirect(request.getContextPath() + "/admin/menu?success=" +
                        java.net.URLEncoder.encode("Menu item '" + item.getItemName() + "' updated successfully!", "UTF-8"));
                    return;
                }
            } catch (Exception ignored) {}

            response.sendRedirect(request.getContextPath() + "/admin/menu?error=" +
                java.net.URLEncoder.encode("Error updating menu item. Check input values.", "UTF-8"));

        } else if ("TOGGLE_AVAILABILITY".equalsIgnoreCase(action)) {
            String itemIdStr = request.getParameter("itemId");
            String currentStatusStr = request.getParameter("currentStatus");
            try {
                int itemId = Integer.parseInt(itemIdStr);
                boolean currentStatus = Boolean.parseBoolean(currentStatusStr);
                menuItemDAO.toggleAvailability(itemId, !currentStatus);
            } catch (Exception ignored) {}

            response.sendRedirect(request.getContextPath() + "/admin/menu");

        } else if ("DELETE".equalsIgnoreCase(action)) {
            String itemIdStr = request.getParameter("itemId");
            try {
                int itemId = Integer.parseInt(itemIdStr);
                int status = menuItemDAO.deleteMenuItemSafely(itemId);
                if (status == 1) {
                    response.sendRedirect(request.getContextPath() + "/admin/menu?success=" +
                        java.net.URLEncoder.encode("Menu item deleted permanently.", "UTF-8"));
                } else if (status == 2) {
                    response.sendRedirect(request.getContextPath() + "/admin/menu?warning=" +
                        java.net.URLEncoder.encode("Item has past order history and cannot be deleted. It has been marked as Out of Stock instead.", "UTF-8"));
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/menu?error=" +
                        java.net.URLEncoder.encode("Failed to delete menu item.", "UTF-8"));
                }
                return;
            } catch (Exception ignored) {}

            response.sendRedirect(request.getContextPath() + "/admin/menu");

        } else if ("ADD_CATEGORY".equalsIgnoreCase(action)) {
            String catName = request.getParameter("categoryName");
            String catDesc = request.getParameter("description");
            if (catName != null && !catName.trim().isEmpty()) {
                if (categoryDAO.getCategoryByName(catName.trim()) != null) {
                    response.sendRedirect(request.getContextPath() + "/admin/menu?error=" +
                        java.net.URLEncoder.encode("A category with this name already exists.", "UTF-8"));
                    return;
                }
                Category cat = new Category(0, catName.trim(), catDesc != null ? catDesc.trim() : "");
                boolean ok = categoryDAO.addCategory(cat);
                if (ok) {
                    response.sendRedirect(request.getContextPath() + "/admin/menu?success=" +
                        java.net.URLEncoder.encode("New category '" + catName + "' created!", "UTF-8"));
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/menu?error=" +
                java.net.URLEncoder.encode("Failed to create category.", "UTF-8"));

        } else if ("DELETE_CATEGORY".equalsIgnoreCase(action)) {
            String catIdStr = request.getParameter("categoryId");
            try {
                int catId = Integer.parseInt(catIdStr);
                if (categoryDAO.isCategoryInUse(catId)) {
                    response.sendRedirect(request.getContextPath() + "/admin/menu?error=" +
                        java.net.URLEncoder.encode("Cannot delete category: existing food items are assigned to it. Reassign or remove the items first.", "UTF-8"));
                    return;
                }
                boolean deleted = categoryDAO.deleteCategory(catId);
                if (deleted) {
                    response.sendRedirect(request.getContextPath() + "/admin/menu?success=" +
                        java.net.URLEncoder.encode("Category deleted successfully.", "UTF-8"));
                    return;
                }
            } catch (Exception ignored) {}

            response.sendRedirect(request.getContextPath() + "/admin/menu?error=" +
                java.net.URLEncoder.encode("Failed to delete category.", "UTF-8"));
        }
    }
}
