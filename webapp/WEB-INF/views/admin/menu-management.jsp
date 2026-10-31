<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.canteenx.model.MenuItem"%>
<%@ page import="com.canteenx.model.Category"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<MenuItem> menuItems = (List<MenuItem>) request.getAttribute("menuItems");
    MenuItem editItem = (MenuItem) request.getAttribute("editItem");
    String ctx = request.getContextPath();
    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>

<div class="row align-items-center mb-4">
    <div class="col-md-7">
        <h2 class="fw-bold mb-1">Menu Catalog Management 🍲</h2>
        <p class="text-muted mb-0">Add new items, update prices, modify descriptions, and toggle kitchen availability.</p>
    </div>
    <div class="col-md-5 text-md-end mt-3 mt-md-0">
        <button type="button" class="btn btn-warning fw-bold px-3" data-bs-toggle="modal" data-bs-target="#addItemModal">
            + Add New Food Item
        </button>
    </div>
</div>

<% if (success != null && !success.isEmpty()) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        ✓ <%= success %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<% } %>

<% if (error != null && !error.isEmpty()) { %>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ⚠️ <%= error %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<% } %>

<!-- Edit Item Form (Shown when editId is requested) -->
<% if (editItem != null) { %>
    <div class="card shadow-sm border-warning mb-4">
        <div class="card-header bg-warning bg-opacity-25 fw-bold">
            ✏️ Edit Menu Item: <%= editItem.getItemName() %>
        </div>
        <div class="card-body">
            <form action="<%= response.encodeURL(ctx + "/admin/menu") %>" method="POST">
                <!-- WT Concept: Hidden Form Fields -->
                <input type="hidden" name="action" value="UPDATE">
                <input type="hidden" name="itemId" value="<%= editItem.getItemId() %>">

                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Item Name</label>
                        <input type="text" name="itemName" class="form-control" value="<%= editItem.getItemName() %>" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Category</label>
                        <select name="categoryId" class="form-select" required>
                            <% if (categories != null) {
                                for (Category c : categories) { %>
                                    <option value="<%= c.getCategoryId() %>" <%= (c.getCategoryId() == editItem.getCategoryId()) ? "selected" : "" %>>
                                        <%= c.getCategoryName() %>
                                    </option>
                            <%   }
                               } %>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label fw-semibold">Price (₹)</label>
                        <input type="number" step="0.50" name="price" class="form-control" value="<%= editItem.getPrice() %>" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Image URL / Path</label>
                        <input type="text" name="imageUrl" class="form-control" value="<%= editItem.getImageUrl() %>">
                    </div>
                    <div class="col-md-10">
                        <label class="form-label fw-semibold">Description</label>
                        <input type="text" name="description" class="form-control" value="<%= editItem.getDescription() %>">
                    </div>
                    <div class="col-md-2 d-flex align-items-center mt-4">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" name="isAvailable" value="true" id="editAvailSwitch" <%= editItem.isAvailable() ? "checked" : "" %>>
                            <label class="form-check-label fw-semibold" for="editAvailSwitch">In Stock</label>
                        </div>
                    </div>
                    <div class="col-12 mt-3 d-flex gap-2">
                        <button type="submit" class="btn btn-success px-4">Save Changes</button>
                        <a href="<%= response.encodeURL(ctx + "/admin/menu") %>" class="btn btn-outline-secondary">Cancel</a>
                    </div>
                </div>
            </form>
        </div>
    </div>
<% } %>

<!-- Menu Items Table -->
<div class="card shadow-sm border-0 rounded-3">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
                <tr>
                    <th>Item</th>
                    <th>Category</th>
                    <th>Description</th>
                    <th class="text-end">Price</th>
                    <th class="text-center">Availability</th>
                    <th class="text-center" style="width: 180px;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <% if (menuItems != null && !menuItems.isEmpty()) {
                    for (MenuItem item : menuItems) { %>
                        <tr>
                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <img src="<%= ctx %>/<%= item.getImageUrl() %>" 
                                         alt="<%= item.getItemName() %>" 
                                         class="rounded" style="width: 40px; height: 40px; object-fit: cover;"
                                         onerror="this.onerror=null; this.src='<%= ctx %>/assets/images/food-placeholder.jpg';">
                                    <strong><%= item.getItemName() %></strong>
                                </div>
                            </td>
                            <td><span class="badge bg-light text-dark border"><%= item.getCategoryName() %></span></td>
                            <td class="small text-muted" style="max-width: 300px;"><%= item.getDescription() %></td>
                            <td class="text-end fw-bold">₹<%= String.format("%.2f", item.getPrice()) %></td>
                            <td class="text-center">
                                <!-- WT Concept: Hidden Form Fields for Availability Toggle -->
                                <form action="<%= response.encodeURL(ctx + "/admin/menu") %>" method="POST" class="d-inline">
                                    <input type="hidden" name="action" value="TOGGLE_AVAILABILITY">
                                    <input type="hidden" name="itemId" value="<%= item.getItemId() %>">
                                    <input type="hidden" name="currentStatus" value="<%= item.isAvailable() %>">

                                    <button type="submit" class="btn btn-sm <%= item.isAvailable() ? "btn-success" : "btn-outline-danger" %> py-0 px-2" style="font-size: 0.8rem;">
                                        <%= item.isAvailable() ? "✓ In Stock" : "✕ Out of Stock" %>
                                    </button>
                                </form>
                            </td>
                            <td class="text-center">
                                <div class="d-inline-flex gap-1">
                                    <a href="<%= response.encodeURL(ctx + "/admin/menu?editId=" + item.getItemId()) %>" class="btn btn-sm btn-outline-primary">
                                        Edit
                                    </a>
                                    <!-- Delete Item Form with Hidden Field -->
                                    <form action="<%= response.encodeURL(ctx + "/admin/menu") %>" method="POST" class="d-inline" onsubmit="return confirm('Are you sure you want to delete this menu item?');">
                                        <input type="hidden" name="action" value="DELETE">
                                        <input type="hidden" name="itemId" value="<%= item.getItemId() %>">
                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                            Delete
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                <%  }
                   } else { %>
                    <tr>
                        <td colspan="6" class="text-center py-4 text-muted">No food items found in the menu.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

<!-- Add Item Modal -->
<div class="modal fade" id="addItemModal" tabindex="-1" aria-labelledby="addItemModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="<%= response.encodeURL(ctx + "/admin/menu") %>" method="POST">
                <input type="hidden" name="action" value="ADD">

                <div class="modal-header bg-dark text-white">
                    <h5 class="modal-title" id="addItemModalLabel">Add New Food Item</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Item Name</label>
                            <input type="text" name="itemName" class="form-control" placeholder="e.g. Masala Dosa" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Category</label>
                            <select name="categoryId" class="form-select" required>
                                <% if (categories != null) {
                                    for (Category c : categories) { %>
                                        <option value="<%= c.getCategoryId() %>"><%= c.getCategoryName() %></option>
                                <%   }
                                   } %>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Price (₹)</label>
                            <input type="number" step="0.50" name="price" class="form-control" placeholder="e.g. 45.00" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Image URL / Path</label>
                            <input type="text" name="imageUrl" class="form-control" placeholder="assets/images/food-placeholder.jpg">
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-semibold">Description</label>
                            <textarea name="description" class="form-control" rows="2" placeholder="Briefly describe ingredients or serving style..."></textarea>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-warning fw-bold px-4">Add Item</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
