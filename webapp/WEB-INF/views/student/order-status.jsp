<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.canteenx.model.Order"%>
<%@ page import="com.canteenx.model.OrderItem"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    Order order = (Order) request.getAttribute("order");
    String ctx = request.getContextPath();
    String placedParam = request.getParameter("placed");

    String status = (order != null) ? order.getOrderStatus() : "PLACED";
    
    // Status progression logic
    int statusStep = 1;
    if ("ACCEPTED".equals(status)) statusStep = 2;
    else if ("PREPARING".equals(status)) statusStep = 3;
    else if ("READY".equals(status)) statusStep = 4;
    else if ("COMPLETED".equals(status)) statusStep = 5;
    else if ("CANCELLED".equals(status)) statusStep = -1;
%>

<div class="row justify-content-center my-4" id="order-status-tracker" 
     data-order-id="<%= (order != null) ? order.getOrderId() : 0 %>" 
     data-context="<%= ctx %>">
    
    <div class="col-lg-8">
        <% if ("true".equalsIgnoreCase(placedParam)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                🎉 <strong>Order Placed Successfully!</strong> Your order has been submitted to the canteen staff.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        <% } %>

        <div class="card shadow-sm border-0 rounded-4 overflow-hidden mb-4">
            <div class="card-header bg-dark text-white p-4">
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                    <div>
                        <h4 class="mb-0 fw-bold">Order #<%= (order != null) ? order.getOrderId() : "" %></h4>
                        <small class="text-white-50">Placed at <%= (order != null && order.getCreatedAt() != null) ? order.getCreatedAt().toString() : "" %></small>
                    </div>
                    <div>
                        <span id="order-status-badge" class="badge badge-status-<%= status %> fs-6 px-3 py-2">
                            <%= status %>
                        </span>
                    </div>
                </div>
            </div>

            <div class="card-body p-4 p-md-5">
                <!-- Visual Stepper -->
                <% if (statusStep > 0) { %>
                    <div class="order-stepper">
                        <div class="step-item <%= statusStep >= 1 ? (statusStep > 1 ? "completed" : "active") : "" %>">
                            <div class="step-icon">1</div>
                            <div class="step-label">Placed</div>
                        </div>
                        <div class="step-item <%= statusStep >= 2 ? (statusStep > 2 ? "completed" : "active") : "" %>">
                            <div class="step-icon">2</div>
                            <div class="step-label">Accepted</div>
                        </div>
                        <div class="step-item <%= statusStep >= 3 ? (statusStep > 3 ? "completed" : "active") : "" %>">
                            <div class="step-icon">3</div>
                            <div class="step-label">Preparing</div>
                        </div>
                        <div class="step-item <%= statusStep >= 4 ? (statusStep > 4 ? "completed" : "active") : "" %>">
                            <div class="step-icon">4</div>
                            <div class="step-label">Ready</div>
                        </div>
                        <div class="step-item <%= statusStep >= 5 ? "completed" : "" %>">
                            <div class="step-icon">5</div>
                            <div class="step-label">Completed</div>
                        </div>
                    </div>
                <% } else { %>
                    <div class="alert alert-danger text-center my-4">
                        <h5>⚠️ This order has been CANCELLED by the canteen.</h5>
                    </div>
                <% } %>

                <!-- Ready Notification Banner -->
                <% if ("READY".equals(status)) { %>
                    <div class="alert alert-success text-center py-3">
                        <h5 class="fw-bold mb-1">🔔 Your food is READY for collection!</h5>
                        <p class="mb-0 small">Please proceed to Counter 1 with your Order ID #<%= order.getOrderId() %>.</p>
                    </div>
                <% } else if ("PREPARING".equals(status)) { %>
                    <div class="alert alert-warning text-center py-2 small">
                        👨‍🍳 The kitchen team is currently preparing your order fresh.
                    </div>
                <% } %>

                <!-- Items Breakdown Table -->
                <h5 class="fw-bold mt-4 mb-3">Order Receipt</h5>
                <div class="table-responsive">
                    <table class="table table-bordered mb-3">
                        <thead class="table-light">
                            <tr>
                                <th>Item</th>
                                <th class="text-center">Quantity</th>
                                <th class="text-end">Unit Price</th>
                                <th class="text-end">Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (order != null && order.getItems() != null) {
                                for (OrderItem item : order.getItems()) { %>
                                    <tr>
                                        <td><%= item.getItemName() %></td>
                                        <td class="text-center"><%= item.getQuantity() %></td>
                                        <td class="text-end">₹<%= String.format("%.2f", item.getUnitPrice()) %></td>
                                        <td class="text-end">₹<%= String.format("%.2f", item.getSubtotal()) %></td>
                                    </tr>
                            <%  }
                               } %>
                        </tbody>
                        <tfoot>
                            <tr>
                                <th colspan="3" class="text-end">Grand Total:</th>
                                <th class="text-end text-success">
                                    ₹<%= (order != null && order.getTotalAmount() != null) ? String.format("%.2f", order.getTotalAmount()) : "0.00" %>
                                </th>
                            </tr>
                        </tfoot>
                    </table>
                </div>

                <div class="row text-muted small mt-3">
                    <div class="col-sm-6">
                        <strong>Payment Method:</strong> <%= (order != null) ? order.getPaymentMethod() : "" %>
                    </div>
                    <div class="col-sm-6 text-sm-end">
                        <strong>Payment Status:</strong> <%= (order != null) ? order.getPaymentStatus() : "" %>
                    </div>
                    <% if (order != null && order.getSpecialInstructions() != null && !order.getSpecialInstructions().isEmpty()) { %>
                        <div class="col-12 mt-2">
                            <strong>Special Instructions:</strong> <em>"<%= order.getSpecialInstructions() %>"</em>
                        </div>
                    <% } %>
                </div>

                <div class="d-flex justify-content-between align-items-center mt-4 pt-3 border-top">
                    <a href="<%= response.encodeURL(ctx + "/order-history") %>" class="btn btn-outline-secondary btn-sm">
                        &larr; View All My Orders
                    </a>
                    <a href="<%= response.encodeURL(ctx + "/order-status?orderId=" + ((order != null) ? order.getOrderId() : 0)) %>" class="btn btn-sm btn-outline-primary">
                        🔄 Refresh Status
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
