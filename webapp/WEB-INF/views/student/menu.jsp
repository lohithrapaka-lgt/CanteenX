<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.canteenx.model.MenuItem"%>
<%@ page import="com.canteenx.model.Category"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<MenuItem> menuItems = (List<MenuItem>) request.getAttribute("menuItems");
    Integer selectedCategory = (Integer) request.getAttribute("selectedCategory");
    String searchQuery = (String) request.getAttribute("searchQuery");
    String ctx = request.getContextPath();
%>

<!-- Search and Filter Header -->
<div class="row align-items-center mb-4 g-3">
    <div class="col-md-7">
        <h2 class="fw-bold mb-1">Today's Campus Menu 🍴</h2>
        <p class="text-muted mb-0">Browse fresh meals prepared daily by the campus culinary team.</p>
    </div>
    <div class="col-md-5">
        <form action="<%= response.encodeURL(ctx + "/menu") %>" method="GET" class="d-flex gap-2">
            <% if (selectedCategory != null && selectedCategory > 0) { %>
                <input type="hidden" name="category" value="<%= selectedCategory %>">
            <% } %>
            <input type="search" name="q" class="form-control" placeholder="Search food items..." 
                   value="<%= searchQuery != null ? searchQuery : "" %>">
            <button type="submit" class="btn btn-warning px-3">Search</button>
            <% if ((searchQuery != null && !searchQuery.isEmpty()) || selectedCategory != null) { %>
                <a href="<%= response.encodeURL(ctx + "/menu") %>" class="btn btn-outline-secondary">Reset</a>
            <% } %>
        </form>
    </div>
</div>

<!-- Category Filters Bar -->
<div class="mb-4">
    <div class="d-flex flex-wrap gap-2">
        <a href="<%= response.encodeURL(ctx + "/menu" + (searchQuery != null && !searchQuery.isEmpty() ? "?q=" + searchQuery : "")) %>" 
           class="btn btn-sm <%= (selectedCategory == null) ? "btn-dark" : "btn-outline-dark" %> rounded-pill px-3">
            All Categories
        </a>
        <% if (categories != null) {
            for (Category cat : categories) {
                boolean isActive = (selectedCategory != null && selectedCategory == cat.getCategoryId());
                String catUrl = ctx + "/menu?category=" + cat.getCategoryId() + 
                                (searchQuery != null && !searchQuery.isEmpty() ? "&q=" + searchQuery : "");
        %>
            <a href="<%= response.encodeURL(catUrl) %>" 
               class="btn btn-sm <%= isActive ? "btn-dark" : "btn-outline-dark" %> rounded-pill px-3">
                <%= cat.getCategoryName() %>
            </a>
        <%   }
           } %>
    </div>
</div>

<!-- Menu Grid -->
<div class="row g-4">
    <% if (menuItems != null && !menuItems.isEmpty()) {
        for (MenuItem item : menuItems) {
    %>
        <div class="col-12 col-sm-6 col-lg-4 col-xl-3">
            <div class="card food-card">
                <div class="position-relative">
                    <img src="<%= ctx %>/<%= item.getImageUrl() %>" class="card-img-top food-card-img" alt="<%= item.getItemName() %>"
                         onerror="this.onerror=null; this.src='<%= ctx %>/assets/images/food-placeholder.jpg';">
                    <span class="badge bg-dark bg-opacity-75 position-absolute top-0 end-0 m-2">
                        <%= item.getCategoryName() %>
                    </span>
                </div>
                <div class="card-body">
                    <h5 class="card-title fw-bold mb-1">
                        <a href="<%= response.encodeURL(ctx + "/item-details?id=" + item.getItemId()) %>" class="text-dark text-decoration-none">
                            <%= item.getItemName() %>
                        </a>
                    </h5>
                    <p class="card-text text-muted small flex-grow-1" style="min-height: 40px;">
                        <%= item.getDescription() %>
                    </p>
                    
                    <div class="d-flex align-items-center justify-content-between pt-2 border-top mt-auto">
                        <span class="price-tag">₹<%= String.format("%.2f", item.getPrice()) %></span>
                        <% if (item.isAvailable()) { %>
                            <!-- jQuery AJAX Add to Cart button -->
                            <button type="button" 
                                    class="btn btn-sm btn-primary btn-add-cart-ajax px-3"
                                    data-item-id="<%= item.getItemId() %>"
                                    data-context="<%= ctx %>">
                                Add to Cart
                            </button>
                        <% } else { %>
                            <span class="badge bg-secondary">Sold Out</span>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    <%  }
       } else { %>
        <div class="col-12 text-center py-5">
            <div class="display-6 text-muted mb-3">🔍</div>
            <h4>No food items matched your criteria.</h4>
            <p class="text-muted">Try clearing your filters or search keywords.</p>
            <a href="<%= response.encodeURL(ctx + "/menu") %>" class="btn btn-warning">View All Items</a>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
