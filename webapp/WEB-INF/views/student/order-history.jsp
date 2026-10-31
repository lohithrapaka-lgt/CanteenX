<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.canteenx.model.Order"%>
<%@ page import="com.canteenx.model.OrderItem"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    String ctx = request.getContextPath();
%>

<div class="row my-4">
    <div class="col-12">
        <h2 class="fw-bold mb-1">My Orders 📜</h2>
        <p class="text-muted">Track active orders or view receipts of past transactions.</p>
    </div>

    <div class="col-12 mt-3">
        <% if (orders == null || orders.isEmpty()) { %>
            <div class="text-center py-5 bg-white rounded-3 shadow-sm">
                <div class="display-6 text-muted mb-3">📦</div>
                <h5>No orders placed yet!</h5>
                <p class="text-muted">Once you order food, it will appear right here with real-time tracking.</p>
                <a href="<%= response.encodeURL(ctx + "/menu") %>" class="btn btn-warning fw-bold px-4">Order Now</a>
            </div>
        <% } else { %>
            <div class="card shadow-sm border-0 rounded-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Order ID</th>
                                <th>Date &amp; Time</th>
                                <th>Items</th>
                                <th>Total</th>
                                <th>Payment</th>
                                <th>Status</th>
                                <th class="text-center">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Order o : orders) { %>
                                <tr>
                                    <td><strong class="text-primary">#<%= o.getOrderId() %></strong></td>
                                    <td class="small text-muted"><%= o.getCreatedAt() %></td>
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
                                        <span class="badge bg-light text-dark border">
                                            <%= o.getPaymentMethod().replace("_", " ") %>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge badge-status-<%= o.getOrderStatus() %>">
                                            <%= o.getOrderStatus() %>
                                        </span>
                                    </td>
                                    <td class="text-center">
                                        <a href="<%= response.encodeURL(ctx + "/order-status?orderId=" + o.getOrderId()) %>" class="btn btn-sm btn-outline-primary">
                                            Track
                                        </a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        <% } %>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
