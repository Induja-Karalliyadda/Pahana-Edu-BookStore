<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Pahana Edu Admin</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" />
  <script defer src="${pageContext.request.contextPath}/js/admin.js"></script>
</head>
<body>

  <!-- TOP BAR -->
  <header class="topbar">
    <div class="top-actions">
      <button id="toggleSidebar" aria-label="menu">&#9776;</button>
    </div>

    <div class="brand">Pahana Edu <span>Admin</span></div>

    <img src="${pageContext.request.contextPath}/img/avatar.png" alt="Admin" class="avatar">
  </header>

  <!-- SLIDE SIDEBAR (LEFT) -->
  <aside id="sidebar" class="sidebar">
    <nav>
      <h4 class="menu-title">Menu</h4>
      <a href="./AdminDashboard.jsp" class="active">Dashboard</a>
      <a href="#">Orders</a>
      <a href="./Item.jsp">Items</a>
      <a href="./Users.jsp">Users</a>
      <a href="./Staff.jsp">Staff</a>
      <a href="#">Reports</a>
      <a href="#">Settings</a>
      <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout">Logout</a>
    </nav>
  </aside>

  <!-- OVERLAY -->
  <div id="overlay" class="overlay"></div>

  <!-- MAIN CONTENT -->
  <main class="main" id="main">
    <section class="cards">
      <div class="card">
        <h3>Total Orders</h3>
        <p>245</p>
      </div>
      <div class="card">
        <h3>Total Books</h3>
        <p>1,128</p>
      </div>
      <div class="card">
        <h3>Users</h3>
        <p>342</p>
      </div>
      <div class="card">
        <h3>Revenue</h3>
        <p>Rs. 1.2M</p>
      </div>
    </section>

    <section class="table-wrap">
      <h2>Recent Orders</h2>
      <table class="table">
        <thead>
          <tr>
            <th>#</th><th>Customer</th><th>Book</th><th>Total</th><th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr><td>0012</td><td>Saman</td><td>Java Basics</td><td>Rs. 2,500</td><td>Paid</td></tr>
          <tr><td>0011</td><td>Nimali</td><td>Spring Boot</td><td>Rs. 3,800</td><td>Pending</td></tr>
          <tr><td>0010</td><td>Ruwan</td><td>Algorithms</td><td>Rs. 4,200</td><td>Paid</td></tr>
        </tbody>
      </table>
    </section>
  </main>

</body>
</html>

    