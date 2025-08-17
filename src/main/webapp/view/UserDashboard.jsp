<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.pahanaedu.model.Book" %>
<%@ page import="com.pahanaedu.model.Accessory" %>
<%@ page import="com.pahanaedu.model.User" %>
<%
    // ---- Session / user guard ----
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp"); // use context path
        return;
    }

    // ---- Cart badge (Number-safe) ----
    Object cartItemCountObj = session.getAttribute("cartItemCount");
    int cartItemCount = (cartItemCountObj instanceof Number) ? ((Number)cartItemCountObj).intValue() : 0;

    // Convenience for context path
    String ctx = request.getContextPath();
%>
<%
    // already have the login guard above
    if (request.getAttribute("books") == null || request.getAttribute("accessories") == null) {
        String qs = request.getQueryString();
        response.sendRedirect(request.getContextPath() + "/UserDashboardController" + (qs != null ? "?" + qs : ""));
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Pahana Edu BookStore - Dashboard</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=ctx%>/css/style.css">
    <style>
        /* (your styles unchanged) */
        *{margin:0;padding:0;box-sizing:border-box}
        body{font-family:'Arial',sans-serif;background-color:#ecf0f1;color:#2c3e50;line-height:1.6}
        .navbar{background-color:#8e44ad;padding:1rem 2rem;display:flex;justify-content:space-between;align-items:center;box-shadow:0 2px 10px rgba(0,0,0,0.1);position:sticky;top:0;z-index:100}
        .navbar .logo{color:white;text-decoration:none;font-size:1.5rem;font-weight:bold}
        .nav-center{display:flex;gap:2rem}
        .nav-center a{color:white;text-decoration:none;padding:.5rem 1rem;border-radius:5px;transition:background-color .3s}
        .nav-center a:hover{background-color:rgba(255,255,255,.1)}
        .nav-right{display:flex;align-items:center;gap:1rem}
        .nav-right .icon{color:white;text-decoration:none;font-size:1.2rem;padding:.5rem;border-radius:50%;transition:background-color .3s;position:relative}
        .nav-right .icon:hover{background-color:rgba(255,255,255,.1)}
        .cart-badge{position:absolute;top:-5px;right:-5px;background-color:#e74c3c;color:white;border-radius:50%;width:20px;height:20px;font-size:.8rem;display:flex;align-items:center;justify-content:center}
        .logout-btn{background-color:#e74c3c;color:white;border:none;padding:.5rem 1rem;border-radius:5px;cursor:pointer;text-decoration:none;transition:background-color .3s}
        .logout-btn:hover{background-color:#c0392b}
        .welcome-section{background:linear-gradient(135deg,#8e44ad,#9b59b6);color:white;padding:2rem;text-align:center}
        .welcome-section h1{font-size:2.5rem;margin-bottom:.5rem}
        .welcome-section p{font-size:1.2rem;opacity:.9}
        .container{max-width:1200px;margin:0 auto;padding:2rem}
        .filters{background:white;padding:1.5rem;border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,.1);margin-bottom:2rem}
        .filter-row{display:flex;gap:1rem;align-items:center;flex-wrap:wrap}
        .filter-group{display:flex;flex-direction:column;gap:.5rem}
        .filter-group label{font-weight:bold;color:#8e44ad}
        .filter-group input,.filter-group select{padding:.5rem;border:2px solid #ecf0f1;border-radius:5px;font-size:1rem}
        .filter-group input:focus,.filter-group select:focus{outline:none;border-color:#8e44ad}
        .filter-btn{background-color:#8e44ad;color:white;border:none;padding:.75rem 1.5rem;border-radius:5px;cursor:pointer;font-size:1rem;transition:background-color .3s;margin-top:1.5rem}
        .filter-btn:hover{background-color:#7d3c98}
        .section-title{font-size:2rem;color:#8e44ad;margin-bottom:1.5rem;text-align:center;position:relative}
        .section-title::after{content:'';display:block;width:80px;height:3px;background-color:#8e44ad;margin:.5rem auto}
        .items-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(280px,1fr));gap:2rem;margin-bottom:3rem}
        .item-card{background:white;border-radius:15px;overflow:hidden;box-shadow:0 5px 20px rgba(0,0,0,.1);transition:transform .3s,box-shadow .3s}
        .item-card:hover{transform:translateY(-5px);box-shadow:0 10px 30px rgba(142,68,173,.2)}
        .item-image{width:100%;height:200px;object-fit:cover;background-color:#f8f9fa}
        .item-info{padding:1.5rem}
        .item-title{font-size:1.2rem;font-weight:bold;color:#2c3e50;margin-bottom:.5rem;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden}
        .item-author{color:#7f8c8d;margin-bottom:.5rem;font-style:italic}
        .item-category{background-color:#ecf0f1;color:#8e44ad;padding:.25rem .5rem;border-radius:15px;font-size:.8rem;display:inline-block;margin-bottom:1rem}
        .item-price{font-size:1.5rem;font-weight:bold;color:#27ae60;margin-bottom:1rem}
        .item-stock{color:#7f8c8d;font-size:.9rem;margin-bottom:1rem}
        .add-to-cart-form{display:flex;gap:.5rem;align-items:center}
        .quantity-input{width:60px;padding:.5rem;border:2px solid #ecf0f1;border-radius:5px;text-align:center}
        .add-to-cart-btn{background-color:#8e44ad;color:white;border:none;padding:.75rem 1rem;border-radius:5px;cursor:pointer;font-size:.9rem;transition:background-color .3s;flex:1}
        .add-to-cart-btn:hover{background-color:#7d3c98}
        .add-to-cart-btn:disabled{background-color:#bdc3c7;cursor:not-allowed}
        .alert{padding:1rem;border-radius:5px;margin-bottom:1rem}
        .alert-success{background-color:#d4edda;color:#155724;border:1px solid #c3e6cb}
        .alert-error{background-color:#f8d7da;color:#721c24;border:1px solid #f5c6cb}
        .no-items{text-align:center;color:#7f8c8d;font-size:1.2rem;padding:3rem}
        @media (max-width:768px){
            .navbar{flex-direction:column;gap:1rem}
            .nav-center{order:3}
            .filter-row{flex-direction:column;align-items:stretch}
            .items-grid{grid-template-columns:repeat(auto-fill,minmax(250px,1fr));gap:1rem}
            .welcome-section h1{font-size:2rem}
        }
    </style>
</head>
<body>
    <!-- NAVBAR -->
    <nav class="navbar">
        <div class="nav-left">
            <a href="<%=ctx%>/UserDashboardController" class="logo">Pahana Edu BookStore</a>
        </div>
        <div class="nav-center">
    <a href="<%=ctx%>/UserDashboardController">Shop</a>
  <a href="<%=ctx%>/view/blog.jsp">Blog</a>
  <a href="<%=ctx%>/view/contact.jsp">Contact</a>
</div>
        
        <div class="nav-right">
            <a href="<%=ctx%>/view/cart.jsp" class="icon" title="Shopping Cart">
                &#128722;
                <% if (cartItemCount > 0) { %>
                    <span class="cart-badge"><%= cartItemCount %></span>
                <% } %>
            </a>
            <a href="<%=ctx%>/userProfile.jsp" class="icon" aria-label="User profile" title="Profile">&#128100;</a>
            <span style="color: white; margin-right: 1rem;">Welcome, <%= user.getUsername() %>!</span>
            <a href="<%=ctx%>/logout" class="logout-btn">Log out</a>
        </div>
    </nav>

    <!-- WELCOME SECTION -->
    <div class="welcome-section">
        <h1>Welcome to Pahana Edu BookStore</h1>
        <p>Discover your next favourite read and essential accessories!</p>
    </div>

    <div class="container">
        <!-- ALERTS -->
        <%
            String message = (String) request.getAttribute("message");
            if (message != null) {
        %>
            <div class="alert alert-success"><%= message %></div>
        <% } %>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
            <div class="alert alert-error"><%= error %></div>
        <% } %>

        <!-- FILTERS -->
        <div class="filters">
            <form method="get" action="<%=ctx%>/UserDashboardController">
                <div class="filter-row">
                    <div class="filter-group">
                        <label for="bookSearch">Search Books:</label>
                        <input type="text" id="bookSearch" name="bookSearch"
                               value="<%= request.getParameter("bookSearch") != null ? request.getParameter("bookSearch") : "" %>"
                               placeholder="Enter book title...">
                    </div>

                    <div class="filter-group">
                        <label for="bookCategory">Book Category:</label>
                        <select id="bookCategory" name="bookCategory">
                            <option value="">All Categories</option>
                            <%
                                List<String> bookCategories = (List<String>) request.getAttribute("bookCategories");
                                String selectedBookCategory = request.getParameter("bookCategory");
                                if (bookCategories != null) {
                                    for (String category : bookCategories) {
                            %>
                                <option value="<%= category %>" <%= (selectedBookCategory!=null && category.equals(selectedBookCategory)) ? "selected" : "" %>>
                                    <%= category %>
                                </option>
                            <%      }
                                } %>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label for="accessorySearch">Search Accessories:</label>
                        <input type="text" id="accessorySearch" name="accessorySearch"
                               value="<%= request.getParameter("accessorySearch") != null ? request.getParameter("accessorySearch") : "" %>"
                               placeholder="Enter accessory name...">
                    </div>

                    <div class="filter-group">
                        <label for="accessoryCategory">Accessory Category:</label>
                        <select id="accessoryCategory" name="accessoryCategory">
                            <option value="">All Categories</option>
                            <%
                                List<String> accessoryCategories = (List<String>) request.getAttribute("accessoryCategories");
                                String selectedAccessoryCategory = request.getParameter("accessoryCategory");
                                if (accessoryCategories != null) {
                                    for (String category : accessoryCategories) {
                            %>
                                <option value="<%= category %>" <%= (selectedAccessoryCategory!=null && category.equals(selectedAccessoryCategory)) ? "selected" : "" %>>
                                    <%= category %>
                                </option>
                            <%      }
                                } %>
                        </select>
                    </div>
                </div>
                <button type="submit" class="filter-btn">Apply Filters</button>
            </form>
        </div>

        <!-- BOOKS SECTION -->
        <h2 class="section-title">Books</h2>
        <div class="items-grid">
            <%
                List<Book> books = (List<Book>) request.getAttribute("books");
                if (books != null && !books.isEmpty()) {
                    for (Book book : books) {
                        String bookImg = (book.getImageUrl()!=null && !book.getImageUrl().trim().isEmpty())
                                ? book.getImageUrl() : "img/default-book.jpg";
            %>
                <div class="item-card">
                    <img src="<%= ctx %>/<%= bookImg %>"
                         alt="<%= book.getTitle() %>" class="item-image"
                         onerror="this.src='<%= ctx %>/img/default-book.jpg'">

                    <div class="item-info">
                        <div class="item-title"><%= book.getTitle() %></div>
                        <div class="item-author">by <%= book.getAuthor() %></div>
                        <div class="item-category"><%= book.getCategory() %></div>
                        <div class="item-price">Rs. <%= String.format("%.2f", book.getSellingPrice()) %></div>
                        <div class="item-stock">
                            <% if (book.getStock() > 0) { %>
                                Stock: <%= book.getStock() %> available
                            <% } else { %>
                                <span style="color: #e74c3c;">Out of Stock</span>
                            <% } %>
                        </div>

                        <% if (book.getStock() > 0) { %>
                            <form method="post" action="<%=ctx%>/UserDashboardController" class="add-to-cart-form">
                                <input type="hidden" name="action" value="addToCart">
                                <input type="hidden" name="itemType" value="book">
                                <input type="hidden" name="itemId" value="<%= book.getBookId() %>">
                                <input type="number" name="quantity" value="1" min="1" max="<%= book.getStock() %>"
                                       class="quantity-input" required>
                                <button type="submit" class="add-to-cart-btn">Add to Cart</button>
                            </form>
                        <% } else { %>
                            <button class="add-to-cart-btn" disabled>Out of Stock</button>
                        <% } %>
                    </div>
                </div>
            <%
                    }
                } else {
            %>
                <div class="no-items">No books found. Try adjusting your search filters.</div>
            <% } %>
        </div>

        <!-- ACCESSORIES SECTION -->
        <h2 class="section-title">Accessories</h2>
        <div class="items-grid">
            <%
                List<Accessory> accessories = (List<Accessory>) request.getAttribute("accessories");
                if (accessories != null && !accessories.isEmpty()) {
                    for (Accessory accessory : accessories) {
                        String accImg = (accessory.getImageUrl()!=null && !accessory.getImageUrl().trim().isEmpty())
                                ? accessory.getImageUrl() : "img/default-accessory.jpg";
            %>
                <div class="item-card">
                    <img src="<%= ctx %>/<%= accImg %>"
                         alt="<%= accessory.getName() %>" class="item-image"
                         onerror="this.src='<%= ctx %>/img/default-accessory.jpg'">

                    <div class="item-info">
                        <div class="item-title"><%= accessory.getName() %></div>
                        <div class="item-category"><%= accessory.getCategory() %></div>
                        <div class="item-price">Rs. <%= String.format("%.2f", accessory.getSellingPrice()) %></div>
                        <div class="item-stock">
                            <% if (accessory.getStock() > 0) { %>
                                Stock: <%= accessory.getStock() %> available
                            <% } else { %>
                                <span style="color: #e74c3c;">Out of Stock</span>
                            <% } %>
                        </div>

                        <% if (accessory.getStock() > 0) { %>
                            <form method="post" action="<%=ctx%>/UserDashboardController" class="add-to-cart-form">
                                <input type="hidden" name="action" value="addToCart">
                                <input type="hidden" name="itemType" value="accessory">
                                <input type="hidden" name="itemId" value="<%= accessory.getAccessoryId() %>">
                                <input type="number" name="quantity" value="1" min="1" max="<%= accessory.getStock() %>"
                                       class="quantity-input" required>
                                <button type="submit" class="add-to-cart-btn">Add to Cart</button>
                            </form>
                        <% } else { %>
                            <button class="add-to-cart-btn" disabled>Out of Stock</button>
                        <% } %>
                    </div>
                </div>
            <%
                    }
                } else {
            %>
                <div class="no-items">No accessories found. Try adjusting your search filters.</div>
            <% } %>
        </div>
    </div>

    <script>
        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            var alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                alert.style.opacity = '0';
                setTimeout(function() { alert.style.display = 'none'; }, 500);
            });
        }, 5000);
    </script>
</body>
</html>

    