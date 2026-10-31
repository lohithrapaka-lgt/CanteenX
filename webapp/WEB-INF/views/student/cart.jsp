<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.canteenx.model.Cart"%>
<%@ page import="com.canteenx.model.CartItem"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    Cart cart = (Cart) request.getAttribute("cart");
    String ctx = request.getContextPath();
    String error = request.getParameter("error");
%>

<div class="row my-4">
    <div class="col-12">
        <h2 class="fw-bold mb-3">Your Canteen Cart 🛒</h2>
        
        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-warning py-2 small" role="alert">
                ⚠️ <%= error %>
            </div>
        <% } %>
    </div>

    <% if (cart == null || cart.isEmpty()) { %>
        <div class="col-12 text-center py-5">
            <div class="display-4 text-muted mb-3">🛒</div>
            <h4>Your cart is empty!</h4>
            <p class="text-muted">Looks like you haven't added anything to your cart yet.</p>
            <a href="<%= response.encodeURL(ctx + "/menu") %>" class="btn btn-warning mt-2 px-4 fw-bold">
                Explore Menu
            </a>
        </div>
    <% } else { %>
        <!-- Cart Items List -->
        <div class="col-lg-8">
            <div class="card shadow-sm border-0 rounded-3 mb-4">
                <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th scope="col" style="width: 45%;">Item</th>
                                <th scope="col" class="text-center" style="width: 20%;">Quantity</th>
                                <th scope="col" class="text-end" style="width: 20%;">Price</th>
                                <th scope="col" class="text-center" style="width: 15%;">Remove</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (CartItem ci : cart.getItems()) { %>
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center gap-3">
                                            <img src="<%= ctx %>/<%= ci.getMenuItem().getImageUrl() %>" 
                                                 alt="<%= ci.getMenuItem().getItemName() %>" 
                                                 class="rounded" style="width: 50px; height: 50px; object-fit: cover;"
                                                 onerror="this.onerror=null; this.src='<%= ctx %>/assets/images/food-placeholder.jpg';">
                                            <div>
                                                <h6 class="mb-0 fw-bold"><%= ci.getMenuItem().getItemName() %></h6>
                                                <small class="text-muted">₹<%= String.format("%.2f", ci.getMenuItem().getPrice()) %> each</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="text-center">
                                        <!-- WT Concept: Hidden Form Fields for Quantity Updates -->
                                        <form action="<%= response.encodeURL(ctx + "/cart") %>" method="POST" class="d-inline-flex align-items-center gap-1">
                                            <input type="hidden" name="action" value="UPDATE">
                                            <input type="hidden" name="itemId" value="<%= ci.getMenuItem().getItemId() %>">

                                            <button type="submit" name="quantity" value="<%= ci.getQuantity() - 1 %>" 
                                                    class="btn btn-sm btn-outline-secondary px-2 py-0" 
                                                    <%= ci.getQuantity() <= 1 ? "disabled" : "" %>>-</button>
                                            <span class="fw-bold px-2"><%= ci.getQuantity() %></span>
                                            <button type="submit" name="quantity" value="<%= ci.getQuantity() + 1 %>" 
                                                    class="btn btn-sm btn-outline-secondary px-2 py-0">+</button>
                                        </form>
                                    </td>
                                    <td class="text-end fw-bold">
                                        ₹<%= String.format("%.2f", ci.getSubtotal()) %>
                                    </td>
                                    <td class="text-center">
                                        <!-- WT Concept: Hidden Form Fields for Removal -->
                                        <form action="<%= response.encodeURL(ctx + "/cart") %>" method="POST" class="d-inline">
                                            <input type="hidden" name="action" value="REMOVE">
                                            <input type="hidden" name="itemId" value="<%= ci.getMenuItem().getItemId() %>">
                                            <button type="submit" class="btn btn-sm btn-outline-danger border-0">
                                                ✕
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <div class="card-footer bg-white d-flex justify-content-between align-items-center py-3">
                    <a href="<%= response.encodeURL(ctx + "/menu") %>" class="btn btn-sm btn-outline-dark">
                        &larr; Add More Items
                    </a>
                    <form action="<%= response.encodeURL(ctx + "/cart") %>" method="POST" class="d-inline">
                        <input type="hidden" name="action" value="CLEAR">
                        <button type="submit" class="btn btn-sm btn-link text-danger text-decoration-none">
                            Clear Entire Cart
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Order Summary & Checkout Card -->
        <div class="col-lg-4">
            <div class="card shadow-sm border-0 rounded-3">
                <div class="card-body p-4">
                    <h5 class="fw-bold mb-3">Order Summary</h5>
                    
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Total Items</span>
                        <span class="fw-semibold"><%= cart.getTotalItemCount() %></span>
                    </div>

                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Subtotal</span>
                        <span class="fw-semibold">₹<%= String.format("%.2f", cart.getTotalAmount()) %></span>
                    </div>

                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Campus Handling Charge</span>
                        <span class="text-success fw-semibold">FREE</span>
                    </div>

                    <hr>

                    <div class="d-flex justify-content-between mb-4">
                        <span class="h5 fw-bold mb-0">Total Payable</span>
                        <span class="h5 fw-bold text-success mb-0">₹<%= String.format("%.2f", cart.getTotalAmount()) %></span>
                    </div>

                    <a href="<%= response.encodeURL(ctx + "/checkout") %>" class="btn btn-warning fw-bold w-100 py-2">
                        Proceed to Checkout &rarr;
                    </a>
                </div>
            </div>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
