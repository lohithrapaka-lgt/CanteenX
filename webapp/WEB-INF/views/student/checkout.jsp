<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.canteenx.model.Cart"%>
<%@ page import="com.canteenx.model.CartItem"%>
<%@ page import="com.canteenx.model.User"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    Cart cart = (Cart) request.getAttribute("cart");
    User currentUser = (User) session.getAttribute("currentUser");
    String ctx = request.getContextPath();
    String error = (String) request.getAttribute("errorMessage");
%>

<div class="row justify-content-center my-4">
    <div class="col-lg-9">
        <h2 class="fw-bold mb-3">Order Checkout 💳</h2>

        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-danger py-2 small" role="alert">
                ⚠️ <%= error %>
            </div>
        <% } %>

        <form action="<%= response.encodeURL(ctx + "/checkout") %>" method="POST">
            <div class="row g-4">
                <div class="col-md-7">
                    <!-- Delivery/Pickup Details -->
                    <div class="card shadow-sm border-0 rounded-3 mb-4">
                        <div class="card-body p-4">
                            <h5 class="fw-bold mb-3">Student Details</h5>
                            <div class="mb-2">
                                <strong>Student Name:</strong> <%= currentUser != null ? currentUser.getFullName() : "" %>
                            </div>
                            <div class="mb-2">
                                <strong>Email:</strong> <%= currentUser != null ? currentUser.getEmail() : "" %>
                            </div>
                            <div class="mb-3">
                                <strong>Phone:</strong> <%= currentUser != null ? currentUser.getPhone() : "" %>
                            </div>

                            <hr>

                            <h5 class="fw-bold mb-3">Payment Method</h5>
                            <div class="form-check mb-2">
                                <input class="form-check-input" type="radio" name="paymentMethod" id="cashOption" value="CASH_AT_COUNTER" checked>
                                <label class="form-check-label" for="cashOption">
                                    💵 Cash at Canteen Counter
                                </label>
                            </div>
                            <div class="form-check mb-3">
                                <input class="form-check-input" type="radio" name="paymentMethod" id="walletOption" value="CAMPUS_WALLET">
                                <label class="form-check-label" for="walletOption">
                                    💳 Student Campus Card / UPI
                                </label>
                            </div>

                            <div class="mb-3">
                                <label for="specialInstructions" class="form-label fw-semibold">Special Instructions (Optional)</label>
                                <textarea class="form-control" id="specialInstructions" name="specialInstructions" rows="2" 
                                          placeholder="e.g. Less spicy, extra sauce, allergic to nuts..."></textarea>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-md-5">
                    <!-- Order Items Breakdown -->
                    <div class="card shadow-sm border-0 rounded-3">
                        <div class="card-body p-4">
                            <h5 class="fw-bold mb-3">Order Summary</h5>
                            
                            <ul class="list-group list-group-flush mb-3">
                                <% if (cart != null) {
                                    for (CartItem ci : cart.getItems()) { %>
                                        <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                            <div>
                                                <h6 class="my-0"><%= ci.getMenuItem().getItemName() %></h6>
                                                <small class="text-muted">Qty: <%= ci.getQuantity() %></small>
                                            </div>
                                            <span class="text-muted">₹<%= String.format("%.2f", ci.getSubtotal()) %></span>
                                        </li>
                                    <% }
                                } %>
                            </ul>

                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-muted">Subtotal</span>
                                <strong>₹<%= cart != null ? String.format("%.2f", cart.getTotalAmount()) : "0.00" %></strong>
                            </div>

                            <hr>

                            <div class="d-flex justify-content-between mb-4">
                                <span class="h5 fw-bold mb-0">Total Due</span>
                                <span class="h5 fw-bold text-success mb-0">₹<%= cart != null ? String.format("%.2f", cart.getTotalAmount()) : "0.00" %></span>
                            </div>

                            <!-- Triggers JDBC Transaction in OrderDAO -->
                            <button type="submit" class="btn btn-warning fw-bold w-100 py-2">
                                Confirm &amp; Place Order 🚀
                            </button>

                            <div class="text-center mt-3">
                                <a href="<%= response.encodeURL(ctx + "/cart") %>" class="text-muted small text-decoration-none">
                                    &larr; Return to Cart
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
