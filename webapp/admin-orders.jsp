<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // MVC Dispatch: Forward to ManageOrdersServlet / AdminOrderServlet controller
    request.getRequestDispatcher("/admin/orders").forward(request, response);
%>
