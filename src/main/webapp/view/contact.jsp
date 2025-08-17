<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url var="shopUrl"     value="/UserDashboardController"/>
<c:url var="blogUrl"     value="/view/blog.jsp"/>
<c:url var="contactUrl"  value="/view/contact.jsp"/>
<c:url var="cartUrl"     value="/view/cart.jsp"/>
<c:url var="profileUrl"  value="/userProfile.jsp"/>
<c:url var="logoutUrl"   value="/logout"/>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Contact | Pahana Edu BookStore</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
  <style>
    :root{ --purple:#8e44ad; --cloud:#ecf0f1; }
    /* Base */
    body{ margin:0; font-family:Arial, sans-serif; background:var(--cloud); color:#2c3e50; }

    /* Navbar */
    .navbar{
      background:var(--purple);
      padding:14px 22px;
      display:flex; align-items:center; justify-content:space-between;
      position:sticky; top:0; z-index:1000;
      box-shadow:0 2px 10px rgba(0,0,0,.12);
    }
    .brand{ display:flex; align-items:center; gap:10px; text-decoration:none; color:#fff; }
    .brand img{ height:34px; width:auto; display:block; }
    .brand span{ font-weight:700; font-size:1.15rem; white-space:nowrap; }

    .nav-links{ display:flex; align-items:center; gap:16px; }
    .nav-links a{
      color:#fff; text-decoration:none; padding:6px 10px; border-radius:8px;
      transition:background .15s ease, opacity .15s ease; white-space:nowrap;
    }
    .nav-links a:hover{ background:rgba(255,255,255,.12); }
    .nav-links a.active{ background:rgba(255,255,255,.22); }

    .cart-link{ position:relative; }
    .cart-badge{
      position:absolute; top:-6px; right:-10px;
      background:#e74c3c; color:#fff; font-size:.72rem; line-height:1;
      padding:4px 6px; border-radius:999px; min-width:18px; text-align:center;
    }
    .logout-btn{
      background:#e74c3c; color:#fff; border-radius:8px; padding:6px 12px; text-decoration:none;
    }
    .logout-btn:hover{ opacity:.9; }

    /* Hero */
    .hero{ background:linear-gradient(135deg,#8e44ad,#9b59b6); color:#fff; padding:42px 20px; text-align:center; }

    /* Content */
    .container{ max-width:1000px; margin:24px auto; padding:0 16px; }
    .card{
      background:#fff; border-radius:14px; padding:20px; margin-bottom:18px;
      box-shadow:0 6px 18px rgba(0,0,0,.06);
    }
    .card h2{ color:var(--purple); margin:0 0 10px; }

    /* Form */
    .form-grid{ display:grid; grid-template-columns:1fr 1fr; gap:14px; }
    .form-grid .full{ grid-column:1 / -1; }
    label{ display:block; margin-bottom:6px; color:#7f8c8d; }
    input, textarea{
      width:100%; padding:10px 12px; border:1px solid #ddd; border-radius:8px; font-size:14px;
      background:#fff;
    }
    textarea{ min-height:120px; resize:vertical; }
    .btn{
      display:inline-block; padding:10px 16px; border-radius:8px; text-decoration:none;
      background:var(--purple); color:#fff; border:none; cursor:pointer;
    }
    .btn:disabled{ opacity:.7; cursor:not-allowed; }

    /* Info list */
    .info-list{ list-style:none; padding-left:0; margin:0; }
    .info-list li{ margin:6px 0; }

    @media (max-width: 760px){
      .form-grid{ grid-template-columns:1fr; }
      .nav-links{ gap:10px; flex-wrap:wrap; }
      .brand span{ display:none; }
    }
  </style>
</head>
<body>

  <!-- Navbar -->
  <nav class="navbar">
    <a href="${shopUrl}" class="brand">
      <!-- Place your logo at /webapp/img/logo.png or adjust this path -->
   
      <span>Pahana Edu BookStore</span>
    </a>

    <div class="nav-links">
      <a href="${shopUrl}">Shop</a>
      <a href="${blogUrl}">Blog</a>
      <a href="${contactUrl}" class="active">Contact</a>
    </div>

    <div class="nav-links">
      <a href="${cartUrl}" class="cart-link" title="Cart">&#128722;
        <c:if test="${sessionScope.cartItemCount gt 0}">
          <span class="cart-badge">${sessionScope.cartItemCount}</span>
        </c:if>
      </a>
      <a href="${profileUrl}" title="Profile">&#128100;</a>
      <a href="${logoutUrl}" class="logout-btn">Log out</a>
    </div>
  </nav>

  <!-- Hero -->
  <section class="hero">
    <h1>Contact Us</h1>
    <p>Questions about books, orders, or study resources? We’re here to help.</p>
  </section>

  <!-- Content -->
  <main class="container">
    <section class="card">
      <h2>Send a message</h2>
      <form action="${pageContext.request.contextPath}/ContactMessageController" method="post">
        <div class="form-grid">
          <div>
            <label for="name">Your Name</label>
            <input id="name" name="name" type="text" value="${sessionScope.user.username}" required>
          </div>
          <div>
            <label for="email">Email</label>
            <input id="email" name="email" type="email" value="${sessionScope.user.email}" required>
          </div>
          <div class="full">
            <label for="subject">Subject</label>
            <input id="subject" name="subject" type="text" placeholder="Eg. Order inquiry">
          </div>
          <div class="full">
            <label for="message">Message</label>
            <textarea id="message" name="message" placeholder="Type your message..." required></textarea>
          </div>
        </div>
        <div style="margin-top:12px;">
          <button class="btn" type="submit">Send</button>
        </div>
      </form>
    </section>

    <section class="card">
      <h2>Store Info</h2>
      <ul class="info-list">
        <li><strong>Address:</strong> No. 02, Main Street, Colombo City</li>
        <li><strong>Phone:</strong>011-2030400</li>
        <li><strong>Email:</strong> info@pahanaedu.lk</li>
        <li><strong>Hours:</strong> Mon–Fri, 9:00–17:00</li>
      </ul>
    </section>
  </main>

</body>
</html>

    