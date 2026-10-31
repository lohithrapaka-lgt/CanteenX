package com.canteenx.dao;

import com.canteenx.model.Admin;
import com.canteenx.model.User;
import com.canteenx.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementing PreparedStatement-based CRUD operations for Admins.
 */
public class AdminDAO {

    public Admin validateLogin(String email, String plainPassword) {
        String sql = "SELECT user_id, full_name, email, password_hash, role, created_at " +
                     "FROM users WHERE LOWER(email) = LOWER(?) AND role = 'ADMIN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordUtil.checkPassword(plainPassword, storedHash)) {
                        Admin admin = new Admin();
                        admin.setAdminId(rs.getInt("user_id"));
                        admin.setName(rs.getString("full_name"));
                        admin.setEmail(rs.getString("email"));
                        admin.setPassword(storedHash);
                        admin.setCreatedAt(rs.getTimestamp("created_at"));
                        return admin;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error validating admin login: " + e.getMessage());
        }
        return null;
    }

    public Admin getAdminById(int adminId) {
        String sql = "SELECT user_id, full_name, email, password_hash, created_at " +
                     "FROM users WHERE user_id = ? AND role = 'ADMIN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getInt("user_id"));
                    admin.setName(rs.getString("full_name"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPassword(rs.getString("password_hash"));
                    admin.setCreatedAt(rs.getTimestamp("created_at"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching admin by ID: " + e.getMessage());
        }
        return null;
    }

    public Admin getAdminByEmail(String email) {
        String sql = "SELECT user_id, full_name, email, password_hash, created_at " +
                     "FROM users WHERE LOWER(email) = LOWER(?) AND role = 'ADMIN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getInt("user_id"));
                    admin.setName(rs.getString("full_name"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPassword(rs.getString("password_hash"));
                    admin.setCreatedAt(rs.getTimestamp("created_at"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching admin by email: " + e.getMessage());
        }
        return null;
    }

    public boolean createAdmin(Admin admin) {
        String sql = "INSERT INTO users (full_name, email, phone, password_hash, role) VALUES (?, ?, '9999999999', ?, 'ADMIN')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getName());
            ps.setString(2, admin.getEmail().toLowerCase());
            ps.setString(3, PasswordUtil.hashPassword(admin.getPassword()));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        admin.setAdminId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error creating admin: " + e.getMessage());
        }
        return false;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT user_id, full_name, email, password_hash, created_at FROM users WHERE role = 'ADMIN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("user_id"));
                admin.setName(rs.getString("full_name"));
                admin.setEmail(rs.getString("email"));
                admin.setPassword(rs.getString("password_hash"));
                admin.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(admin);
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error listing admins: " + e.getMessage());
        }
        return list;
    }
}
