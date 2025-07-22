<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>Sign Up - Pahana Edu Book Store</title>
  <link rel="stylesheet" href="../css/style.css">
  <style>
    body {
      background: #ecf0f1;
    }
    .signup-container {
      max-width: 420px;
      margin: 70px auto;
      background: #fff;
      border-radius: 12px;
      box-shadow: 0 6px 28px rgba(80,40,120,0.11);
      padding: 32px 28px 22px 28px;
    }
    .signup-title {
      color: #8e44ad;
      font-size: 2rem;
      font-weight: bold;
      text-align: center;
      margin-bottom: 26px;
      letter-spacing: 1px;
    }
    .form-group {
      margin-bottom: 17px;
    }
    .form-group label {
      display: block;
      color: #8e44ad;
      margin-bottom: 5px;
      font-weight: bold;
    }
    .form-group input {
      width: 100%;
      padding: 8px 11px;
      border-radius: 5px;
      border: 1px solid #bbb;
      font-size: 1rem;
      background: #ecf0f1;
    }
    .signup-btn {
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
      margin-top: 12px;
    }
    .signup-btn:hover {
      background: #6d2e98;
    }
    .login-link {
      display: block;
      text-align: center;
      margin-top: 18px;
      color: #8e44ad;
      text-decoration: none;
      font-size: 1rem;
      font-weight: bold;
    }
    .login-link:hover {
      text-decoration: underline;
    }
  </style>
</head>
<body>
<br>

  <div class="signup-container">
    <div class="signup-title">Pahana Edu Book Store</div>
    <form action="../SignUpServlet" method="post">
      <div class="form-group">
        <label for="username">Username<span style="color:#e74c3c">*</span></label>
        <input type="text" id="username" name="username" required autocomplete="off">
      </div>
      <div class="form-group">
        <label for="address">Address<span style="color:#e74c3c">*</span></label>
        <input type="text" id="address" name="address" required autocomplete="off">
      </div>
      <div class="form-group">
        <label for="telephone">Telephone<span style="color:#e74c3c">*</span></label>
        <input type="text" id="telephone" name="telephone" required autocomplete="off">
      </div>
      <div class="form-group">
        <label for="email">Email<span style="color:#e74c3c">*</span></label>
        <input type="email" id="email" name="email" required autocomplete="off">
      </div>
      <div class="form-group">
        <label for="password">Password<span style="color:#e74c3c">*</span></label>
        <input type="password" id="password" name="password" required autocomplete="off">
      </div>
      <button type="submit" class="signup-btn">Sign Up</button>
    </form>
    <a href="../index.jsp" class="login-link">Already have an account? Log in here</a>
  </div>
  <script src="../js/script.js"></script>
</body>
</html>

</html>