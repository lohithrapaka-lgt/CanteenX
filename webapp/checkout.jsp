<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // MVC Dispatch: Forward to CheckoutServlet controller
    request.getRequestDispatcher("/checkout").forward(request, response);
%>
