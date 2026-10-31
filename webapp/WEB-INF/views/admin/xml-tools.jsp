<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.canteenx.model.MenuItem"%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<%
    String resultType = (String) request.getAttribute("resultType");
    String resultMessage = (String) request.getAttribute("resultMessage");
    String selectedOp = (String) request.getAttribute("selectedOp");
    List<MenuItem> parsedItems = (List<MenuItem>) request.getAttribute("parsedItems");
    List<String> warnings = (List<String>) request.getAttribute("warnings");
    String ctx = request.getContextPath();
%>

<div class="row align-items-center mb-4">
    <div class="col-md-8">
        <h2 class="fw-bold mb-1">XML Technologies Suite ⚙️</h2>
        <p class="text-muted mb-0">Demonstrating WT syllabus concepts: XML Schemas (XSD), DTDs, DOM Parsers, SAX Parsers, XML Import, and XML Generation.</p>
    </div>
    <div class="col-md-4 text-md-end mt-3 mt-md-0">
        <a href="<%= response.encodeURL(ctx + "/admin/xml-export") %>" class="btn btn-outline-primary fw-bold" target="_blank">
            ⬇️ Download Live Menu as XML
        </a>
    </div>
</div>

<!-- Operation Result Display -->
<% if (resultMessage != null) { %>
    <div class="alert <%= "SUCCESS".equals(resultType) ? "alert-success" : "alert-danger" %> alert-dismissible fade show shadow-sm" role="alert">
        <h5 class="alert-heading fw-bold mb-1">
            <%= "SUCCESS".equals(resultType) ? "✓ Validation / Operation Succeeded" : "⚠️ Error Encountered" %>
        </h5>
        <p class="mb-0"><%= resultMessage %></p>
        <% if (warnings != null && !warnings.isEmpty()) { %>
            <ul class="mt-2 mb-0 small">
                <% for (String w : warnings) { %>
                    <li><%= w %></li>
                <% } %>
            </ul>
        <% } %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<% } %>

<!-- Parsed Items Grid if DOM parser was run -->
<% if (parsedItems != null && !parsedItems.isEmpty()) { %>
    <div class="card shadow-sm border-0 rounded-3 mb-4">
        <div class="card-header bg-dark text-white py-3">
            <h5 class="mb-0 fw-bold">Parsed Food Items from DOM Tree (<%= parsedItems.size() %> items)</h5>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered table-striped mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Item ID</th>
                        <th>Category</th>
                        <th>Food Name</th>
                        <th>Description</th>
                        <th class="text-end">Price</th>
                        <th class="text-center">Available</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (MenuItem mi : parsedItems) { %>
                        <tr>
                            <td>#<%= mi.getItemId() %></td>
                            <td><span class="badge bg-light text-dark border"><%= mi.getCategoryName() != null ? mi.getCategoryName() : "General" %></span></td>
                            <td><strong><%= mi.getItemName() %></strong></td>
                            <td class="small text-muted"><%= mi.getDescription() %></td>
                            <td class="text-end fw-bold">₹<%= String.format("%.2f", mi.getPrice()) %></td>
                            <td class="text-center">
                                <span class="badge <%= mi.isAvailable() ? "bg-success" : "bg-danger" %>">
                                    <%= mi.isAvailable() ? "In Stock" : "Unavailable" %>
                                </span>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
<% } %>

<!-- Interactive Test Cards -->
<div class="row g-4 mb-4">
    <!-- Test 1: XSD Validation -->
    <div class="col-md-6 col-lg-4">
        <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex flex-column p-4">
                <h5 class="fw-bold text-primary mb-2">1. XML Schema (XSD)</h5>
                <p class="card-text text-muted small flex-grow-1">
                    Validates <code>sample-menu.xml</code> against <code>menu.xsd</code> schema rules, element constraints, and typed decimal facets using <code>javax.xml.validation.Validator</code>.
                </p>
                <form action="<%= response.encodeURL(ctx + "/admin/xml-tools") %>" method="POST">
                    <input type="hidden" name="operation" value="VALIDATE_XSD">
                    <button type="submit" class="btn btn-outline-primary w-100 fw-semibold">
                        Validate against XSD
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- Test 2: DTD Validation -->
    <div class="col-md-6 col-lg-4">
        <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex flex-column p-4">
                <h5 class="fw-bold text-success mb-2">2. DTD Validation</h5>
                <p class="card-text text-muted small flex-grow-1">
                    Validates <code>sample-menu.xml</code> against the Document Type Definition <code>menu.dtd</code> grammar using validating <code>DocumentBuilderFactory</code> with local EntityResolver.
                </p>
                <form action="<%= response.encodeURL(ctx + "/admin/xml-tools") %>" method="POST">
                    <input type="hidden" name="operation" value="VALIDATE_DTD">
                    <button type="submit" class="btn btn-outline-success w-100 fw-semibold">
                        Validate against DTD
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- Test 3: DOM Parser -->
    <div class="col-md-6 col-lg-4">
        <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex flex-column p-4">
                <h5 class="fw-bold text-warning mb-2">3. DOM Parser</h5>
                <p class="card-text text-muted small flex-grow-1">
                    Constructs an in-memory Document Object Model tree from the XML file, traversing categories and food nodes with XXE protection.
                </p>
                <form action="<%= response.encodeURL(ctx + "/admin/xml-tools") %>" method="POST">
                    <input type="hidden" name="operation" value="PARSE_DOM">
                    <button type="submit" class="btn btn-outline-warning text-dark w-100 fw-semibold">
                        Execute DOM Parse
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- Test 4: SAX Parser -->
    <div class="col-md-6 col-lg-4">
        <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex flex-column p-4">
                <h5 class="fw-bold text-info mb-2">4. SAX Parser</h5>
                <p class="card-text text-muted small flex-grow-1">
                    Event-driven stream parser using <code>DefaultHandler</code> to process opening/closing tags and stream element counts without memory overhead.
                </p>
                <form action="<%= response.encodeURL(ctx + "/admin/xml-tools") %>" method="POST">
                    <input type="hidden" name="operation" value="PARSE_SAX">
                    <button type="submit" class="btn btn-outline-info w-100 fw-semibold">
                        Execute SAX Parse
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- Test 5: XML Menu Import to Database -->
    <div class="col-md-6 col-lg-8">
        <div class="card h-100 shadow-sm border-0 border-start border-4 border-warning bg-light">
            <div class="card-body d-flex flex-column p-4">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h5 class="fw-bold text-dark mb-0">5. Import XML Menu to Database 🚀</h5>
                    <span class="badge bg-warning text-dark">DB Sync</span>
                </div>
                <p class="card-text text-muted small flex-grow-1">
                    Validates <code>sample-menu.xml</code> against the schema, enforces price (&gt;0 and &le;5000) and category constraints, maps verified dish images, and synchronizes the food items with MySQL.
                </p>
                <form action="<%= response.encodeURL(ctx + "/admin/xml-tools") %>" method="POST" onsubmit="return confirm('Synchronize database menu with sample-menu.xml? Existing items will be updated.');">
                    <input type="hidden" name="operation" value="IMPORT_XML">
                    <button type="submit" class="btn btn-warning fw-bold px-4">
                        📥 Import &amp; Sync Database from XML
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Schema Reference View -->
<div class="card shadow-sm border-0 rounded-3 mb-4">
    <div class="card-header bg-light py-3">
        <h5 class="mb-0 fw-bold">XML Configuration Files in WEB-INF/xml/</h5>
    </div>
    <div class="card-body">
        <ul class="list-group list-group-flush small">
            <li class="list-group-item d-flex justify-content-between align-items-center">
                <span><code>sample-menu.xml</code> – Validated XML data source with DTD &amp; XSD definitions</span>
                <span class="badge bg-secondary">XML Data</span>
            </li>
            <li class="list-group-item d-flex justify-content-between align-items-center">
                <span><code>menu.dtd</code> – Document Type Definition declaring element hierarchies and attributes</span>
                <span class="badge bg-secondary">DTD Grammar</span>
            </li>
            <li class="list-group-item d-flex justify-content-between align-items-center">
                <span><code>menu.xsd</code> – W3C XML Schema Definition with typed restrictions and decimal facets</span>
                <span class="badge bg-secondary">XSD Schema</span>
            </li>
        </ul>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
