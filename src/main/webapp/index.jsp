<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>Pahana Edu Book Store</title>
  <link rel="stylesheet" href="css/style.css">
  <script src="js/script.js" defer></script>
  <style>
    .login-container {
      max-width: 400px;
      margin: 80px auto;
      background: #fff;
      padding: 36px 30px 24px 30px;
      border-radius: 12px;
      box-shadow: 0 8px 32px rgba(80,40,120,0.13);
    }
    .login-title {
      color: #8e44ad;
      font-size: 2rem;
      font-weight: bold;
      text-align: center;
      margin-bottom: 28px;
      letter-spacing: 1px;
    }
    .form-group {
      margin-bottom: 18px;
    }
    .form-group label {
      display: block;
      color: #8e44ad;
      margin-bottom: 6px;
      font-weight: bold;
    }
    .form-group input {
      width: 100%;
      padding: 9px 12px;
      border-radius: 5px;
      border: 1px solid #bbb;
      font-size: 1rem;
    }
    .login-btn {
      background: #8e44ad;
      color: #fff;
      border: none;
      border-radius: 6px;
      padding: 10px 0;
      width: 100%;
      font-size: 1.12rem;
      font-weight: bold;
      cursor: pointer;
      transition: background 0.2s;
    }
    .login-btn:hover {
      background: #6d2e98;
    }
    .signup-link {
      display: block;
      text-align: center;
      margin-top: 20px;
      color: #8e44ad;
      text-decoration: none;
      font-size: 1rem;
      font-weight: bold;
    }
    .signup-link:hover {
      text-decoration: underline;
    }
    .login-error {
      color: #e74c3c;
      text-align: center;
      margin-bottom: 12px;
      font-size: 1rem;
    }
  </style>
</head>
<body>
<br>

  <div class="login-container">
    <div class="login-title">Pahana Edu Book Store</div>
    <%-- Display error if login failed --%>
    <%
      String error = request.getParameter("error");
      if ("1".equals(error)) {
    %>
      <div class="login-error">Invalid username or password.</div>
    <%
      }
    %>
  <form action="LoginServlet" method="post">
  <div class="form-group">
    <label for="email">UserName</label>
    <input type="email" id="email" name="email" required autofocus>
  </div>
  <div class="form-group">
    <label for="password">Password</label>
    <input type="password" id="password" name="password" required>
  </div>
  <button type="submit" class="login-btn">Login</button>
</form>
  
    
    <a href="view/signup.jsp" class="signup-link">Don't have an account? Sign up here</a>
  </div>
</body>
</html>
