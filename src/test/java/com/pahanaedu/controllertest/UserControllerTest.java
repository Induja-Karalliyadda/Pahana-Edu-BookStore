package com.pahanaedu.controllertest;

import com.pahanaedu.controller.UserController;
import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  /** Make doGet/doPost callable from tests (they’re protected on HttpServlet). */
  static class TestableUserController extends UserController {
    @Override public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws javax.servlet.ServletException, java.io.IOException { super.doGet(req, res); }
    @Override public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws javax.servlet.ServletException, java.io.IOException { super.doPost(req, res); }
  }

  // Mocks shared by tests
  private final HttpServletRequest request  = mock(HttpServletRequest.class);
  private final HttpServletResponse response = mock(HttpServletResponse.class);
  private final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
  private final TestableUserController controller = new TestableUserController();

  // ---------- doGet: no keyword -> calls getAllUsers() and forwards ----------

  @Test
  void doGet_withoutKeyword_loadsAllUsers_and_forwards() throws Exception {
    when(request.getParameter("kw")).thenReturn(null);
    when(request.getRequestDispatcher("/view/Users.jsp")).thenReturn(dispatcher);
    when(request.getRequestURI()).thenReturn("/app/users");

    List<User> list = List.of(user(1, "c001", "alice"), user(2, "c002", "bob"));

    try (MockedStatic<UserService> us = mockStatic(UserService.class)) {
      us.when(UserService::getAllUsers).thenReturn(list);

      controller.doGet(request, response);

      us.verify(UserService::getAllUsers);
      verify(request).setAttribute("userList", list);
      verify(dispatcher).forward(request, response);
      verify(response, never()).sendRedirect(anyString());
    }
  }

  // ---------- doGet: with keyword -> calls searchUsers() and forwards ----------

  @Test
  void doGet_withKeyword_searches_and_forwards() throws Exception {
    when(request.getParameter("kw")).thenReturn("ali");
    when(request.getRequestDispatcher("/view/Users.jsp")).thenReturn(dispatcher);
    when(request.getRequestURI()).thenReturn("/app/users");

    List<User> list = List.of(user(1, "c001", "alice"));

    try (MockedStatic<UserService> us = mockStatic(UserService.class)) {
      us.when(() -> UserService.searchUsers("ali")).thenReturn(list);

      controller.doGet(request, response);

      us.verify(() -> UserService.searchUsers("ali"));
      verify(request).setAttribute("userList", list);
      verify(dispatcher).forward(request, response);
      verify(response, never()).sendRedirect(anyString());
    }
  }

  // ---------- doPost: delete -> calls deleteUser() and redirects ----------

  @Test
  void doPost_delete_redirectsToUsers() throws Exception {
    when(request.getParameter("action")).thenReturn("delete");
    when(request.getParameter("id")).thenReturn("7");
    when(request.getContextPath()).thenReturn("/app");

    try (MockedStatic<UserService> us = mockStatic(UserService.class)) {
      us.when(() -> UserService.deleteUser(7)).thenReturn(true);

      controller.doPost(request, response);

      us.verify(() -> UserService.deleteUser(7));
      verify(response).sendRedirect("/app/users");
    }
  }

  // ---------- doPost: update -> calls updateUser() with values and redirects ----------

  @Test
  void doPost_update_sendsUserAnd_redirects() throws Exception {
    when(request.getParameter("action")).thenReturn("update");
    when(request.getParameter("id")).thenReturn("5");
    when(request.getParameter("username")).thenReturn("alice");
    when(request.getParameter("address")).thenReturn("1 Main St");
    when(request.getParameter("email")).thenReturn("a@x.com");
    when(request.getParameter("telephone")).thenReturn("0771234567");
    when(request.getContextPath()).thenReturn("/app");

    try (MockedStatic<UserService> us = mockStatic(UserService.class)) {
      us.when(() -> UserService.updateUser(any(User.class))).thenReturn(true);

      controller.doPost(request, response);

      us.verify(() -> UserService.updateUser(argThat(u ->
           u.getId() == 5
        && "alice".equals(u.getUsername())
        && "1 Main St".equals(u.getAddress())
        && "a@x.com".equals(u.getEmail())
        && "0771234567".equals(u.getTelephone())
      )));

      verify(response).sendRedirect("/app/users");
    }
  }

  // ---------- INTENTIONAL FAIL: expect redirect on GET (but controller forwards) ----------

  @Test
  void doGet_withoutKeyword_intentionalFailure_expectRedirectButItForwards() throws Exception {
    // This test is purposely written to FAIL to show a red bar in JUnit.
    when(request.getParameter("kw")).thenReturn(null);
    when(request.getRequestDispatcher("/view/Users.jsp")).thenReturn(dispatcher);
    when(request.getRequestURI()).thenReturn("/app/users");

    try (MockedStatic<UserService> us = mockStatic(UserService.class)) {
      us.when(UserService::getAllUsers).thenReturn(List.of());

      controller.doGet(request, response);

      // The servlet actually forwards; this verify will fail.
      verify(response).sendRedirect(anyString());
    }
  }

  // ---------- helpers ----------

  private static User user(int id, String code, String username) {
    User u = new User();
    u.setId(id);
    u.setCustomerCode(code);
    u.setUsername(username);
    u.setRole("user");
    return u;
  }
}

