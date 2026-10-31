<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.canteenx.model.Order"%>
<%@ page import="com.canteenx.model.OrderItem"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    Map<String, Object> metrics = (Map<String, Object>) request.getAttribute("metrics");
    List<Order> recentOrders = (List<Order>) request.getAttribute("recentOrders");
    Integer totalMenuItems = (Integer) request.getAttribute("totalMenuItems");
    String ctx = request.getContextPath();

    BigDecimal totalRevenue = metrics != null ? (BigDecimal) metrics.get("totalRevenue") : BigDecimal.ZERO;
    int totalOrders = metrics != null ? (Integer) metrics.get("totalOrders") : 0;
    int pendingOrders = metrics != null ? (Integer) metrics.get("pendingOrders") : 0;
    int preparingOrders = metrics != null ? (Integer) metrics.get("preparingOrders") : 0;
    int readyOrders = metrics != null ? (Integer) metrics.get("readyOrders") : 0;
    int completedOrders = metrics != null ? (Integer) metrics.get("completedOrders") : 0;
%>

<div class="row align-items-center mb-4">
    <div class="col-md-8">
        <h2 class="fw-bold mb-1">Canteen Operations Dashboard 📊</h2>
        <p class="text-muted mb-0">Overview of campus canteen revenue, current kitchen queue, and order fulfillment.</p>
    </div>
    <div class="col-md-4 text-md-end mt-3 mt-md-0">
        <a href="<%= response.encodeURL(ctx + "/admin/orders") %>" class="btn btn-warning fw-bold px-3">
            📋 Order Queue (<%= pendingOrders + preparingOrders %> Active)
        </a>
    </div>
</div>

<!-- Metric Tiles Row -->
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-lg-3">
        <div class="metric-card border-start border-4 border-success">
            <div class="metric-label">Total Revenue</div>
            <div class="metric-value text-success">₹<%= String.format("%.2f", totalRevenue) %></div>
            <small class="text-muted"><%= completedOrders %> completed orders</small>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="metric-card border-start border-4 border-primary">
            <div class="metric-label">Total Orders Placed</div>
            <div class="metric-value text-primary"><%= totalOrders %></div>
            <small class="text-muted">Lifetime canteen orders</small>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="metric-card border-start border-4 border-warning">
            <div class="metric-label">In Kitchen / Preparing</div>
            <div class="metric-value text-warning"><%= preparingOrders %></div>
            <small class="text-muted"><%= pendingOrders %> waiting acceptance</small>
        </div>
    </div>
    <div class="col-sm-6 col-lg-3">
        <div class="metric-card border-start border-4 border-info">
            <div class="metric-label">Ready at Counter</div>
            <div class="metric-value text-info"><%= readyOrders %></div>
            <small class="text-muted">Awaiting student collection</small>
        </div>
    </div>
</div>

<!-- Quick Shortcuts -->
<div class="row g-3 mb-4">
    <div class="col-md-6">
        <div class="card shadow-sm border-0 h-100 p-3">
            <div class="d-flex align-items-center gap-3">
                <div class="display-6 text-primary">📋</div>
                <div>
                    <h5 class="fw-bold mb-1">Order Management</h5>
                    <p class="text-muted small mb-2">Accept orders, move to kitchen, notify ready status.</p>
                    <a href="<%= response.encodeURL(ctx + "/admin/orders") %>" class="btn btn-sm btn-outline-primary">Open Queue &rarr;</a>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-6">
        <div class="card shadow-sm border-0 h-100 p-3">
            <div class="d-flex align-items-center gap-3">
                <div class="display-6 text-warning">🍲</div>
                <div>
                    <h5 class="fw-bold mb-1">Menu Catalog</h5>
                    <p class="text-muted small mb-2"><%= totalMenuItems != null ? totalMenuItems : 0 %> items configured. Add, edit, or toggle stock.</p>
                    <a href="<%= response.encodeURL(ctx + "/admin/menu") %>" class="btn btn-sm btn-outline-warning text-dark">Manage Menu &rarr;</a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Recent Orders Table -->
<div class="card shadow-sm border-0 rounded-3 mb-4">
    <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
        <h5 class="mb-0 fw-bold">Recent Orders</h5>
        <a href="<%= response.encodeURL(ctx + "/admin/orders") %>" class="btn btn-sm btn-link text-decoration-none">View All Orders &rarr;</a>
    </div>
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
                <tr>
                    <th>Order ID</th>
                    <th>Student</th>
                    <th>Items</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if (recentOrders != null && !recentOrders.isEmpty()) {
                    for (Order o : recentOrders) { %>
                        <tr>
                            <td><strong class="text-primary">#<%= o.getOrderId() %></strong></td>
                            <td>
                                <div><%= o.getUserName() %></div>
                                <small class="text-muted"><%= o.getUserPhone() %></small>
                            </td>
                            <td>
                                <small>
                                    <% if (o.getItems() != null) {
                                        for (int i = 0; i < o.getItems().size(); i++) {
                                            OrderItem oi = o.getItems().get(i);
                                    %>
                                        <%= oi.getItemName() %> (&times;<%= oi.getQuantity() %>)<%= i < o.getItems().size() - 1 ? ", " : "" %>
                                    <%   }
                                       } %>
                                </small>
                            </td>
                            <td class="fw-bold">₹<%= String.format("%.2f", o.getTotalAmount()) %></td>
                            <td>
                                <span class="badge badge-status-<%= o.getOrderStatus() %>">
                                    <%= o.getOrderStatus() %>
                                </span>
                            </td>
                            <td>
                                <a href="<%= response.encodeURL(ctx + "/admin/orders") %>" class="btn btn-sm btn-outline-secondary">
                                    Manage
                                </a>
                            </td>
                        </tr>
                <%  }
                   } else { %>
                    <tr>
                        <td colspan="6" class="text-center py-4 text-muted">No orders found in the database.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
