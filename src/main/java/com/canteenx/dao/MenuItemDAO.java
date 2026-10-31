package com.canteenx.dao;

import com.canteenx.model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementing JDBC CRUD operations for Food Menu Items.
 */
public class MenuItemDAO {

    private MenuItem mapRow(ResultSet rs) throws SQLException {
        MenuItem item = new MenuItem();
        item.setItemId(rs.getInt("item_id"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setCategoryName(rs.getString("category_name"));
        item.setItemName(rs.getString("item_name"));
        item.setDescription(rs.getString("description"));
        item.setPrice(rs.getBigDecimal("price"));
        item.setAvailable(rs.getBoolean("is_available"));
        item.setImageUrl(rs.getString("image_url"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        return item;
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT m.item_id, m.category_id, c.category_name, m.item_name, m.description, " +
                     "m.price, m.is_available, m.image_url, m.created_at " +
                     "FROM menu_items m " +
                     "JOIN categories c ON m.category_id = c.category_id " +
                     "ORDER BY m.category_id ASC, m.item_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching menu items: " + e.getMessage());
        }
        return list;
    }

    public List<MenuItem> getAvailableMenuItems() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT m.item_id, m.category_id, c.category_name, m.item_name, m.description, " +
                     "m.price, m.is_available, m.image_url, m.created_at " +
                     "FROM menu_items m " +
                     "JOIN categories c ON m.category_id = c.category_id " +
                     "WHERE m.is_available = TRUE " +
                     "ORDER BY m.category_id ASC, m.item_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching available items: " + e.getMessage());
        }
        return list;
    }

    public List<MenuItem> searchAndFilter(String keyword, Integer categoryId, Boolean availableOnly) {
        List<MenuItem> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT m.item_id, m.category_id, c.category_name, m.item_name, m.description, " +
            "m.price, m.is_available, m.image_url, m.created_at " +
            "FROM menu_items m " +
            "JOIN categories c ON m.category_id = c.category_id WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (availableOnly != null && availableOnly) {
            sql.append(" AND m.is_available = TRUE ");
        }

        if (categoryId != null && categoryId > 0) {
            sql.append(" AND m.category_id = ? ");
            params.add(categoryId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(m.item_name) LIKE ? OR LOWER(m.description) LIKE ?) ");
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            params.add(likePattern);
            params.add(likePattern);
        }

        sql.append(" ORDER BY m.category_id ASC, m.item_name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error filtering menu items: " + e.getMessage());
        }
        return list;
    }

    public MenuItem getMenuItemById(int itemId) {
        String sql = "SELECT m.item_id, m.category_id, c.category_name, m.item_name, m.description, " +
                     "m.price, m.is_available, m.image_url, m.created_at " +
                     "FROM menu_items m " +
                     "JOIN categories c ON m.category_id = c.category_id " +
                     "WHERE m.item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching item by ID: " + e.getMessage());
        }
        return null;
    }

    public MenuItem getMenuItemByName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT m.item_id, m.category_id, c.category_name, m.item_name, m.description, " +
                     "m.price, m.is_available, m.image_url, m.created_at " +
                     "FROM menu_items m " +
                     "JOIN categories c ON m.category_id = c.category_id " +
                     "WHERE LOWER(m.item_name) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching item by name: " + e.getMessage());
        }
        return null;
    }

    public boolean addMenuItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (category_id, item_name, description, price, is_available, image_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getCategoryId());
            ps.setString(2, item.getItemName());
            ps.setString(3, item.getDescription());
            ps.setBigDecimal(4, item.getPrice());
            ps.setBoolean(5, item.isAvailable());
            ps.setString(6, item.getImageUrl() != null && !item.getImageUrl().trim().isEmpty() 
                            ? item.getImageUrl() : "assets/images/food-placeholder.jpg");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        item.setItemId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error adding menu item: " + e.getMessage());
        }
        return false;
    }

    public boolean updateMenuItem(MenuItem item) {
        String sql = "UPDATE menu_items SET category_id = ?, item_name = ?, description = ?, " +
                     "price = ?, is_available = ?, image_url = ? WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getCategoryId());
            ps.setString(2, item.getItemName());
            ps.setString(3, item.getDescription());
            ps.setBigDecimal(4, item.getPrice());
            ps.setBoolean(5, item.isAvailable());
            ps.setString(6, item.getImageUrl());
            ps.setInt(7, item.getItemId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error updating menu item: " + e.getMessage());
            return false;
        }
    }

    public boolean toggleAvailability(int itemId, boolean isAvailable) {
        String sql = "UPDATE menu_items SET is_available = ? WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isAvailable);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error toggling item availability: " + e.getMessage());
            return false;
        }
    }

    public boolean isItemOrdered(int itemId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error checking if item is ordered: " + e.getMessage());
        }
        return false;
    }

    /**
     * Safely deletes a menu item. If the item has existing order history,
     * it prevents foreign key constraint violation by toggling is_available to false.
     * Returns 1 if deleted, 2 if archived/disabled due to order history, 0 on failure.
     */
    public int deleteMenuItemSafely(int itemId) {
        if (isItemOrdered(itemId)) {
            // Cannot delete due to foreign key integrity - archive by disabling availability
            toggleAvailability(itemId, false);
            return 2;
        }
        String sql = "DELETE FROM menu_items WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error deleting menu item: " + e.getMessage());
            // Fallback safety if DB throws constraint violation
            toggleAvailability(itemId, false);
            return 2;
        }
    }

    public boolean deleteMenuItem(int itemId) {
        return deleteMenuItemSafely(itemId) > 0;
    }
}
