package com.pahanaedu.controller;

import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/SignUpServlet")
public class SignUpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        // Get form parameters
        String username = request.getParameter("username");
        String address = request.getParameter("address");
        String telephone = request.getParameter("telephone");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = "user"; // default role
        
        try {
            // Create user object
            User user = new User(username, address, telephone, email, password, role);
            
            // Register user
            boolean isRegistered = UserService.registerUser(user);
            
            if (isRegistered) {
                // Success - redirect to login page with success message
                response.sendRedirect("index.jsp?signup=success");
            } else {
                // Failed - redirect back to signup with error
                response.sendRedirect("view/signup.jsp?error=registration_failed");
            }
            
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            response.sendRedirect("view/signup.jsp?error=server_error");
        }
    }
    
    // Handle GET requests (redirect to signup page)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("view/signup.jsp");
    }
}

