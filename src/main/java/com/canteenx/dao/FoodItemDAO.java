package com.canteenx.dao;

import com.canteenx.model.FoodItem;
import com.canteenx.model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementing JDBC CRUD operations for Food Items.
 * Uses PreparedStatement for secure query execution.
 */
public class FoodItemDAO {

    private final MenuItemDAO menuItemDAO = new MenuItemDAO();

    private FoodItem mapRow(ResultSet rs) throws SQLException {
        FoodItem item = new FoodItem();
        item.setItemId(rs.getInt("item_id"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setCategoryName(rs.getString("category_name"));
        item.setName(rs.getString("item_name"));
        item.setDescription(rs.getString("description"));
        item.setPrice(rs.getBigDecimal("price"));
        item.setAvailable(rs.getBoolean("is_available"));
        item.setImage(rs.getString("image_url"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        return item;
    }

    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> list = new ArrayList<>();
        List<MenuItem> menuItems = menuItemDAO.getAllMenuItems();
        for (MenuItem m : menuItems) {
            list.add(FoodItem.fromMenuItem(m));
        }
        return list;
    }

    public List<FoodItem> getAvailableFoodItems() {
        List<FoodItem> list = new ArrayList<>();
        List<MenuItem> menuItems = menuItemDAO.getAvailableMenuItems();
        for (MenuItem m : menuItems) {
            list.add(FoodItem.fromMenuItem(m));
        }
        return list;
    }

    public FoodItem getFoodItemById(int itemId) {
        MenuItem m = menuItemDAO.getMenuItemById(itemId);
        return FoodItem.fromMenuItem(m);
    }

    public boolean addFoodItem(FoodItem item) {
        if (item == null) return false;
        MenuItem m = item.toMenuItem();
        boolean ok = menuItemDAO.addMenuItem(m);
        if (ok) {
            item.setItemId(m.getItemId());
        }
        return ok;
    }

    public boolean updateFoodItem(FoodItem item) {
        if (item == null) return false;
        return menuItemDAO.updateMenuItem(item.toMenuItem());
    }

    public boolean updateAvailability(int itemId, boolean isAvailable) {
        return menuItemDAO.toggleAvailability(itemId, isAvailable);
    }

    public boolean deleteFoodItem(int itemId) {
        return menuItemDAO.deleteMenuItem(itemId);
    }

    public List<FoodItem> searchAndFilter(String keyword, Integer categoryId, Boolean availableOnly) {
        List<FoodItem> list = new ArrayList<>();
        List<MenuItem> menuItems = menuItemDAO.searchAndFilter(keyword, categoryId, availableOnly);
        for (MenuItem m : menuItems) {
            list.add(FoodItem.fromMenuItem(m));
        }
        return list;
    }
}
