package com.pahanaedu.controller;

import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/users", "/users/"})
public class UserController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String keyword = request.getParameter("kw");
            List<User> userList;

            // Debugging information
            System.out.println("=== UserServlet doGet called ===");
            System.out.println("Request URI: " + request.getRequestURI());
            System.out.println("Search keyword: '" + keyword + "'");

            if (keyword != null && !keyword.trim().isEmpty()) {
                // Search for users if the keyword is provided
                userList = UserService.searchUsers(keyword.trim());
                System.out.println("Search performed with keyword: '" + keyword + "'");
            } else {
                // Load all users if no keyword is provided
                userList = UserService.getAllUsers();
                System.out.println("Loading all users (no search keyword)");
            }

            // Debug: Check if userList is null or empty
            if (userList == null) {
                System.out.println("ERROR: userList is null!");
                userList = new java.util.ArrayList<>();
            } else {
                System.out.println("Users loaded: " + userList.size());
                for (User u : userList) {
                    System.out.println("User: " + u.getUsername() + " (" + u.getCustomerCode() + ") - Role: " + u.getRole());
                }
            }

            request.setAttribute("userList", userList);
            // Forward to the users page
            request.getRequestDispatcher("/view/Users.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Exception in UserServlet doGet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            System.out.println("=== UserServlet doPost called ===");
            System.out.println("Action: " + action);

            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean success = UserService.deleteUser(id);
                System.out.println("Delete user " + id + ": " + (success ? "SUCCESS" : "FAILED"));
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String username = request.getParameter("username");
                String address = request.getParameter("address");
                String email = request.getParameter("email");
                String tel = request.getParameter("telephone");

                User u = new User();
                u.setId(id);
                u.setUsername(username);
                u.setAddress(address);
                u.setEmail(email);
                u.setTelephone(tel);

                boolean success = UserService.updateUser(u);
                System.out.println("Update user " + id + ": " + (success ? "SUCCESS" : "FAILED"));
            }

            // Redirect to the users page after action
            response.sendRedirect(request.getContextPath() + "/users");

        } catch (Exception e) {
            System.err.println("Exception in UserServlet doPost: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}

