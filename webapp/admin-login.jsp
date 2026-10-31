<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    String error = (String) request.getAttribute("errorMessage");
    if (error == null) error = request.getParameter("error");
    String success = request.getParameter("success");
    String rememberedAdminEmail = "";
    jakarta.servlet.http.Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (jakarta.servlet.http.Cookie c : cookies) {
            if ("canteenx_admin_email".equals(c.getName())) {
                rememberedAdminEmail = c.getValue();
                break;
            }
        }
    }
%>

<div class="row justify-content-center my-4">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm border-0 rounded-4">
            <div class="card-body p-4 p-md-5">
                <div class="text-center mb-4">
                    <div class="display-6 mb-2">ðŸ›¡ï¸</div>
                    <h3 class="fw-bold">Admin Portal Login</h3>
                    <p class="text-muted small">Canteen staff &amp; management dashboard access</p>
                </div>

                <% if (error != null && !error.isEmpty()) { %>
                    <div class="alert alert-danger py-2 small" role="alert">
                        âš ï¸ <%= error %>
                    </div>
                <% } %>

                <% if (success != null && !success.isEmpty()) { %>
                    <div class="alert alert-success py-2 small" role="alert">
                        âœ“ <%= success %>
                    </div>
                <% } %>

                <form action="<%= response.encodeURL(request.getContextPath() + "/admin/login") %>" method="POST">
                    <div class="mb-3">
                        <label for="adminEmail" class="form-label fw-semibold">Admin Email</label>
                        <input type="email" class="form-control" id="adminEmail" name="email" 
                               value="<%= !rememberedAdminEmail.isEmpty() ? rememberedAdminEmail : (request.getAttribute("email") != null ? request.getAttribute("email") : "admin@canteenx.com") %>" 
                               placeholder="admin@canteenx.com" required autofocus>
                    </div>

                    <div class="mb-3">
                        <label for="adminPassword" class="form-label fw-semibold">Admin Password</label>
                        <input type="password" class="form-control" id="adminPassword" name="password" 
                               placeholder="Enter admin password" required>
                    </div>

                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe" 
                               <%= !rememberedAdminEmail.isEmpty() ? "checked" : "" %>>
                        <label class="form-check-label small text-muted" for="rememberMe">Remember admin email</label>
                    </div>

                    <div class="d-grid mb-3">
                        <button type="submit" class="btn btn-dark fw-bold py-2">Sign In as Admin</button>
                    </div>

                    <div class="text-center small text-muted">
                        Are you a student? 
                        <a href="<%= response.encodeURL(request.getContextPath() + "/login") %>" class="text-decoration-none fw-semibold">Student Login</a>
                    </div>
                </form>

                <div class="mt-4 pt-3 border-top text-center small text-muted">
                    Default credentials: <code>admin@canteenx.com</code> / <code>admin123</code>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />

