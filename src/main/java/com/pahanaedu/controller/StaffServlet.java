package com.pahanaedu.controller;

import com.pahanaedu.dao.StaffDAO;
import com.pahanaedu.model.Staff;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;

@WebServlet("/staff")
public class StaffServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Add console logging
        System.out.println("================================");
        System.out.println("🚀 StaffServlet doGet() CALLED!");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("================================");
        
        // Test database connection directly in servlet
        try {
            // Direct database test
            String url = "jdbc:mysql://localhost:3306/pahana_edu_book_store";
            String user = "root";
            String pass = "1234";
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ Direct DB connection successful in servlet");
            
            // Direct query test
            String sql = "SELECT id, username, address, telephone, email, customer_code FROM users WHERE role = 'staff'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            List<Staff> staffList = new java.util.ArrayList<>();
            while (rs.next()) {
                Staff staff = new Staff();
                staff.setId(rs.getInt("id"));
                staff.setUsername(rs.getString("username"));
                staff.setAddress(rs.getString("address"));
                staff.setTelephone(rs.getString("telephone"));
                staff.setEmail(rs.getString("email"));
                staff.setCustomerCode(rs.getString("customer_code"));
                staffList.add(staff);
                
                System.out.println("📋 Found staff: " + staff.getUsername() + " (ID: " + staff.getId() + ")");
            }
            
            System.out.println("📊 Total staff found: " + staffList.size());
            
            rs.close();
            stmt.close();
            conn.close();
            
            // Set the staff list as request attribute
            request.setAttribute("staffList", staffList);
            System.out.println("✅ Set staffList attribute with " + staffList.size() + " items");
            
            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/view/Staff.jsp");
            System.out.println("➡️ Forwarding to /view/Staff.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in StaffServlet: " + e.getMessage());
            e.printStackTrace();
            
            // Send error response
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Error in StaffServlet</h2>");
            out.println("<p>Error: " + e.getMessage() + "</p>");
            out.println("<p><a href='" + request.getContextPath() + "/test'>Try Database Test</a></p>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📝 StaffServlet doPost() called");
        String action = request.getParameter("action");
        System.out.println("Action: " + action);
        
        // Redirect back to staff list
        response.sendRedirect(request.getContextPath() + "/staff");
    }
}