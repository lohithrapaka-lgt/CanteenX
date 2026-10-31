<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.canteenx.model.Order"%>
<%@ page import="com.canteenx.model.OrderItem"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    String currentFilter = (String) request.getAttribute("currentFilter");
    if (currentFilter == null) currentFilter = "ALL";
    String ctx = request.getContextPath();

    String[] filterList = new String[]{"ALL", "PLACED", "ACCEPTED", "PREPARING", "READY", "COMPLETED", "CANCELLED"};
%>

<div class="row align-items-center mb-4">
    <div class="col-12">
        <h2 class="fw-bold mb-1">Canteen Kitchen &amp; Order Queue 📋</h2>
        <p class="text-muted mb-0">Progress orders through the kitchen lifecycle: Accept &rarr; Prepare &rarr; Ready &rarr; Complete.</p>
    </div>
</div>

<!-- Filter Tabs -->
<div class="mb-4">
    <div class="d-flex flex-wrap gap-2">
        <% for (String f : filterList) {
            boolean active = f.equalsIgnoreCase(currentFilter);
        %>
            <a href="<%= response.encodeURL(ctx + "/admin/orders?filter=" + f) %>" 
               class="btn btn-sm <%= active ? "btn-dark" : "btn-outline-dark" %> rounded-pill px-3">
                <%= f %>
            </a>
        <% } %>
    </div>
</div>

<!-- Orders Table -->
<div class="card shadow-sm border-0 rounded-3">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
                <tr>
                    <th>Order #</th>
                    <th>Student Info</th>
                    <th>Ordered Items</th>
                    <th>Total</th>
                    <th>Payment</th>
                    <th>Status</th>
                    <th style="min-width: 220px;">Lifecycle Transition</th>
                </tr>
            </thead>
            <tbody>
                <% if (orders != null && !orders.isEmpty()) {
                    for (Order o : orders) {
                        String st = o.getOrderStatus();
                %>
                    <tr>
                        <td>
                            <strong class="text-primary fs-6">#<%= o.getOrderId() %></strong>
                            <div class="small text-muted"><%= o.getCreatedAt() %></div>
                        </td>
                        <td>
                            <div class="fw-semibold"><%= o.getUserName() %></div>
                            <small class="text-muted"><%= o.getUserEmail() %> &bull; <%= o.getUserPhone() %></small>
                            <% if (o.getSpecialInstructions() != null && !o.getSpecialInstructions().isEmpty()) { %>
                                <div class="badge bg-warning text-dark mt-1 d-block text-start text-wrap">
                                    Note: <%= o.getSpecialInstructions() %>
                                </div>
                            <% } %>
                        </td>
                        <td>
                            <small>
                                <% if (o.getItems() != null) {
                                    for (OrderItem oi : o.getItems()) { %>
                                        <div>&bull; <%= oi.getItemName() %> (&times;<%= oi.getQuantity() %>)</div>
                                <%  }
                                } %>
                            </small>
                        </td>
                        <td class="fw-bold text-nowrap">₹<%= String.format("%.2f", o.getTotalAmount()) %></td>
                        <td>
                            <span class="badge bg-light text-dark border">
                                <%= o.getPaymentMethod().replace("_", " ") %>
                            </span>
                        </td>
                        <td>
                            <span class="badge badge-status-<%= st %> fs-6">
                                <%= st %>
                            </span>
                        </td>
                        <td>
                            <!-- WT Concept: Hidden Form Fields for Order Lifecycle Transition -->
                            <form action="<%= response.encodeURL(ctx + "/admin/orders") %>" method="POST" class="d-flex flex-wrap gap-1">
                                <input type="hidden" name="action" value="UPDATE_STATUS">
                                <input type="hidden" name="orderId" value="<%= o.getOrderId() %>">
                                <input type="hidden" name="currentFilter" value="<%= currentFilter %>">

                                <% if ("PLACED".equals(st)) { %>
                                    <button type="submit" name="newStatus" value="ACCEPTED" class="btn btn-sm btn-primary">
                                        ✓ Accept
                                    </button>
                                    <button type="submit" name="newStatus" value="CANCELLED" class="btn btn-sm btn-outline-danger" 
                                            onclick="return confirm('Reject this order?');">
                                        ✕ Reject
                                    </button>
                                <% } else if ("ACCEPTED".equals(st)) { %>
                                    <button type="submit" name="newStatus" value="PREPARING" class="btn btn-sm btn-warning fw-semibold">
                                        👨‍🍳 Start Cooking
                                    </button>
                                <% } else if ("PREPARING".equals(st)) { %>
                                    <button type="submit" name="newStatus" value="READY" class="btn btn-sm btn-success fw-semibold">
                                        🔔 Mark Ready
                                    </button>
                                <% } else if ("READY".equals(st)) { %>
                                    <button type="submit" name="newStatus" value="COMPLETED" class="btn btn-sm btn-dark">
                                        📦 Complete &amp; Handover
                                    </button>
                                <% } else if ("COMPLETED".equals(st)) { %>
                                    <span class="text-success small fw-bold">✓ Fulfilled</span>
                                <% } else if ("CANCELLED".equals(st)) { %>
                                    <span class="text-danger small fw-bold">✕ Cancelled</span>
                                <% } %>
                            </form>
                        </td>
                    </tr>
                <%  }
                   } else { %>
                    <tr>
                        <td colspan="7" class="text-center py-4 text-muted">
                            No orders found matching filter: <strong><%= currentFilter %></strong>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
