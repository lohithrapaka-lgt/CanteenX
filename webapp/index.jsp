<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="hero-section text-center shadow-sm">
    <div class="container py-4">
        <h1 class="display-4 fw-bold">Skip the Canteen Queue! 🚀</h1>
        <p class="lead mb-4">Order delicious breakfast, lunch combos, evening snacks &amp; beverages directly from your phone or laptop.</p>
        <div class="d-flex justify-content-center gap-3">
            <a href="<%= response.encodeURL(request.getContextPath() + "/menu") %>" class="btn btn-light btn-lg px-4 fw-bold text-dark shadow-sm">
                🍽️ Browse Menu
            </a>
            <% if (session.getAttribute("currentUser") == null) { %>
                <a href="<%= response.encodeURL(request.getContextPath() + "/login") %>" class="btn btn-outline-light btn-lg px-4">
                    Student Login
                </a>
            <% } %>
        </div>
    </div>
</div>

<div class="row g-4 my-3">
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm text-center p-3">
            <div class="card-body">
                <div class="display-6 text-warning mb-2">⚡</div>
                <h5 class="card-title fw-bold">Instant Ordering</h5>
                <p class="card-text text-muted">Browse items by category, customize your cart, and place orders in seconds.</p>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm text-center p-3">
            <div class="card-body">
                <div class="display-6 text-warning mb-2">🔔</div>
                <h5 class="card-title fw-bold">Live Status Tracking</h5>
                <p class="card-text text-muted">Follow your order lifecycle in real-time from Preparation to Ready for Pickup.</p>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm text-center p-3">
            <div class="card-body">
                <div class="display-6 text-warning mb-2">📜</div>
                <h5 class="card-title fw-bold">Standard WT Architecture</h5>
                <p class="card-text text-muted">Built with pure Servlets, JSP, JDBC Transactions, XML DTD/XSD, and MVC pattern.</p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
