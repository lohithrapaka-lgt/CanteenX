<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="row justify-content-center">
    <div class="col-md-8 text-center my-5">
        <div class="card shadow-sm border-danger">
            <div class="card-body p-5">
                <h1 class="display-3 text-danger fw-bold">Oops!</h1>
                <h4 class="card-title text-muted mt-2">An error occurred while processing your request.</h4>
                
                <div class="alert alert-light border mt-4 text-start small">
                    <p class="mb-1"><strong>Status Code:</strong> <%= pageContext.getErrorData() != null ? pageContext.getErrorData().getStatusCode() : "500" %></p>
                    <p class="mb-1"><strong>Requested URI:</strong> <%= pageContext.getErrorData() != null ? pageContext.getErrorData().getRequestURI() : request.getRequestURI() %></p>
                    <% if (exception != null) { %>
                        <p class="mb-0 text-danger"><strong>Exception Message:</strong> <%= exception.getMessage() %></p>
                    <% } %>
                </div>

                <div class="mt-4">
                    <a href="<%= response.encodeURL(request.getContextPath() + "/menu") %>" class="btn btn-warning px-4">Return to Menu</a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
