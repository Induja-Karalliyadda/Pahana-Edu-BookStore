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
  <title>Pahana Edu Blog</title>
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

    /* Cart + profile */
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

    /* Hero + content */
    .hero{ background:linear-gradient(135deg,#8e44ad,#9b59b6); color:#fff; padding:42px 20px; text-align:center; }
    .container{ max-width:1000px; margin:24px auto; padding:0 16px; }
    .post{
      background:#fff; border-radius:14px; padding:20px; margin-bottom:18px;
      box-shadow:0 6px 18px rgba(0,0,0,.06);
    }
    .post h2{ color:var(--purple); margin:0 0 6px; }
    .post .meta{ color:#7f8c8d; font-size:.9rem; margin-bottom:10px; }
    .btn{ display:inline-block; padding:10px 14px; border-radius:8px; text-decoration:none; background:var(--purple); color:#fff; }

    /* Responsive */
    @media (max-width: 760px){
      .nav-links{ gap:10px; flex-wrap:wrap; }
      .brand span{ display:none; } /* show just logo text on small screens */
    }
  </style>
</head>
<body>

  <!-- Top Nav -->
  <nav class="navbar">
    <!-- Brand with image -->
    <a href="${shopUrl}" class="brand">
      <!-- Put your logo file at /webapp/img/logo.png (or change the src below) -->
     
      <span>Pahana Edu BookStore</span>
    </a>

    <!-- Main links -->
    <div class="nav-links">
      <a href="${shopUrl}">Shop</a>
      <a href="${blogUrl}" class="active">Blog</a>
      <a href="${contactUrl}">Contact</a>
    </div>

    <!-- User actions -->
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
    <h1>Pahana Edu Blog</h1>
    <p>Notes on reading, study skills, and the tools that make learning stick.</p>
  </section>

  <!-- Blog posts -->
  <main class="container">
    <article class="post">
      <h2>Why Reading 20 Minutes a Day Works</h2>
      <div class="meta">Study habits • 3 min read</div>
      <p>Short, consistent reading sessions beat occasional marathons. Your brain consolidates ideas better when you return to a book frequently, even if it’s just a chapter at a time. Pick a time, set a timer, and let your next favourite story or textbook meet you there.</p>
      <p>Try keeping a simple reading log—title, pages, and one sentence about what stood out. That tiny reflection turns pages into progress.</p>
    </article>

    <article class="post">
      <h2>Choosing the Right Study Guide</h2>
      <div class="meta">Book picks • 4 min read</div>
      <p>Good study guides do three things: explain clearly, give targeted practice, and show worked answers. When comparing books, skim a sample chapter and one full exercise set. If you understand the author’s voice and the solutions feel “teachable,” you’ve found the right fit.</p>
      <p>In our store, the <em>Exam Essentials</em> series is concise, while <em>Masterclass</em> guides dive deep—great for revision vs. mastery.</p>
    </article>

    <article class="post">
      <h2>Annotate Without Ruining the Page</h2>
      <div class="meta">Reading tools • 2 min read</div>
      <p>Sticky tabs and a soft pencil beat heavy highlighters. Mark definitions with a small dot in the margin and write a 3–5 word summary at the end of each section. When you revisit, those micro-notes are a map back to the big ideas.</p>
      <a class="btn" href="${shopUrl}">Browse Books</a>
    </article>
  </main>
</body>
</html>

