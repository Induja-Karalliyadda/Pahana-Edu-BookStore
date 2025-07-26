package com.pahanaedu.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/test")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Database Test</title></head><body>");
        out.println("<h2>Database Connection Test</h2>");
        
        try {
            // Test database connection
            String url = "jdbc:mysql://localhost:3306/pahana_edu_book_store";
            String user = "root";
            String pass = "1234";
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            
            out.println("<p style='color: green;'>✅ Database connection successful!</p>");
            
            // Test query for all users
            String sql = "SELECT id, username, role, customer_code FROM users ORDER BY id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            out.println("<h3>All Users in Database:</h3>");
            out.println("<table border='1' style='border-collapse: collapse;'>");
            out.println("<tr style='background-color: #f0f0f0;'>");
            out.println("<th>ID</th><th>Username</th><th>Role</th><th>Customer Code</th>");
            out.println("</tr>");
            
            while (rs.next()) {
                String rowColor = "staff".equals(rs.getString("role")) ? "background-color: #ffffcc;" : "";
                out.println("<tr style='" + rowColor + "'>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("username") + "</td>");
                out.println("<td>" + rs.getString("role") + "</td>");
                out.println("<td>" + rs.getString("customer_code") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            
            // Test query for staff only
            String staffSql = "SELECT id, username, address, telephone, email, customer_code FROM users WHERE role = 'staff'";
            PreparedStatement staffStmt = conn.prepareStatement(staffSql);
            ResultSet staffRs = staffStmt.executeQuery();
            
            out.println("<h3>Staff Members Only:</h3>");
            out.println("<table border='1' style='border-collapse: collapse;'>");
            out.println("<tr style='background-color: #f0f0f0;'>");
            out.println("<th>ID</th><th>Username</th><th>Address</th><th>Telephone</th><th>Email</th><th>Code</th>");
            out.println("</tr>");
            
            int staffCount = 0;
            while (staffRs.next()) {
                staffCount++;
                out.println("<tr style='background-color: #e8f5e8;'>");
                out.println("<td>" + staffRs.getInt("id") + "</td>");
                out.println("<td>" + staffRs.getString("username") + "</td>");
                out.println("<td>" + staffRs.getString("address") + "</td>");
                out.println("<td>" + staffRs.getString("telephone") + "</td>");
                out.println("<td>" + staffRs.getString("email") + "</td>");
                out.println("<td>" + staffRs.getString("customer_code") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            
            out.println("<p><strong>Total Staff Found: " + staffCount + "</strong></p>");
            
            // Close connections
            staffRs.close();
            staffStmt.close();
            rs.close();
            stmt.close();
            conn.close();
            
            out.println("<hr>");
            out.println("<h3>Next Steps:</h3>");
            out.println("<p>1. <a href='" + request.getContextPath() + "/staff'>Go to Staff Management (Servlet)</a></p>");
            out.println("<p>2. <a href='" + request.getContextPath() + "/view/Staff.jsp'>Direct JSP Access (Wrong way)</a></p>");
            
        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        out.println("</body></html>");
    }
}