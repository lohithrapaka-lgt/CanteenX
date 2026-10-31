<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    </div>
</main>

<footer class="bg-dark text-white-50 py-4 mt-auto border-top border-secondary">
    <div class="container text-center">
        <p class="mb-1 text-white fw-bold">CanteenX – College Canteen Ordering System</p>
        <p class="small mb-0">Built with pure Web Technologies: Java Servlets &bull; JSP &bull; JDBC &bull; MySQL 8 &bull; XML/DTD/XSD &bull; Bootstrap &bull; jQuery</p>
        <p class="text-muted small mt-2">&copy; <%= java.time.Year.now() %> Department of Computer Science &amp; Engineering</p>
    </div>
</footer>

<!-- Scripts -->
<script src="<%= request.getContextPath() %>/assets/js/jquery.min.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/form-validation.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/cart-ajax.js"></script>
</body>
</html>
