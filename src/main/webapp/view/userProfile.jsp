<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url var="shopUrl"     value="/UserDashboardController"/>
<c:url var="blogUrl"     value="/view/blog.jsp"/>
<c:url var="contactUrl"  value="/view/contact.jsp"/>
<c:url var="cartUrl"     value="/view/cart.jsp"/>
<c:url var="logoutUrl"   value="/logout"/>
<c:url var="profileAction" value="/UserProfileController"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>My Profile | Pahana Edu BookStore</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
  <style>
    :root{ --purple:#8e44ad; --cloud:#ecf0f1; }
    body{ margin:0; font-family:Arial, sans-serif; background:var(--cloud); color:#2c3e50; }
    .navbar{ background:var(--purple); padding:14px 22px; display:flex; align-items:center; justify-content:space-between; position:sticky; top:0; z-index:1000; box-shadow:0 2px 10px rgba(0,0,0,.12); }
    .brand{ text-decoration:none; color:#fff; font-weight:700; font-size:1.15rem; }
    .nav-links{ display:flex; align-items:center; gap:16px; }
    .nav-links a{ color:#fff; text-decoration:none; padding:6px 10px; border-radius:8px; }
    .nav-links a:hover{ background:rgba(255,255,255,.12); }
    .nav-links a.active{ background:rgba(255,255,255,.22); }
    .cart-link{ position:relative; }
    .cart-badge{ position:absolute; top:-6px; right:-10px; background:#e74c3c; color:#fff; font-size:.72rem; line-height:1; padding:4px 6px; border-radius:999px; min-width:18px; text-align:center; }
    .logout-btn{ background:#e74c3c; color:#fff; border-radius:8px; padding:6px 12px; text-decoration:none; }
    .hero{ background:linear-gradient(135deg,#8e44ad,#9b59b6); color:#fff; padding:42px 20px; text-align:center; }
    .container{ max-width:1000px; margin:24px auto; padding:0 16px; }
    .card{ background:#fff; border-radius:14px; padding:20px; margin-bottom:18px; box-shadow:0 6px 18px rgba(0,0,0,.06); }
    .card h2{ color:var(--purple); margin:0 0 10px; }
    .form-grid{ display:grid; grid-template-columns:1fr 1fr; gap:14px; }
    .form-grid .full{ grid-column:1 / -1; }
    label{ display:block; margin-bottom:6px; color:#7f8c8d; }
    input{ width:100%; padding:10px 12px; border:1px solid #ddd; border-radius:8px; font-size:14px; background:#fff; }
    .btn{ display:inline-block; padding:10px 16px; border-radius:8px; text-decoration:none; background:var(--purple); color:#fff; border:none; cursor:pointer; }
    .alert{ padding:10px 12px; border-radius:8px; margin-bottom:12px; }
    .alert.ok{ background:#eafaf1; color:#1e824c; border:1px solid #c6f1da; }
    .alert.err{ background:#fdecea; color:#c0392b; border:1px solid #f5c6cb; }
    @media (max-width:760px){ .form-grid{ grid-template-columns:1fr; } }
  </style>
</head>
<body>

  <!-- Navbar -->
  <nav class="navbar">
    <a href="${shopUrl}" class="brand">Pahana Edu BookStore</a>
    <div class="nav-links">
      <a href="${shopUrl}">Shop</a>
      <a href="${blogUrl}">Blog</a>
      <a href="${contactUrl}">Contact</a>
    </div>
    <div class="nav-links">
      <a href="${cartUrl}" class="cart-link" title="Cart">&#128722;
        <c:if test="${sessionScope.cartItemCount gt 0}">
          <span class="cart-badge">${sessionScope.cartItemCount}</span>
        </c:if>
      </a>
      <a class="active" href="${pageContext.request.contextPath}/UserProfileController" title="Profile">&#128100;</a>
      <a href="${logoutUrl}" class="logout-btn">Log out</a>
    </div>
  </nav>

  <section class="hero">
    <h1>My Profile</h1>
    <p>Manage your account details and password.</p>
  </section>

  <main class="container">
    <c:if test="${not empty msg}">
      <div class="alert ok">${msg}</div>
    </c:if>
    <c:if test="${not empty err}">
      <div class="alert err">${err}</div>
    </c:if>

    <c:choose>
      <c:when test="${empty sessionScope.user}">
        <div class="card"><p>You’re not signed in. <a href="${pageContext.request.contextPath}/login.jsp">Login</a></p></div>
      </c:when>
      <c:otherwise>

        <!-- Profile details -->
        <section class="card">
          <h2>Account details</h2>
          <form action="${profileAction}" method="post" autocomplete="off">
            <input type="hidden" name="action" value="updateProfile">
            <div class="form-grid">
              <div>
                <label for="username">Username</label>
                <input id="username" name="username" value="${sessionScope.user.username}" required>
              </div>
              <div>
                <label for="email">Email</label>
                <input id="email" name="email" type="email" value="${sessionScope.user.email}" required>
              </div>
              <div class="full">
                <label for="address">Address</label>
                <input id="address" name="address" value="${sessionScope.user.address}">
              </div>
              <div class="full">
                <label for="telephone">Telephone</label>
                <input id="telephone" name="telephone" value="${sessionScope.user.telephone}"
                       pattern="[0-9+\-\s]{7,20}" title="7–20 digits, +, - or spaces">
              </div>
            </div>
            <div style="margin-top:12px;">
              <button class="btn" type="submit">Save changes</button>
            </div>
          </form>
        </section>

        <!-- Password -->
        <section class="card">
          <h2>Change password</h2>
          <form action="${profileAction}" method="post" autocomplete="off">
            <input type="hidden" name="action" value="changePassword">
            <div class="form-grid">
              <div>
                <label for="currentPassword">Current password</label>
                <input id="currentPassword" name="currentPassword" type="password" required>
              </div>
              <div>
                <label for="newPassword">New password</label>
                <input id="newPassword" name="newPassword" type="password" minlength="6" required>
              </div>
              <div class="full">
                <label for="confirmPassword">Confirm new password</label>
                <input id="confirmPassword" name="confirmPassword" type="password" minlength="6" required>
              </div>
            </div>
            <div style="margin-top:12px;">
              <button class="btn" type="submit">Update password</button>
            </div>
          </form>
        </section>

      </c:otherwise>
    </c:choose>
  </main>
</body>
</html>
