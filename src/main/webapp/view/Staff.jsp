<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Pahana Edu Admin | Staff</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" />
  <script defer src="${pageContext.request.contextPath}/js/admin.js"></script>
</head>
<body>
  <!-- TOP BAR -->
  <header class="topbar">
    <div class="top-actions"><button id="toggleSidebar">&#9776;</button></div>
    <div class="brand">Pahana Edu <span>Admin</span></div>
    <img src="${pageContext.request.contextPath}/img/avatar.png" class="avatar" alt="Admin">
  </header>

  <!-- SIDEBAR -->
  <aside id="sidebar" class="sidebar open">
    <nav>
      <h4 class="menu-title">Menu</h4>
      <a href="${pageContext.request.contextPath}/view/AdminDashboard.jsp">Dashboard</a>
      <a href="#">Orders</a>
      <a href="#">Books</a>
      <a href="#">Users</a>
      <a href="${pageContext.request.contextPath}/staff" class="active">Staff</a>
      <a href="#">Reports</a>
      <a href="#">Settings</a>
      <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout">Logout</a>
    </nav>
  </aside>
  <div id="overlay" class="overlay show"></div>

  <!-- MAIN -->
  <main class="main shift" id="main">
    <section class="staff-header">
      <h1>Staff Management</h1>
      <div class="staff-actions">
        <span class="badge" id="staffCount">${empty staffList ? 0 : staffList.size()}</span>
        <button id="btnAddStaff" class="btn primary">+ Add Staff</button>
      </div>
    </section>

    <section class="table-wrap">
      <table class="table" id="staffTable">
        <thead>
        <tr>
          <th>#</th><th>Name</th><th>Username</th><th>Email</th><th>Telephone</th><th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${staffList}" varStatus="i">
          <tr>
            <td>${i.index + 1}</td>
            <td>${s.address}</td>
            <td>${s.username}</td>
            <td>${s.email}</td>
            <td>${s.telephone}</td>
            <td>
              <form action="${pageContext.request.contextPath}/staff" method="post" style="display:inline;">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="${s.id}">
                <button class="btn small" onclick="return confirm('Delete?')">Delete</button>
              </form>
              <button class="btn small"
                      onclick="openEditModal(${s.id},'${s.address}','${s.username}','${s.email}','${s.telephone}')">
                Edit
              </button>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </section>
  </main>

  <!-- MODAL -->
  <div id="modal" class="modal">
    <div class="modal-content">
      <h2 id="modalTitle">Add Staff</h2>
      <form id="staffForm" action="${pageContext.request.contextPath}/staff" method="post">
        <input type="hidden" name="action" id="f-action" value="add">
        <input type="hidden" name="id" id="f-id">

        <div class="form-group">
          <label>Name</label>
          <input type="text" name="address" id="f-name" required>
        </div>
        <div class="form-group">
          <label>Username</label>
          <input type="text" name="username" id="f-username" required>
        </div>
        <div class="form-group">
          <label>Email</label>
          <input type="email" name="email" id="f-email" required>
        </div>
        <div class="form-group">
          <label>Telephone</label>
          <input type="text" name="telephone" id="f-telephone" required>
        </div>
        <div class="form-group" id="pwRow">
          <label>Password</label>
          <input type="password" name="password" id="f-password" required>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">Save</button>
          <button type="button" class="btn" id="cancelBtn">Cancel</button>
        </div>
      </form>
    </div>
  </div>
</body>
</html>

