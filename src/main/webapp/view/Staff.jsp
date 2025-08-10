<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Pahana Edu Admin | Staff Management</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" />
</head>
<body>
  <!-- TOP BAR -->
  <header class="topbar">
    <div class="top-actions">
      <button id="toggleSidebar" aria-label="Toggle menu">&#9776;</button>
    </div>
    <div class="brand">Pahana Edu <span>Admin</span></div>
    <img src="${pageContext.request.contextPath}/img/avatar.png" alt="Admin" class="avatar" />
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

  <!-- MAIN CONTENT -->
  <main class="main shift" id="main">
    <section class="staff-header">
      <h1>Staff Management</h1>
      <div class="staff-actions">
        <span class="badge" id="staffCount">
          ${empty staffList ? 0 : staffList.size()}
        </span>
        <button id="btnAddStaff" class="btn primary">+ Add Staff</button>
      </div>
    </section>

    <!-- Error Message -->
    <c:if test="${not empty errorMessage}">
      <div class="error-box">
        <strong>Error:</strong> ${errorMessage}
      </div>
    </c:if>

    <!-- STAFF TABLE -->
    <section class="table-wrap">
      <table class="table" id="staffTable">
        <thead>
          <tr>
            <th>Code</th>
            <th>Username</th>
            <th>Address</th>
            <th>Email</th>
            <th>Telephone</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty staffList}">
              <tr>
                <td colspan="6" class="no-data">
                  No staff members found. Click "Add Staff" to create one.
                </td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="s" items="${staffList}">
                <tr>
                  <td>${s.customerCode}</td>
                  <td>${s.username}</td>
                  <td>${s.address}</td>
                  <td>${s.email}</td>
                  <td>${s.telephone}</td>
                  <td>
                    <form action="${pageContext.request.contextPath}/staff"
                          method="post" style="display:inline">
                      <input type="hidden" name="action" value="delete" />
                      <input type="hidden" name="id" value="${s.id}" />
                      <button class="btn small"
                              onclick="return confirm('Delete this staff member?')">
                        Delete
                      </button>
                    </form>
                    <button class="btn small"
                            onclick="openEditModal(
                              '${s.id}',
                              '${s.username}',
                              '${s.address}',
                              '${s.email}',
                              '${s.telephone}'
                            )">
                      Edit
                    </button>
                  </td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </section>
  </main>

  <!-- ADD / EDIT MODAL -->
  <div id="modal" class="modal">
    <div class="modal-content">
      <h2 id="modalTitle">Add Staff</h2>
      <form id="staffForm" action="${pageContext.request.contextPath}/staff" method="post">
        <input type="hidden" name="action" id="f-action" value="add" />
        <input type="hidden" name="id" id="f-id" />

        <div class="form-group">
          <label for="f-username">Username</label>
          <input type="text" name="username" id="f-username" required />
        </div>
        <div class="form-group">
          <label for="f-address">Address</label>
          <input type="text" name="address" id="f-address" required />
        </div>
        <div class="form-group">
          <label for="f-email">Email</label>
          <input type="email" name="email" id="f-email" required />
        </div>
        <div class="form-group">
          <label for="f-telephone">Telephone</label>
          <input type="text" name="telephone" id="f-telephone" required />
        </div>
        <div class="form-group" id="pwRow">
          <label for="f-password">Password</label>
          <input type="password" name="password" id="f-password" required />
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">Save</button>
          <button type="button" class="btn" id="cancelBtn">Cancel</button>
        </div>
      </form>
    </div>
  </div>

  <!-- EXTERNAL JS -->
  <script src="${pageContext.request.contextPath}/js/admin.js" defer></script>
</body>
</html>


  <!-- ADD / EDIT MODAL -->
  <div id="modal" class="modal">
    <div class="modal-content">
      <h2 id="modalTitle">Add Staff</h2>
      <form id="staffForm" action="${pageContext.request.contextPath}/staff" method="post">
        <input type="hidden" name="action" id="f-action" value="add" />
        <input type="hidden" name="id" id="f-id" />

        <div class="form-group">
          <label for="f-username">Username</label>
          <input type="text" name="username" id="f-username" required />
        </div>
        <div class="form-group">
          <label for="f-address">Address</label>
          <input type="text" name="address" id="f-address" required />
        </div>
        <div class="form-group">
          <label for="f-email">Email</label>
          <input type="email" name="email" id="f-email" required />
        </div>
        <div class="form-group">
          <label for="f-telephone">Telephone</label>
          <input type="text" name="telephone" id="f-telephone" required />
        </div>
        <div class="form-group" id="pwRow">
          <label for="f-password">Password</label>
          <input type="password" name="password" id="f-password" required />
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">Save</button>
          <button type="button" class="btn" id="cancelBtn">Cancel</button>
        </div>
      </form>
    </div>
  </div>

  <!-- EXTERNAL JS -->
  <script src="${pageContext.request.contextPath}/js/admin.js" defer></script>
</body>
</html>


