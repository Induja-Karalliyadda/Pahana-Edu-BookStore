// Keep this one as /test-db
package com.pahanaedu.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/test-db")
public class DatabaseTestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        String url = "jdbc:mysql://localhost:3306/pahana_edu_book_store";
        String user = "root";
        String pass = "1234";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        out.println("<html><body>");
        out.println("<h2>Database Connection Test</h2>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            
            out.println("<p style='color: green;'>✅ Database connection successful!</p>");

            String sql = "SELECT * FROM users LIMIT 5";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            out.println("<h3>Sample Users Data:</h3>");
            out.println("<table border='1' style='border-collapse: collapse;'>");
            out.println("<tr><th>Username</th><th>Role</th></tr>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("username") + "</td>");
                out.println("<td>" + rs.getString("role") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");

        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Error: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
        
        out.println("</body></html>");
    }
}
