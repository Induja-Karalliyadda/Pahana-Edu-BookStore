package com.pahanaedu.controller;

import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = UserService.loginUser(email, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            String role = user.getRole();
            if ("admin".equalsIgnoreCase(role)) {
                response.sendRedirect("view/AdminDashboard.jsp");
            } else if ("staff".equalsIgnoreCase(role)) {
                response.sendRedirect("view/StaffDashboard.jsp");
            } else {
                response.sendRedirect("view/UserDashboard.jsp");
            }
        } else {
            response.sendRedirect("index.jsp?error=1");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
}

