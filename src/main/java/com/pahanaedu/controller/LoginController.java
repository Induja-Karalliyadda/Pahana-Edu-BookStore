package com.pahanaedu.controller;

import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        User user = UserService.loginUser(email, password);

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=1");
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("user", user);

        switch (user.getRole().toLowerCase()) {
            case "admin":
                resp.sendRedirect(req.getContextPath() + "/AdminPOSController");
                break;
            case "staff":
                resp.sendRedirect(req.getContextPath() + "/view/StaffDashboard.jsp");
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/view/UserDashboard.jsp");
        }
    }
}

