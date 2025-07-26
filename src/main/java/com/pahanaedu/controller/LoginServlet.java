package com.pahanaedu.controller;

import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");

    String idOrEmail = req.getParameter("email");
    String pwd       = req.getParameter("password");
    User u = UserService.loginUser(idOrEmail, pwd);

    if (u == null) {
      resp.sendRedirect(req.getContextPath() + "/index.jsp?error=1");
      return;
    }

    HttpSession session = req.getSession();
    session.setAttribute("user", u);

    String r = u.getRole().toLowerCase();
    if (r.equals("admin")) {
      resp.sendRedirect(req.getContextPath() + "/view/AdminDashboard.jsp");
    } else if (r.equals("staff")) {
      resp.sendRedirect(req.getContextPath() + "/view/StaffDashboard.jsp");
    } else {
      resp.sendRedirect(req.getContextPath() + "/view/UserDashboard.jsp");
    }
  }
}


