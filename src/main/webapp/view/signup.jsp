<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Pahana Edu Book Store – Sign Up</title>
  <style>
    body {
      background: #f3f5f7;
      min-height: 100vh;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .signup-container {
      background: #fff;
      border-radius: 20px;
      box-shadow: 0 8px 32px rgba(60,30,100,0.11);
      max-width: 440px;
      width: 98%;
      padding: 36px 30px 32px 30px;
      margin: 50px 0;
    }
    .header {
      text-align: center;
      margin-bottom: 25px;
    }
    .header h1 {
      font-size: 44px;
      color: #8e44ad;
      font-weight: bold;
      letter-spacing: 1px;
      margin-bottom: 5px;
      line-height: 1.1;
    }
    .header p {
      color: #8a8a8a;
      font-size: 18px;
      margin-bottom: 0;
    }
    .form-group {
      margin-bottom: 20px;
    }
    .form-group label {
      color: #8e44ad;
      font-size: 20px;
      font-weight: bold;
      margin-bottom: 7px;
      display: block;
      letter-spacing: 0.3px;
    }
    .required { color: #e74c3c; font-weight: bold; }
    .form-group input {
      border: 2px solid #bbb;
      border-radius: 11px;
      padding: 13px 14px;
      font-size: 20px;
      width: 100%;
      background: #fff;
      transition: border 0.25s;
      margin-top: 4px;
      box-sizing: border-box;
      line-height: 1.4;
    }
    .form-group input:focus {
      border-color: #8e44ad;
      outline: none;
    }
    .signup-btn {
      width: 100%;
      background: #8e44ad;
      color: #fff;
      border: none;
      padding: 17px 0;
      border-radius: 10px;
      font-size: 26px;
      font-weight: bold;
      cursor: pointer;
      margin-top: 10px;
      transition: background 0.2s, box-shadow 0.2s;
      box-shadow: 0 3px 10px rgba(142,68,173,0.06);
    }
    .signup-btn:hover {
      background: #733e9a;
    }
    .login-link {
      display: block;
      text-align: center;
      margin-top: 24px;
      color: #8e44ad;
      font-size: 20px;
      font-weight: 600;
      text-decoration: none;
      letter-spacing: 0.2px;
      transition: color 0.2s;
    }
    .login-link:hover {
      color: #6d2e98;
      text-decoration: underline;
    }
    .alert {
      padding: 11px 15px;
      border-radius: 9px;
      margin-bottom: 15px;
      font-size: 16px;
      font-weight: 500;
    }
    .alert-success {
      background: #d4edda;
      color: #155724;
      border: 1px solid #c3e6cb;
    }
    .alert-error {
      background: #f8d7da;
      color: #721c24;
      border: 1px solid #f5c6cb;
    }
    @media (max-width: 600px) {
      .signup-container {
        max-width: 98vw;
        padding: 18px 4vw 12px 4vw;
        border-radius: 12px;
      }
      .header h1 { font-size: 29px; }
      .form-group label, .login-link { font-size: 16px; }
      .form-group input, .signup-btn { font-size: 16px; }
    }
  </style>
</head>
<body>
  <div class="signup-container">
    <div class="header">
      <h1>Pahana Edu Book<br/>Store</h1>
      <p>Join our community of book lovers</p>
    </div>

    <!-- Alert messages from server -->
    <c:choose>
      <c:when test="${param.error == 'server_error'}">
        <div class="alert alert-error">
          A server error occurred. Please try again later.
        </div>
      </c:when>
      <c:when test="${param.error == 'exists'}">
        <div class="alert alert-error">
          That email is already registered. Try another.
        </div>
      </c:when>
      <c:when test="${param.signup == 'success'}">
        <div class="alert alert-success">
          Account created successfully! 
          <a href="${pageContext.request.contextPath}/index.jsp">Log in here</a>.
        </div>
      </c:when>
    </c:choose>

    <!-- *** POSTS to /signup, not /SignUpServlet *** -->
    <form id="signupForm"
          action="${pageContext.request.contextPath}/signup"
          method="post"
          autocomplete="off">

      <div class="form-group">
        <label for="username">Username <span class="required">*</span></label>
        <input type="text" id="username" name="username"
               required minlength="3" placeholder="Enter your username">
      </div>

      <div class="form-group">
        <label for="address">Address <span class="required">*</span></label>
        <input type="text" id="address" name="address"
               required placeholder="Enter your address">
      </div>

      <div class="form-group">
        <label for="telephone">Telephone <span class="required">*</span></label>
        <input type="tel" id="telephone" name="telephone"
               required placeholder="Enter your telephone">
      </div>

      <div class="form-group">
        <label for="email">Email <span class="required">*</span></label>
        <input type="email" id="email" name="email"
               required placeholder="Enter your email">
      </div>

      <div class="form-group">
        <label for="password">Password <span class="required">*</span></label>
        <input type="password" id="password" name="password"
               required minlength="6" placeholder="Create a password">
      </div>

      <button type="submit" class="signup-btn">Create Account</button>
    </form>

    <a href="${pageContext.request.contextPath}/index.jsp"
       class="login-link">
      Already have an account? Sign in here
    </a>
  </div>

  <script>
    // subtle input‑pop effect
    document.querySelectorAll('.form-group input').forEach(input => {
      input.addEventListener('focus', ()=> {
        input.parentElement.style.transform = 'scale(1.01)';
      });
      input.addEventListener('blur', ()=> {
        input.parentElement.style.transform = 'scale(1)';
      });
    });
  </script>
</body>
</html>

