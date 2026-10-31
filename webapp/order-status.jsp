<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // MVC Dispatch: Forward to OrderStatusServlet controller
    request.getRequestDispatcher("/order-status").forward(request, response);
%>
