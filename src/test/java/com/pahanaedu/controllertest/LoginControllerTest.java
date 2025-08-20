package com.pahanaedu.controllertest;

import com.pahanaedu.controller.LoginController;
import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

  /** Expose protected doPost for unit testing. */
  static class TestableLoginController extends LoginController {
    @Override public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws javax.servlet.ServletException, java.io.IOException {
      super.doPost(req, resp);
    }
  }

  private final TestableLoginController controller = new TestableLoginController();
  private final HttpServletRequest request = mock(HttpServletRequest.class);
  private final HttpServletResponse response = mock(HttpServletResponse.class);
  private final HttpSession session = mock(HttpSession.class);

  // ---------- FAILED LOGIN ----------

  @Test
  void doPost_loginFailure_redirectsToIndexWithError() throws Exception {
    when(request.getParameter("email")).thenReturn("a@x.com");
    when(request.getParameter("password")).thenReturn("wrong");
    when(request.getContextPath()).thenReturn("/app");

    try (MockedStatic<UserService> us = mockStatic(UserService.class)) {
      us.when(() -> UserService.loginUser("a@x.com", "wrong")).thenReturn(null);

      controller.doPost(request, response);

      verify(response).sendRedirect("/app/index.jsp?error=1");
      // No cookies should be set on failure
      verify(response, never()).addCookie(any());
      // getSession should not be called
      verify(request, never()).getSession();
    }
  }

  // ---------- SUCCESS: ADMIN ----------

  @Test
  void doPost_loginAdmin_setsSession_addsCookies_redirectsToAdmin() throws Exception {
    // Inputs
    when(request.getParameter("email")).thenReturn("admin@x.com");
    when(request.getParameter("password")).thenReturn("secret");
    when(request.getContextPath()).thenReturn("/app");
    when(request.getSession()).thenReturn(session);

    // Returned user: include spaces in username to exercise sanitizing
    User admin = new User();
    admin.setId(1);
    admin.setUsername("Alice Admin");  // will become "Alice_Admin" in cookie
    admin.setEmail("admin@x.com");
    admin.setRole("admin");

    try (MockedStatic<UserService> us = mockStatic(UserService.class)) {
      us.when(() -> UserService.loginUser("admin@x.com", "secret")).thenReturn(admin);

      controller.doPost(request, response);

      // Session
      verify(session).setAttribute("user", admin);

      // Cookies (capture three)
      ArgumentCaptor<Cookie> cookieCap = ArgumentCaptor.forClass(Cookie.class);
      verify(response, times(3)).addCookie(cookieCap.capture());

      assertEquals(3, cookieCap.getAllValues().size());

      // Assert cookie names/values (spaces sanitized)
      boolean roleOk = cookieCap.getAllValues().stream()
          .anyMatch(c -> c.getName().equals("role") && "admin".equals(c.getValue()));
      boolean userOk = cookieCap.getAllValues().stream()
          .anyMatch(c -> c.getName().equals("username") && "Alice_Admin".equals(c.getValue()));
      boolean emailOk = cookieCap.getAllValues().stream()
          .anyMatch(c -> c.getName().equals("email") && "admin@x.com".equals(c.getValue()));

      assertTrue(roleOk, "role cookie missing/incorrect");
      assertTrue(userOk, "username cookie missing/incorrect (sanitizing failed?)");
      assertTrue(emailOk, "email cookie missing/incorrect");

      // Redirect to admin area
      verify(response).sendRedirect("/app/AdminPOSController");
    }
  }

  // ---------- SUCCESS: USER (default branch) ----------

  @Test
  void doPost_loginUser_redirectsToUserDashboard() throws Exception {
    when(request.getParameter("email")).thenReturn("u@x.com");
    when(request.getParameter("password")).thenReturn("pw");
    when(request.getContextPath()).thenReturn("/app");
    when(request.getSession()).thenReturn(session);

    User user = new User();
    user.setId(5);
    user.setUsername("Jane Doe");
    user.setEmail("u@x.com");
    user.setRole("user");

    try (MockedStatic<UserService> us = mockStatic(UserService.class)) {
      us.when(() -> UserService.loginUser("u@x.com", "pw")).thenReturn(user);

      controller.doPost(request, response);

      // At least verify redirect path for default branch
      verify(response).sendRedirect("/app/view/UserDashboard.jsp");
    }
  }
}
