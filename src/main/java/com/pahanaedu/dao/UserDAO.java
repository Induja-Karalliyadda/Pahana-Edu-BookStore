package com.pahanaedu.dao;

import com.pahanaedu.model.User;
import java.sql.*;

public class UserDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pahana_edu_book_store";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    // Generate next customer code (C001, C002, ...)
    private String generateCustomerCode(Connection conn) throws SQLException {
        String sql = "SELECT customer_code FROM users ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastCode = rs.getString(1); // e.g., "C005"
                try {
                    int num = Integer.parseInt(lastCode.substring(1));
                    return String.format("C%03d", num + 1);
                } catch (Exception e) {
                    return "C001";
                }
            }
        }
        return "C001";
    }

    // Register new user
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, address, telephone, email, password, role, customer_code) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String customerCode = generateCustomerCode(conn);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getAddress());
            pstmt.setString(3, user.getTelephone());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getRole());
            pstmt.setString(7, customerCode);

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if user exists by email
    public boolean userExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Login: find user by **email** and password
    public User getUserByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Add customer_code if you need
                return new User(
                    rs.getString("username"),
                    rs.getString("address"),
                    rs.getString("telephone"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                    // , rs.getString("customer_code") // Add to constructor if you need
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

