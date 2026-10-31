package com.canteenx.controller.admin;

import com.canteenx.dao.CategoryDAO;
import com.canteenx.dao.MenuItemDAO;
import com.canteenx.model.Category;
import com.canteenx.model.MenuItem;
import com.canteenx.util.XmlParserUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controller demonstrating Web Technologies (WT) Syllabus XML Concepts:
 * - XML Schema (XSD) validation
 * - DTD validation
 * - DOM (Document Object Model) parsing
 * - SAX (Simple API for XML) parsing
 * - XML Menu Import with database synchronization
 * - Dynamic XML document generation & export
 */
@WebServlet(name = "MenuXmlServlet", urlPatterns = {"/admin/xml-tools", "/admin/xml-export"})
public class MenuXmlServlet extends HttpServlet {

    private MenuItemDAO menuItemDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        this.menuItemDAO = new MenuItemDAO();
        this.categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        // 1. Dynamic XML Export Feature
        if ("/admin/xml-export".equals(servletPath)) {
            exportMenuAsXml(request, response);
            return;
        }

        // 2. Render XML Tools dashboard
        request.getRequestDispatcher("/WEB-INF/views/admin/xml-tools.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String operation = request.getParameter("operation");
        ServletContext context = getServletContext();

        String xmlPath = context.getRealPath("/WEB-INF/xml/sample-menu.xml");
        String xsdPath = context.getRealPath("/WEB-INF/xml/menu.xsd");

        File xmlFile = new File(xmlPath);
        File xsdFile = new File(xsdPath);

        if ("VALIDATE_XSD".equalsIgnoreCase(operation)) {
            XmlParserUtil.ValidationResult result = XmlParserUtil.validateAgainstXsd(xmlFile, xsdFile);
            request.setAttribute("resultType", result.isValid() ? "SUCCESS" : "ERROR");
            request.setAttribute("resultMessage", result.getMessage());

        } else if ("VALIDATE_DTD".equalsIgnoreCase(operation)) {
            XmlParserUtil.ValidationResult result = XmlParserUtil.validateAgainstDtd(xmlFile);
            request.setAttribute("resultType", result.isValid() ? "SUCCESS" : "ERROR");
            request.setAttribute("resultMessage", result.getMessage());

        } else if ("PARSE_DOM".equalsIgnoreCase(operation)) {
            try (FileInputStream fis = new FileInputStream(xmlFile)) {
                List<MenuItem> parsedItems = XmlParserUtil.parseMenuItemsUsingDOM(fis);
                request.setAttribute("resultType", "SUCCESS");
                request.setAttribute("resultMessage", "DOM Parser successfully parsed " + parsedItems.size() + 
                    " food items from XML tree.");
                request.setAttribute("parsedItems", parsedItems);
            } catch (Exception e) {
                request.setAttribute("resultType", "ERROR");
                request.setAttribute("resultMessage", "DOM Parsing failed: " + e.getMessage());
            }

        } else if ("PARSE_SAX".equalsIgnoreCase(operation)) {
            try (FileInputStream fis = new FileInputStream(xmlFile)) {
                int totalElements = XmlParserUtil.countElementsUsingSAX(fis);
                request.setAttribute("resultType", "SUCCESS");
                request.setAttribute("resultMessage", "SAX Parser processed document stream and counted " + 
                    totalElements + " total XML elements.");
            } catch (Exception e) {
                request.setAttribute("resultType", "ERROR");
                request.setAttribute("resultMessage", "SAX Parsing failed: " + e.getMessage());
            }

        } else if ("IMPORT_XML".equalsIgnoreCase(operation)) {
            // Validate first before importing
            XmlParserUtil.ValidationResult xsdResult = XmlParserUtil.validateAgainstXsd(xmlFile, xsdFile);
            if (!xsdResult.isValid()) {
                request.setAttribute("resultType", "ERROR");
                request.setAttribute("resultMessage", "Cannot import: XML failed XSD schema validation: " + xsdResult.getMessage());
            } else {
                try (FileInputStream fis = new FileInputStream(xmlFile)) {
                    XmlParserUtil.ImportResult importResult = XmlParserUtil.importMenuFromXml(fis, menuItemDAO, categoryDAO);
                    request.setAttribute("resultType", importResult.isSuccess() ? "SUCCESS" : "ERROR");
                    request.setAttribute("resultMessage", importResult.getMessage());
                    if (!importResult.getWarnings().isEmpty()) {
                        request.setAttribute("warnings", importResult.getWarnings());
                    }
                } catch (Exception e) {
                    request.setAttribute("resultType", "ERROR");
                    request.setAttribute("resultMessage", "XML Import error: " + e.getMessage());
                }
            }
        }

        request.setAttribute("selectedOp", operation);
        request.getRequestDispatcher("/WEB-INF/views/admin/xml-tools.jsp").forward(request, response);
    }

    private void exportMenuAsXml(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/xml;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"canteenx-menu.xml\"");

        List<Category> categories = categoryDAO.getAllCategories();
        PrintWriter out = response.getWriter();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE canteenMenu SYSTEM \"menu.dtd\">");
        out.println("<canteenMenu xmlns=\"http://www.canteenx.com/menu\"");
        out.println("             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        out.println("             xsi:schemaLocation=\"http://www.canteenx.com/menu menu.xsd\"");
        out.println("             canteenName=\"CanteenX Central Campus\"");
        out.println("             generatedDate=\"" + today + "\">");
        out.println("    <info>");
        out.println("        <college>Campus Canteen Management</college>");
        out.println("        <timing>08:00 AM - 08:00 PM</timing>");
        out.println("        <notice>Freshly prepared daily specials</notice>");
        out.println("    </info>");
        out.println("    <categoryList>");

        for (Category cat : categories) {
            out.println("        <category id=\"" + cat.getCategoryId() + "\">");
            out.println("            <categoryName>" + escapeXml(cat.getCategoryName()) + "</categoryName>");
            out.println("            <items>");

            List<MenuItem> items = menuItemDAO.searchAndFilter(null, cat.getCategoryId(), null);
            for (MenuItem item : items) {
                out.println("                <foodItem id=\"" + item.getItemId() + "\">");
                out.println("                    <name>" + escapeXml(item.getItemName()) + "</name>");
                out.println("                    <description>" + escapeXml(item.getDescription()) + "</description>");
                out.println("                    <price>" + item.getPrice() + "</price>");
                out.println("                    <isAvailable>" + item.isAvailable() + "</isAvailable>");
                out.println("                </foodItem>");
            }
            out.println("            </items>");
            out.println("        </category>");
        }

        out.println("    </categoryList>");
        out.println("</canteenMenu>");
        out.flush();
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
