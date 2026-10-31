<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // MVC Dispatch: Forward to OrderHistoryServlet controller
    request.getRequestDispatcher("/order-history").forward(request, response);
%>
