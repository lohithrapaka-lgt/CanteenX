package com.canteenx.dao;

import com.canteenx.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementing category lookups and management.
 */
public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, category_name, description FROM categories ORDER BY category_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryId(rs.getInt("category_id"));
                cat.setCategoryName(rs.getString("category_name"));
                cat.setDescription(rs.getString("description"));
                list.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching categories: " + e.getMessage());
        }
        return list;
    }

    public Category getCategoryById(int categoryId) {
        String sql = "SELECT category_id, category_name, description FROM categories WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category cat = new Category();
                    cat.setCategoryId(rs.getInt("category_id"));
                    cat.setCategoryName(rs.getString("category_name"));
                    cat.setDescription(rs.getString("description"));
                    return cat;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching category by ID: " + e.getMessage());
        }
        return null;
    }

    public Category getCategoryByName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT category_id, category_name, description FROM categories WHERE LOWER(category_name) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category cat = new Category();
                    cat.setCategoryId(rs.getInt("category_id"));
                    cat.setCategoryName(rs.getString("category_name"));
                    cat.setDescription(rs.getString("description"));
                    return cat;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching category by name: " + e.getMessage());
        }
        return null;
    }

    public boolean addCategory(Category category) {
        if (category == null || category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            return false;
        }
        String sql = "INSERT INTO categories (category_name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getCategoryName().trim());
            ps.setString(2, category.getDescription() != null ? category.getDescription().trim() : "");
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        category.setCategoryId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error adding category: " + e.getMessage());
        }
        return false;
    }

    public boolean updateCategory(Category category) {
        if (category == null || category.getCategoryId() <= 0) {
            return false;
        }
        String sql = "UPDATE categories SET category_name = ?, description = ? WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName().trim());
            ps.setString(2, category.getDescription() != null ? category.getDescription().trim() : "");
            ps.setInt(3, category.getCategoryId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error updating category: " + e.getMessage());
            return false;
        }
    }

    public boolean isCategoryInUse(int categoryId) {
        String sql = "SELECT COUNT(*) FROM menu_items WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error checking category usage: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCategory(int categoryId) {
        if (isCategoryInUse(categoryId)) {
            return false; // Prevent accidental deletion of categories with menu items
        }
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error deleting category: " + e.getMessage());
            return false;
        }
    }
}
