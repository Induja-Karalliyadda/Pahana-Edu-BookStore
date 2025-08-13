<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // Check if user is logged in and get their role from session
    String role = null;
    
    // Use the implicit session object
    if (session != null) {
        role = (String) session.getAttribute("role");
    }
    
    // If role is not in session, check cookies
    if (role == null) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("role".equals(cookie.getName())) {
                    role = cookie.getValue();
                    // Set in session for consistency
                    if (session != null) {
                        session.setAttribute("role", role);
                    }
                    break;
                }
            }
        }
    }

    // If role is not 'admin', redirect appropriately
    if (role == null || !role.equals("admin")) {
        if ("staff".equals(role)) {
            // Staff members go to their dashboard
            response.sendRedirect(request.getContextPath() + "/AdminPOSController");
        } else {
            // Others go to login
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Pahana Edu Admin | Staff Management</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" />
  
  <!-- Additional CSS for Modal if not in admin.css -->
  <style>
    .modal {
      display: none;
      position: fixed;
      z-index: 1000;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      overflow: auto;
      background-color: rgba(0, 0, 0, 0.5);
      align-items: center;
      justify-content: center;
    }
    
    .modal-content {
      background-color: #fefefe;
      margin: 5% auto;
      padding: 30px;
      border: 1px solid #888;
      border-radius: 8px;
      width: 90%;
      max-width: 500px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }
    
    .modal-content h2 {
      margin-top: 0;
      color: #333;
      border-bottom: 2px solid #007bff;
      padding-bottom: 10px;
    }
    
    .form-group {
      margin-bottom: 20px;
    }
    
    .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: bold;
      color: #555;
    }
    
    .form-group input {
      width: 100%;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 14px;
      box-sizing: border-box;
    }
    
    .form-group input:focus {
      outline: none;
      border-color: #007bff;
      box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.1);
    }
    
    .form-actions {
      display: flex;
      gap: 10px;
      justify-content: flex-end;
      margin-top: 25px;
      padding-top: 20px;
      border-top: 1px solid #eee;
    }
    
    .required {
      color: #e74c3c;
    }
    
    .success-box {
      background-color: #d4edda;
      border: 1px solid #c3e6cb;
      color: #155724;
      padding: 12px;
      border-radius: 4px;
      margin-bottom: 20px;
    }
    
    .error-box {
      background-color: #f8d7da;
      border: 1px solid #f5c6cb;
      color: #721c24;
      padding: 12px;
      border-radius: 4px;
      margin-bottom: 20px;
    }
    
    .btn {
      padding: 10px 20px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
      transition: background-color 0.3s;
    }
    
    .btn.primary {
      background-color: #007bff;
      color: white;
    }
    
    .btn.primary:hover {
      background-color: #0056b3;
    }
    
    .btn.danger {
      background-color: #dc3545;
      color: white;
    }
    
    .btn.danger:hover {
      background-color: #c82333;
    }
    
    .btn.small {
      padding: 5px 10px;
      font-size: 12px;
    }
    
    .badge {
      background-color: #007bff;
      color: white;
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 12px;
      margin-right: 10px;
    }
  </style>
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
  <aside id="sidebar" class="sidebar">
    <nav>
      <h4 class="menu-title">Menu</h4>
      <c:url var="dashboardUrl" value="/AdminPOSController"/>
      <c:url var="itemsBookUrl" value="/items"><c:param name="mainCategory" value="book"/></c:url>
      <c:url var="usersUrl" value="/users"/>
      <c:url var="staffUrl" value="/staff"/>
      <c:url var="logoutUrl" value="/logout"/>
      

      <a href="${dashboardUrl}">Dashboard</a>
      <a href="#">Orders</a>
      <a href="${itemsBookUrl}">Items</a>
      <a href="${usersUrl}">Users</a>
      <a href="${staffUrl}" class="active">Staff</a>
      <a href="#">Reports</a>
      <a href="#">Settings</a>
      <a href="${logoutUrl}" class="logout">Logout</a>
    </nav>
  </aside>
  
  <div id="overlay" class="overlay"></div>

  <!-- MAIN CONTENT -->
  <main class="main" id="main">
    <section class="staff-header">
      <h1>Staff Management</h1>
      <div class="staff-actions">
        <span class="badge" id="staffCount">
          Total: ${empty staffList ? 0 : staffList.size()}
        </span>
        <button id="btnAddStaff" class="btn primary" type="button">+ Add Staff</button>
      </div>
    </section>

    <!-- Success Message -->
    <c:if test="${not empty successMessage}">
      <div class="success-box">
        <strong>Success:</strong> ${successMessage}
      </div>
    </c:if>

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
                <td colspan="6" class="no-data" style="text-align: center; padding: 20px;">
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
                    <button class="btn small primary" type="button"
                            onclick="openEditModal(
                              '${s.id}',
                              '${s.username}',
                              '${s.address}',
                              '${s.email}',
                              '${s.telephone}'
                            )">
                      Edit
                    </button>
                    <form action="${pageContext.request.contextPath}/staff"
                          method="post" style="display:inline">
                      <input type="hidden" name="action" value="delete" />
                      <input type="hidden" name="id" value="${s.id}" />
                      <button class="btn small danger" type="submit"
                              onclick="return confirm('Are you sure you want to delete this staff member?')">
                        Delete
                      </button>
                    </form>
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
          <label for="f-username">Username <span class="required">*</span></label>
          <input type="text" 
                 name="username" 
                 id="f-username" 
                 placeholder="Enter username"
                 required />
        </div>
        
        <div class="form-group">
          <label for="f-address">Address <span class="required">*</span></label>
          <input type="text" 
                 name="address" 
                 id="f-address" 
                 placeholder="Enter address"
                 required />
        </div>
        
        <div class="form-group">
          <label for="f-email">Email <span class="required">*</span></label>
          <input type="email" 
                 name="email" 
                 id="f-email" 
                 placeholder="Enter email address"
                 required />
        </div>
        
        <div class="form-group">
          <label for="f-telephone">Telephone <span class="required">*</span></label>
          <input type="text" 
                 name="telephone" 
                 id="f-telephone" 
                 pattern="[0-9]{10}" 
                 placeholder="Enter 10-digit phone number"
                 title="Please enter a valid 10-digit phone number"
                 required />
        </div>
        
        <div class="form-group" id="pwRow">
          <label for="f-password">Password <span class="required">*</span></label>
          <input type="password" 
                 name="password" 
                 id="f-password" 
                 minlength="6"
                 placeholder="Minimum 6 characters"
                 title="Password must be at least 6 characters long"
                 required />
        </div>

        <div class="form-actions">
          <button type="button" class="btn" id="cancelBtn">Cancel</button>
          <button type="submit" class="btn primary">Save Staff</button>
        </div>
      </form>
    </div>
  </div>

  <!-- JavaScript for Modal Functionality -->
  <script>
    // Wait for DOM to load
    document.addEventListener('DOMContentLoaded', function() {
      
      // Get modal elements
      const modal = document.getElementById('modal');
      const btnAddStaff = document.getElementById('btnAddStaff');
      const cancelBtn = document.getElementById('cancelBtn');
      const modalTitle = document.getElementById('modalTitle');
      const staffForm = document.getElementById('staffForm');
      const actionField = document.getElementById('f-action');
      const idField = document.getElementById('f-id');
      const pwRow = document.getElementById('pwRow');

      // Debug log
      console.log('Modal elements loaded:', {
        modal: modal,
        btnAddStaff: btnAddStaff,
        cancelBtn: cancelBtn
      });

      // Open modal for adding new staff
      if (btnAddStaff) {
        btnAddStaff.addEventListener('click', function(e) {
          e.preventDefault();
          console.log('Add Staff button clicked');
          
          modalTitle.textContent = 'Add New Staff Member';
          actionField.value = 'add';
          idField.value = '';
          staffForm.reset();
          
          // Show password field for new staff
          pwRow.style.display = 'block';
          document.getElementById('f-password').setAttribute('required', 'required');
          
          // Show modal
          modal.style.display = 'flex';
        });
      }

      // Close modal via Cancel button
      if (cancelBtn) {
        cancelBtn.addEventListener('click', function(e) {
          e.preventDefault();
          console.log('Cancel button clicked');
          modal.style.display = 'none';
          staffForm.reset();
        });
      }

      // Close modal when clicking outside
      window.addEventListener('click', function(e) {
        if (e.target === modal) {
          console.log('Clicked outside modal');
          modal.style.display = 'none';
          staffForm.reset();
        }
      });

      // Handle ESC key to close modal
      document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && modal.style.display === 'flex') {
          modal.style.display = 'none';
          staffForm.reset();
        }
      });
    });

    // Open modal for editing staff (Global function)
    function openEditModal(id, username, address, email, telephone) {
      console.log('Edit modal opened for ID:', id);
      
      const modal = document.getElementById('modal');
      const modalTitle = document.getElementById('modalTitle');
      const actionField = document.getElementById('f-action');
      const idField = document.getElementById('f-id');
      const pwRow = document.getElementById('pwRow');
      
      modalTitle.textContent = 'Edit Staff Member';
      actionField.value = 'edit';
      idField.value = id;
      
      // Fill form fields with existing data
      document.getElementById('f-username').value = username;
      document.getElementById('f-address').value = address;
      document.getElementById('f-email').value = email;
      document.getElementById('f-telephone').value = telephone;
      
      // Hide password field for edit (password not changed during edit)
      pwRow.style.display = 'none';
      document.getElementById('f-password').removeAttribute('required');
      document.getElementById('f-password').value = '';
      
      // Show modal
      modal.style.display = 'flex';
    }

    // Form validation before submit
    document.getElementById('staffForm').addEventListener('submit', function(e) {
      const action = document.getElementById('f-action').value;
      console.log('Form submitted with action:', action);
      
      // Additional validation if needed
      const telephone = document.getElementById('f-telephone').value;
      if (telephone && !/^[0-9]{10}$/.test(telephone)) {
        e.preventDefault();
        alert('Please enter a valid 10-digit phone number');
        return false;
      }
      
      // If adding new staff, ensure password is provided
      if (action === 'add') {
        const password = document.getElementById('f-password').value;
        if (!password || password.length < 6) {
          e.preventDefault();
          alert('Password must be at least 6 characters long');
          return false;
        }
      }
      
      console.log('Form validation passed, submitting...');
      return true;
    });
  </script>
  
  <!-- External admin.js if available -->
  <script src="${pageContext.request.contextPath}/js/admin.js" defer></script>
</body>
</html>