package com.canteenx.dao;

import com.canteenx.model.Cart;
import com.canteenx.model.CartItem;
import com.canteenx.model.Order;
import com.canteenx.model.OrderItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO implementing Order management.
 * Demonstrates JDBC Transactions (ACID compliance):
 * - conn.setAutoCommit(false)
 * - conn.commit()
 * - conn.rollback()
 */
public class OrderDAO {

    /**
     * Places a customer order atomically using a JDBC transaction.
     * Inserts the order master record and all line items within a single transaction.
     */
    public int placeOrder(int userId, Cart cart, String paymentMethod, String specialInstructions) throws SQLException {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }

        String insertOrderSql = "INSERT INTO orders (user_id, total_amount, order_status, payment_method, " +
                                "payment_status, special_instructions) VALUES (?, ?, 'PLACED', ?, 'PENDING', ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, item_id, quantity, unit_price, subtotal) " +
                               "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psItem = null;
        ResultSet rsKeys = null;
        int generatedOrderId = -1;

        try {
            conn = DBConnection.getConnection();
            // BEGIN TRANSACTION
            conn.setAutoCommit(false);

            psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, userId);
            psOrder.setBigDecimal(2, cart.getTotalAmount());
            psOrder.setString(3, paymentMethod != null ? paymentMethod : "CASH_AT_COUNTER");
            psOrder.setString(4, specialInstructions != null ? specialInstructions.trim() : "");

            psOrder.executeUpdate();
            rsKeys = psOrder.getGeneratedKeys();
            if (rsKeys.next()) {
                generatedOrderId = rsKeys.getInt(1);
            } else {
                throw new SQLException("Failed to obtain generated order ID.");
            }

            // Batch insert order items
            psItem = conn.prepareStatement(insertItemSql);
            for (CartItem cartItem : cart.getItems()) {
                psItem.setInt(1, generatedOrderId);
                psItem.setInt(2, cartItem.getMenuItem().getItemId());
                psItem.setInt(3, cartItem.getQuantity());
                psItem.setBigDecimal(4, cartItem.getMenuItem().getPrice());
                psItem.setBigDecimal(5, cartItem.getSubtotal());
                psItem.addBatch();
            }
            psItem.executeBatch();

            // COMMIT TRANSACTION
            conn.commit();
            return generatedOrderId;

        } catch (SQLException e) {
            // ROLLBACK TRANSACTION ON FAILURE
            if (conn != null) {
                try {
                    System.err.println("[CanteenX] Transaction rollback initiated due to: " + e.getMessage());
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("[CanteenX] Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (rsKeys != null) try { rsKeys.close(); } catch (SQLException ignored) {}
            if (psOrder != null) try { psOrder.close(); } catch (SQLException ignored) {}
            if (psItem != null) try { psItem.close(); } catch (SQLException ignored) {}
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public Order getOrderById(int orderId) {
        String orderSql = "SELECT o.order_id, o.user_id, u.full_name, u.email, u.phone, o.total_amount, " +
                          "o.order_status, o.payment_method, o.payment_status, o.special_instructions, " +
                          "o.created_at, o.updated_at " +
                          "FROM orders o " +
                          "JOIN users u ON o.user_id = u.user_id " +
                          "WHERE o.order_id = ?";

        String itemsSql = "SELECT oi.order_item_id, oi.order_id, oi.item_id, m.item_name, " +
                          "oi.quantity, oi.unit_price, oi.subtotal " +
                          "FROM order_items oi " +
                          "JOIN menu_items m ON oi.item_id = m.item_id " +
                          "WHERE oi.order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psOrder = conn.prepareStatement(orderSql)) {
            psOrder.setInt(1, orderId);
            try (ResultSet rs = psOrder.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setUserName(rs.getString("full_name"));
                    order.setUserEmail(rs.getString("email"));
                    order.setUserPhone(rs.getString("phone"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setOrderStatus(rs.getString("order_status"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setSpecialInstructions(rs.getString("special_instructions"));
                    order.setCreatedAt(rs.getTimestamp("created_at"));
                    order.setUpdatedAt(rs.getTimestamp("updated_at"));

                    // Fetch line items
                    try (PreparedStatement psItems = conn.prepareStatement(itemsSql)) {
                        psItems.setInt(1, orderId);
                        try (ResultSet rsItems = psItems.executeQuery()) {
                            while (rsItems.next()) {
                                OrderItem item = new OrderItem(
                                    rsItems.getInt("order_item_id"),
                                    rsItems.getInt("order_id"),
                                    rsItems.getInt("item_id"),
                                    rsItems.getString("item_name"),
                                    rsItems.getInt("quantity"),
                                    rsItems.getBigDecimal("unit_price"),
                                    rsItems.getBigDecimal("subtotal")
                                );
                                order.addItem(item);
                            }
                        }
                    }
                    return order;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching order by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Order> getOrdersByUser(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT order_id FROM orders WHERE user_id = ? ORDER BY order_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = getOrderById(rs.getInt("order_id"));
                    if (o != null) {
                        list.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching user orders: " + e.getMessage());
        }
        return list;
    }

    public List<Order> getAllOrders(String statusFilter) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT order_id FROM orders ");
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("WHERE order_status = ? ");
        }
        sql.append("ORDER BY order_id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
                ps.setString(1, statusFilter.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = getOrderById(rs.getInt("order_id"));
                    if (o != null) {
                        list.add(o);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error fetching all orders: " + e.getMessage());
        }
        return list;
    }

    public static boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) return false;
        String cur = currentStatus.trim().toUpperCase();
        String next = newStatus.trim().toUpperCase();
        if (cur.equals(next)) return true;

        switch (cur) {
            case "PLACED":
                return "ACCEPTED".equals(next) || "CANCELLED".equals(next);
            case "ACCEPTED":
                return "PREPARING".equals(next) || "CANCELLED".equals(next);
            case "PREPARING":
                return "READY".equals(next) || "CANCELLED".equals(next);
            case "READY":
                return "COMPLETED".equals(next) || "CANCELLED".equals(next);
            default:
                return false; // Terminal states COMPLETED and CANCELLED cannot transition
        }
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        Order order = getOrderById(orderId);
        if (order == null) return false;

        String formattedStatus = newStatus != null ? newStatus.trim().toUpperCase() : "";
        if (!isValidStatusTransition(order.getOrderStatus(), formattedStatus)) {
            System.err.println("[CanteenX] Invalid status transition rejected: " + order.getOrderStatus() + " -> " + formattedStatus);
            return false;
        }

        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, formattedStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error updating order status: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getSalesMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        String sql = "SELECT " +
                     "COUNT(*) AS total_orders, " +
                     "COALESCE(SUM(CASE WHEN order_status != 'CANCELLED' THEN total_amount ELSE 0 END), 0) AS total_revenue, " +
                     "SUM(CASE WHEN order_status IN ('PLACED', 'ACCEPTED') THEN 1 ELSE 0 END) AS pending_orders, " +
                     "SUM(CASE WHEN order_status = 'PREPARING' THEN 1 ELSE 0 END) AS preparing_orders, " +
                     "SUM(CASE WHEN order_status = 'READY' THEN 1 ELSE 0 END) AS ready_orders, " +
                     "SUM(CASE WHEN order_status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_orders " +
                     "FROM orders";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                metrics.put("totalOrders", rs.getInt("total_orders"));
                metrics.put("totalRevenue", rs.getBigDecimal("total_revenue"));
                metrics.put("pendingOrders", rs.getInt("pending_orders"));
                metrics.put("preparingOrders", rs.getInt("preparing_orders"));
                metrics.put("readyOrders", rs.getInt("ready_orders"));
                metrics.put("completedOrders", rs.getInt("completed_orders"));
            }
        } catch (SQLException e) {
            System.err.println("[CanteenX] Error calculating sales metrics: " + e.getMessage());
            metrics.put("totalOrders", 0);
            metrics.put("totalRevenue", BigDecimal.ZERO);
            metrics.put("pendingOrders", 0);
            metrics.put("preparingOrders", 0);
            metrics.put("readyOrders", 0);
            metrics.put("completedOrders", 0);
        }
        return metrics;
    }
}
