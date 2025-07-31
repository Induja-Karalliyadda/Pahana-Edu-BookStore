package com.pahanaedu.controller;

import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/signup")
public class SignUpController extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher("/view/signup.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");

    User u = new User(
      req.getParameter("username"),
      req.getParameter("address"),
      req.getParameter("telephone"),
      req.getParameter("email"),
      req.getParameter("password"),
      "user"
    );

    boolean ok = UserService.registerUser(u);
    if (!ok) {
      resp.sendRedirect(req.getContextPath() + "/signup?error=exists");
    } else {
      resp.sendRedirect(req.getContextPath() + "/signup?signup=success");
    }
  }
}


