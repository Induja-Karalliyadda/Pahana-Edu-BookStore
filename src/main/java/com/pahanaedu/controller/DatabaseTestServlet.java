package com.pahanaedu.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/test-db")
public class DatabaseTestServlet extends HttpServlet {
    
    private static final String URL  = "jdbc:mysql://localhost:3306/pahana_edu_book_store";
    private static final String USER = "root";
    private static final String PASS = "1234";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><body>");
        out.println("<h2>Database Connection Test</h2>");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            out.println("<p>✓ MySQL Driver loaded successfully</p>");
            
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                out.println("<p>✓ Database connection established</p>");
                
                String sql = "SELECT * FROM users WHERE role='user' ORDER BY id";
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    
                    out.println("<h3>Users with role='user':</h3>");
                    out.println("<table border='1'>");
                    out.println("<tr><th>ID</th><th>Username</th><th>Customer Code</th><th>Role</th></tr>");
                    
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        out.println("<tr>");
                        out.println("<td>" + rs.getInt("id") + "</td>");
                        out.println("<td>" + rs.getString("username") + "</td>");
                        out.println("<td>" + rs.getString("customer_code") + "</td>");
                        out.println("<td>" + rs.getString("role") + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                    out.println("<p><strong>Total users found: " + count + "</strong></p>");
                }
            }
            
        } catch (ClassNotFoundException e) {
            out.println("<p>❌ MySQL Driver not found: " + e.getMessage() + "</p>");
        } catch (SQLException e) {
            out.println("<p>❌ Database error: " + e.getMessage() + "</p>");
        }
        
        out.println("<p><a href='" + request.getContextPath() + "/users'>Go to User Management</a></p>");
        out.println("</body></html>");
    }
}