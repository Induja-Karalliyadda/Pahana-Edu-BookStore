<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>Pahana Edu BookStore</title>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
  <script src="<%=request.getContextPath()%>/js/script.js" defer></script>
</head>
<body>
  <!-- NAVBAR -->
  <nav class="navbar">
    <div class="nav-left">
      <a href="UserDashboard.jsp" class="logo">Pahana Edu BookStore</a>
    </div>
    <div class="nav-center">
      <a href="shop.jsp">Shop</a>
      <a href="blog.jsp">Blog</a>
      <a href="contact.jsp">Contact</a>
    </div>
    <div class="nav-right">
      <a href="view/cart.jsp" class="icon">&#128722;</a>
      <a href="userProfile.jsp" class="icon" aria-label="User profile">&#128100;</a>
      <button id="login-signup-btn" href="view/cart.jsp">Log out </button>
    </div>
  </nav>
  <br>
  <!-- PAGE‑SPECIFIC CONTENT -->
  <div class="content">
    <h1>Welcome to Pahana Edu BookStore</h1>
    <p>Discover your next favourite read!</p>
  </div>
  
</body>
</html>
    