document.addEventListener('DOMContentLoaded', function() {
  var btn = document.getElementById('login-signup-btn');
  if (btn) {
    btn.addEventListener('click', function() {
      // First load the login page (as per your requirement)
      window.location.href = 'view/login.jsp';
    });
  }
});
