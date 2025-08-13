package com.pahanaedu.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Invalidate the session
        HttpSession session = req.getSession();
        session.invalidate(); // Invalidates the current session

        // Clear cookies by setting their max age to 0
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // Clear the cookies by setting the max age to 0
                cookie.setMaxAge(0);
                cookie.setPath("/"); // Ensure the cookie is removed across the entire app
                resp.addCookie(cookie); // Send the cookie with maxAge=0 to delete it
            }
        }

        // Redirect the user to the login page
        resp.sendRedirect(req.getContextPath() + "/index.jsp"); // Redirect to the login page
    }
}
