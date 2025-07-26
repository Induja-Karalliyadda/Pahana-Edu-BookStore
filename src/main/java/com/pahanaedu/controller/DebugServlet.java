package com.pahanaedu.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/debug")
public class DebugServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        out.println("<html><head><title>Database Debug</title></head><body>");
        out.println("<h2>Database Connection Test</h2>");
        
        String url = "jdbc:mysql://localhost:3306/pahana_edu_book_store?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String pass = "1234";
        
        try {
            // Test 1: Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            out.println("<p>✅ MySQL Driver loaded successfully</p>");
            
            // Test 2: Connect to database
            Connection conn = DriverManager.getConnection(url, user, pass);
            out.println("<p>✅ Database connection successful</p>");
            
            // Test 3: Query all users
            String sql = "SELECT * FROM users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            out.println("<h3>All Users:</h3>");
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Username</th><th>Role</th><th>Customer Code</th></tr>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("username") + "</td>");
                out.println("<td>" + rs.getString("role") + "</td>");
                out.println("<td>" + rs.getString("customer_code") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            
            // Test 4: Query only staff
            String staffSql = "SELECT * FROM users WHERE role = 'staff'";
            PreparedStatement staffPs = conn.prepareStatement(staffSql);
            ResultSet staffRs = staffPs.executeQuery();
            
            out.println("<h3>Staff Only:</h3>");
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Username</th><th>Email</th><th>Customer Code</th></tr>");
            
            int staffCount = 0;
            while (staffRs.next()) {
                staffCount++;
                out.println("<tr>");
                out.println("<td>" + staffRs.getInt("id") + "</td>");
                out.println("<td>" + staffRs.getString("username") + "</td>");
                out.println("<td>" + staffRs.getString("email") + "</td>");
                out.println("<td>" + staffRs.getString("customer_code") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<p><strong>Total Staff Found: " + staffCount + "</strong></p>");
            
            // Close connections
            staffRs.close();
            staffPs.close();
            rs.close();
            ps.close();
            conn.close();
            
        } catch (ClassNotFoundException e) {
            out.println("<p>❌ MySQL Driver not found: " + e.getMessage() + "</p>");
        } catch (SQLException e) {
            out.println("<p>❌ Database error: " + e.getMessage() + "</p>");
        }
        
        out.println("<br><a href='" + req.getContextPath() + "/staff'>Go to Staff Management</a>");
        out.println("</body></html>");
    }
}