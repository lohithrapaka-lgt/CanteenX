<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    String error = (String) request.getAttribute("errorMessage");
    if (error == null) error = request.getParameter("error");
    String success = request.getParameter("success");
    String rememberedEmail = (String) request.getAttribute("rememberedEmail");
    if (rememberedEmail == null) rememberedEmail = "";
%>

<div class="row justify-content-center my-4">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm border-0 rounded-4">
            <div class="card-body p-4 p-md-5">
                <div class="text-center mb-4">
                    <h3 class="fw-bold">Sign In to CanteenX</h3>
                    <p class="text-muted small">Access student ordering or canteen administration</p>
                </div>

                <% if (error != null && !error.isEmpty()) { %>
                    <div class="alert alert-danger py-2 small" role="alert">
                        ⚠️ <%= error %>
                    </div>
                <% } %>

                <% if (success != null && !success.isEmpty()) { %>
                    <div class="alert alert-success py-2 small" role="alert">
                        ✓ <%= success %>
                    </div>
                <% } %>

                <form action="<%= response.encodeURL(request.getContextPath() + "/login") %>" method="POST">
                    <div class="mb-3">
                        <label for="email" class="form-label fw-semibold">Email Address</label>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="<%= !rememberedEmail.isEmpty() ? rememberedEmail : (request.getAttribute("email") != null ? request.getAttribute("email") : "") %>" 
                               placeholder="e.g. student@college.edu" required autofocus>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label fw-semibold">Password</label>
                        <input type="password" class="form-control" id="password" name="password" 
                               placeholder="Enter your password" required>
                    </div>

                    <div class="mb-3 form-check">
                        <!-- Demonstrates Cookies concept: Remember Me flag -->
                        <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe" 
                               <%= !rememberedEmail.isEmpty() ? "checked" : "" %>>
                        <label class="form-check-label small text-muted" for="rememberMe">Remember my email (Cookies)</label>
                    </div>

                    <div class="d-grid mb-3">
                        <button type="submit" class="btn btn-warning fw-bold py-2">Sign In</button>
                    </div>

                    <div class="text-center small text-muted">
                        Don't have an account? 
                        <a href="<%= response.encodeURL(request.getContextPath() + "/register") %>" class="text-decoration-none fw-semibold">Register here</a>
                    </div>
                </form>

                <!-- Academic Viva Demonstration Helper -->
                <div class="mt-4 pt-3 border-top">
                    <div class="badge bg-secondary mb-2">Demo Credentials for Evaluation</div>
                    <div class="small bg-light p-2 rounded border">
                        <div><strong>Admin:</strong> admin@canteenx.com / <code>admin123</code></div>
                        <div><strong>Student:</strong> student@college.edu / <code>student123</code></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
