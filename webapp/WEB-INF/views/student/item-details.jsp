<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.canteenx.model.MenuItem"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    MenuItem item = (MenuItem) request.getAttribute("item");
    String ctx = request.getContextPath();
%>

<% if (item == null) { %>
    <div class="row justify-content-center my-5">
        <div class="col-md-6 text-center">
            <h4>Item not found</h4>
            <p class="text-muted">The requested food item does not exist or has been removed.</p>
            <a href="<%= response.encodeURL(ctx + "/menu") %>" class="btn btn-warning">Return to Menu</a>
        </div>
    </div>
<% } else { %>
    <div class="row justify-content-center my-4">
        <div class="col-lg-8">
            <nav aria-label="breadcrumb" class="mb-3">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="<%= response.encodeURL(ctx + "/menu") %>">Menu</a></li>
                    <li class="breadcrumb-item active" aria-current="page"><%= item.getItemName() %></li>
                </ol>
            </nav>

            <div class="card shadow-sm border-0 rounded-4 overflow-hidden">
                <div class="row g-0">
                    <div class="col-md-5">
                        <img src="<%= ctx %>/<%= item.getImageUrl() %>" 
                             class="img-fluid h-100 w-100 object-fit-cover" 
                             alt="<%= item.getItemName() %>"
                             onerror="this.onerror=null; this.src='<%= ctx %>/assets/images/food-placeholder.jpg';">
                    </div>
                    <div class="col-md-7">
                        <div class="card-body p-4 p-md-5 d-flex flex-column h-100">
                            <div class="mb-2">
                                <span class="badge bg-secondary"><%= item.getCategoryName() %></span>
                            </div>
                            <h2 class="card-title fw-bold"><%= item.getItemName() %></h2>
                            
                            <div class="my-3">
                                <span class="display-6 fw-bold text-success">₹<%= String.format("%.2f", item.getPrice()) %></span>
                            </div>

                            <p class="card-text text-muted my-3 flex-grow-1">
                                <%= item.getDescription() %>
                            </p>

                            <div class="pt-3 border-top mt-auto">
                                <% if (item.isAvailable()) { %>
                                    <form action="<%= response.encodeURL(ctx + "/cart") %>" method="POST" class="d-flex align-items-center gap-3">
                                        <!-- WT Syllabus: Hidden Form Fields -->
                                        <input type="hidden" name="action" value="ADD">
                                        <input type="hidden" name="itemId" value="<%= item.getItemId() %>">

                                        <div class="input-group" style="width: 140px;">
                                            <label class="input-group-text small" for="qtyInput">Qty</label>
                                            <input type="number" id="qtyInput" name="quantity" class="form-control text-center" value="1" min="1" max="10">
                                        </div>
                                        <button type="submit" class="btn btn-warning fw-bold px-4 flex-grow-1">
                                            Add to Cart 🛒
                                        </button>
                                    </form>
                                <% } else { %>
                                    <div class="alert alert-secondary mb-0">
                                        This item is currently out of stock. Please check back later.
                                    </div>
                                <% } %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
<% } %>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
