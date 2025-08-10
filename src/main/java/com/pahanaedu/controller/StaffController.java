package com.pahanaedu.controller;

import com.pahanaedu.service.StaffService;
import com.pahanaedu.model.Staff;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;

@WebServlet("/staff")
public class StaffController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Staff> staffList = StaffService.getAll();
            request.setAttribute("staffList", staffList);
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

        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                Staff s = new Staff();
                s.setUsername(request.getParameter("username"));
                s.setAddress(request.getParameter("address"));
                s.setEmail(request.getParameter("email"));
                s.setTelephone(request.getParameter("telephone"));
                s.setPassword(request.getParameter("password"));

                if (!StaffService.add(s)) {
                    request.setAttribute("errorMessage", "Failed to add staff.");
                    request.setAttribute("staffList", StaffService.getAll());
                    request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
                    return;
                }

            } else if ("edit".equals(action)) {
                Staff s = new Staff();
                s.setId(Integer.parseInt(request.getParameter("id")));
                s.setUsername(request.getParameter("username"));
                s.setAddress(request.getParameter("address"));
                s.setEmail(request.getParameter("email"));
                s.setTelephone(request.getParameter("telephone"));

                if (!StaffService.update(s)) {
                    request.setAttribute("errorMessage", "Failed to update staff.");
                    request.setAttribute("staffList", StaffService.getAll());
                    request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
                    return;
                }

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                if (!StaffService.delete(id)) {
                    request.setAttribute("errorMessage", "Failed to delete staff.");
                    request.setAttribute("staffList", StaffService.getAll());
                    request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
                    return;
                }
            }

            response.sendRedirect(request.getContextPath() + "/staff");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Server error: " + e.getMessage());
            request.setAttribute("staffList", StaffService.getAll());
            request.getRequestDispatcher("/view/Staff.jsp").forward(request, response);
        }
    }
}
