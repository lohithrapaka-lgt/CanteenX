<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // MVC Dispatch: Forward to ManageMenuServlet / AdminFoodServlet controller
    request.getRequestDispatcher("/admin/menu").forward(request, response);
%>
