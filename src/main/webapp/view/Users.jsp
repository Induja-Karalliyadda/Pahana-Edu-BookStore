<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
// Check if this JSP is being accessed directly (not through servlet)
if (request.getAttribute("userList") == null) {
    // Redirect to servlet instead of direct JSP access
    response.sendRedirect(request.getContextPath() + "/users");
    return;
}
%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>User Management - Pahana Edu Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" />
  <script defer src="${pageContext.request.contextPath}/js/admin.js"></script>
</head>
<body>
<header class="topbar">
  <div class="top-actions">
    <button id="toggleSidebar">&#9776;</button>
  </div>
  <div class="brand">Pahana Edu <span>Admin</span></div>
  <img src="${pageContext.request.contextPath}/img/avatar.png" alt="Admin" class="avatar" />
</header>

<aside id="sidebar" class="sidebar">
  <nav>
    <h4 class="menu-title">Menu</h4>
    <a href="${pageContext.request.contextPath}/view/AdminDashboard.jsp">Dashboard</a>
    <a href="#">Orders</a>
    <a href="#">Books</a>
    <a href="${pageContext.request.contextPath}/users" class="active">Users</a>
    <a href="${pageContext.request.contextPath}/staff">Staff</a>
    <a href="#">Reports</a>
    <a href="#">Settings</a>
    <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout">Logout</a>
  </nav>
</aside>

<div id="overlay" class="overlay"></div>

<main class="main" id="main">
  <section class="staff-header">
    <h1>User Management</h1>
    <form method="get" action="${pageContext.request.contextPath}/users">
      <input type="text" name="kw" placeholder="Search username or code" value="${param.kw}" />
      <button type="submit" class="btn small">Search</button>
    </form>
  </section>

 

  <section class="table-wrap">
    <table class="table">
      <thead>
        <tr>
          <th>#</th>
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
          <c:when test="${empty userList}">
            <tr><td colspan="7" style="text-align:center;">No users found.</td></tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="u" items="${userList}" varStatus="st">
              <tr>
                <td>${st.index + 1}</td>
                <td>${u.customerCode}</td>
                <td>${u.username}</td>
                <td>${u.address}</td>
                <td>${u.email}</td>
                <td>${u.telephone}</td>
                <td>
                  <form action="${pageContext.request.contextPath}/users" method="post" style="display:inline;">
                    <input type="hidden" name="action" value="delete"/>
                    <input type="hidden" name="id" value="${u.id}"/>
                    <button class="btn small" onclick="return confirm('Delete this user?')">Delete</button>
                  </form>
                  <button class="btn small" onclick="openUserEditModal(
                    ${u.id}, '${u.customerCode}', '${u.username}', '${u.address}',
                    '${u.email}', '${u.telephone}')">Edit</button>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </section>
</main>

<!-- Modal -->
<div id="userModal" class="modal">
  <div class="modal-content">
    <h2 id="modalTitle">Edit User</h2>
    <form id="userForm" action="${pageContext.request.contextPath}/users" method="post">
      <input type="hidden" name="action" value="update"/>
      <input type="hidden" name="id" id="f-id"/>
      <div class="form-group">
        <label>Customer Code</label>
        <input type="text" id="f-code" disabled/>
      </div>
      <div class="form-group">
        <label>Username</label>
        <input type="text" name="username" id="f-username" required/>
      </div>
      <div class="form-group">
        <label>Address</label>
        <input type="text" name="address" id="f-address" required/>
      </div>
      <div class="form-group">
        <label>Email</label>
        <input type="email" name="email" id="f-email" required/>
      </div>
      <div class="form-group">
        <label>Telephone</label>
        <input type="text" name="telephone" id="f-telephone" required/>
      </div>
      <div class="form-actions">
        <button type="submit" class="btn primary">Save</button>
        <button type="button" class="btn" id="userCancelBtn">Cancel</button>
      </div>
    </form>
  </div>
</div>

<script>
  function openUserEditModal(id, code, username, address, email, tel) {
    document.getElementById('modalTitle').textContent = 'Edit User';
    document.getElementById('f-id').value = id;
    document.getElementById('f-code').value = code;
    document.getElementById('f-username').value = username;
    document.getElementById('f-address').value = address;
    document.getElementById('f-email').value = email;
    document.getElementById('f-telephone').value = tel;
    document.getElementById('userModal').classList.add('show');
  }

  document.getElementById('userCancelBtn')?.addEventListener('click', () => {
    document.getElementById('userModal').classList.remove('show');
  });

  document.getElementById('userModal')?.addEventListener('click', e => {
    if (e.target === document.getElementById('userModal')) {
      document.getElementById('userModal').classList.remove('show');
    }
  });

  const sidebar = document.getElementById('sidebar');
  const overlay = document.getElementById('overlay');
  const toggleBtn = document.getElementById('toggleSidebar');
  const main = document.getElementById('main');

  function toggleSidebarFn(){
    sidebar.classList.toggle('open');
    overlay.classList.toggle('show');
    main.classList.toggle('shift');
  }

  toggleBtn?.addEventListener('click', toggleSidebarFn);
  overlay?.addEventListener('click', toggleSidebarFn);
</script>

</body>
</html>
</html>