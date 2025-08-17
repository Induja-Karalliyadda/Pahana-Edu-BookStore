package com.pahanaedu.controller;

import com.pahanaedu.service.StaffService;
import com.pahanaedu.model.Staff;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/staff")
public class StaffController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if the user is logged in and has an admin role
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        // If role is null (session doesn't exist), check the cookies for role
        if (role == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("role".equals(cookie.getName())) {
                        role = cookie.getValue();
                        // If found in cookie, also set it in session for consistency
                        if (session != null) {
                            session.setAttribute("role", role);
                        }
                        break;
                    }
                }
            }
        }

        // If the role is not 'admin', redirect accordingly
        if (role == null || !role.equals("admin")) {
            if ("staff".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/view/StaffDashboard.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
            return;
        }

        try {
            // Fetch all staff data and forward to Staff.jsp
            List<Staff> staffList = StaffService.getAll();
            request.setAttribute("staffList", staffList);

            // Forward to Staff.jsp
            request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading staff: " + e.getMessage());
            request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Authorization check for POST requests
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        // Check cookies if session role is null
        if (role == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("role".equals(cookie.getName())) {
                        role = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // Deny access if not admin
        if (role == null || !role.equals("admin")) {
            response.sendRedirect(request.getContextPath() + "/view/StaffDashboard.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                // Add new staff
                Staff s = new Staff();
                s.setUsername(request.getParameter("username"));
                s.setAddress(request.getParameter("address"));
                s.setEmail(request.getParameter("email"));
                s.setTelephone(request.getParameter("telephone"));
                s.setPassword(request.getParameter("password")); // required on add

                if (!StaffService.add(s)) {
                    request.setAttribute("errorMessage", "Failed to add staff.");
                    request.setAttribute("staffList", StaffService.getAll());
                    request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
                    return;
                }

            } else if ("edit".equals(action)) {
                // Edit existing staff (optional password change)
                Staff s = new Staff();
                s.setId(Integer.parseInt(request.getParameter("id")));
                s.setUsername(request.getParameter("username"));
                s.setAddress(request.getParameter("address"));
                s.setEmail(request.getParameter("email"));
                s.setTelephone(request.getParameter("telephone"));

                // If "Change Password" was used, this will be non-empty (from #f-new-password)
                String newPassword = request.getParameter("password");
                String confirm     = request.getParameter("confirmPassword");

                if (newPassword != null && !newPassword.isBlank()) {
                    if (confirm == null || !newPassword.equals(confirm)) {
                        request.setAttribute("errorMessage", "New password and confirm password do not match.");
                        request.setAttribute("staffList", StaffService.getAll());
                        request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
                        return;
                    }
                    s.setPassword(newPassword); // DAO will hash
                }

                if (!StaffService.update(s)) {
                    request.setAttribute("errorMessage", "Failed to update staff.");
                    request.setAttribute("staffList", StaffService.getAll());
                    request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
                    return;
                }

            } else if ("delete".equals(action)) {
                // Delete staff
                int id = Integer.parseInt(request.getParameter("id"));
                if (!StaffService.delete(id)) {
                    request.setAttribute("errorMessage", "Failed to delete staff.");
                    request.setAttribute("staffList", StaffService.getAll());
                    request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
                    return;
                }

            } else {
                request.setAttribute("errorMessage", "Unknown action.");
                request.setAttribute("staffList", StaffService.getAll());
                request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
                return;
            }

            // Redirect back to staff management page after success
            response.sendRedirect(request.getContextPath() + "/staff");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Server error: " + e.getMessage());
            try {
                request.setAttribute("staffList", StaffService.getAll());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
        }
    }
}
