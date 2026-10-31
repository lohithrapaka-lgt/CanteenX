<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // MVC Dispatch: Forward to MenuServlet controller for single item details
    request.getRequestDispatcher("/item-details").forward(request, response);
%>
