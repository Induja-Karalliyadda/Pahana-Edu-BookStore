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

        // Validate login credentials
        User user = UserService.loginUser(email, password);

        if (user == null) {
            // Redirect if login fails
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=1");
            return;
        }

        // Create session and set session attributes
        HttpSession session = req.getSession();
        session.setAttribute("user", user);

        // Sanitize cookie values by replacing spaces with an underscore
        String role = user.getRole().replace(" ", "_");
        String username = user.getUsername().replace(" ", "_");
        String emailValue = user.getEmail().replace(" ", "_");

        // Set cookies for user information (role, username, and email)
        Cookie roleCookie = new Cookie("role", role);
        Cookie usernameCookie = new Cookie("username", username);
        Cookie emailCookie = new Cookie("email", emailValue);

        // Set cookie expiration times (1 day)
        roleCookie.setMaxAge(60 * 60 * 24); // 1 day
        usernameCookie.setMaxAge(60 * 60 * 24); // 1 day
        emailCookie.setMaxAge(60 * 60 * 24); // 1 day

        // Add cookies to the response
        resp.addCookie(roleCookie);
        resp.addCookie(usernameCookie);
        resp.addCookie(emailCookie);

        // Redirect based on user role
        switch (user.getRole().toLowerCase()) {
            case "admin":
                resp.sendRedirect(req.getContextPath() + "/AdminPOSController");
                break;
            case "staff":
                resp.sendRedirect(req.getContextPath() + "/AdminPOSController");
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/view/UserDashboard.jsp");
        }
    }
}

