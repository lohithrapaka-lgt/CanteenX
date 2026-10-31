<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    String error = (String) request.getAttribute("errorMessage");
    String fullName = (String) request.getAttribute("fullName");
    String email = (String) request.getAttribute("email");
    String phone = (String) request.getAttribute("phone");
%>

<div class="row justify-content-center my-4">
    <div class="col-md-7 col-lg-6">
        <div class="card shadow-sm border-0 rounded-4">
            <div class="card-body p-4 p-md-5">
                <div class="text-center mb-4">
                    <h3 class="fw-bold">Student Registration</h3>
                    <p class="text-muted small">Create an account to order from the campus canteen</p>
                </div>

                <% if (error != null && !error.isEmpty()) { %>
                    <div class="alert alert-danger py-2 small" role="alert">
                        ⚠️ <%= error %>
                    </div>
                <% } %>

                <!-- WT Syllabus: Form Validation & Regular Expressions -->
                <form id="registrationForm" action="<%= response.encodeURL(request.getContextPath() + "/register") %>" method="POST" novalidate>
                    <div class="mb-3">
                        <label for="fullName" class="form-label fw-semibold">Full Name</label>
                        <input type="text" class="form-control" id="fullName" name="fullName" 
                               value="<%= fullName != null ? fullName : "" %>"
                               placeholder="e.g. Rahul Sharma" required>
                        <div class="invalid-feedback">Letters and spaces only (min 2 characters).</div>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label fw-semibold">College Email</label>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="<%= email != null ? email : "" %>"
                               placeholder="e.g. rahul@college.edu" required>
                        <div class="invalid-feedback">Please enter a valid email format.</div>
                    </div>

                    <div class="mb-3">
                        <label for="phone" class="form-label fw-semibold">Mobile Phone (10 digits)</label>
                        <input type="tel" class="form-control" id="phone" name="phone" 
                               value="<%= phone != null ? phone : "" %>"
                               placeholder="e.g. 9876543210" required>
                        <div class="invalid-feedback">10-digit mobile number starting with 6-9.</div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label fw-semibold">Password</label>
                            <input type="password" class="form-control" id="password" name="password" 
                                   placeholder="Minimum 6 characters" required>
                            <div class="invalid-feedback">Password must be at least 6 characters.</div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="confirmPassword" class="form-label fw-semibold">Confirm Password</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                                   placeholder="Re-type password" required>
                            <div class="invalid-feedback">Passwords must match.</div>
                        </div>
                    </div>

                    <div class="d-grid mb-3">
                        <button type="submit" class="btn btn-warning fw-bold py-2">Create Account</button>
                    </div>

                    <div class="text-center small text-muted">
                        Already have an account? 
                        <a href="<%= response.encodeURL(request.getContextPath() + "/login") %>" class="text-decoration-none fw-semibold">Sign in here</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
