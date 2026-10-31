<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // MVC Dispatch: Forward to AdminDashboardServlet controller
    request.getRequestDispatcher("/admin/dashboard").forward(request, response);
%>
