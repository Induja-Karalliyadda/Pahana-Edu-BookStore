package com.pahanaedu.dao;

import com.pahanaedu.model.User;
import java.sql.*;
import java.util.*;

public class UserDAO {
    private static final String URL  = "jdbc:mysql://localhost:3306/pahana_edu_book_store";
    private static final String USER = "root";
    private static final String PASS = "1234";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Database connection established successfully");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            throw new SQLException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setCustomerCode(rs.getString("customer_code"));
        u.setUsername(rs.getString("username"));
        u.setAddress(rs.getString("address"));
        u.setTelephone(rs.getString("telephone"));
        u.setEmail(rs.getString("email"));
        // Include role for debugging
        String role = rs.getString("role");
        u.setRole(role);
        System.out.println("Mapped user: " + u.getUsername() + " with role: " + role);
        return u;
    }

    // 1) List all users
    public List<User> findAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, address, telephone, email, role, customer_code FROM users WHERE role='user' ORDER BY id";
        
        System.out.println("=== Executing findAllUsers ===");
        System.out.println("SQL: " + sql);
        
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            int count = 0;
            while (rs.next()) {
                User user = mapRow(rs);
                list.add(user);
                count++;
            }
            System.out.println("Total users found: " + count);
            
        } catch (SQLException ex) {
            System.err.println("Error in findAllUsers: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        System.out.println("Returning list with " + list.size() + " users");
        return list;
    }

    // 2) Search users by username or customer code
    public List<User> searchUsers(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, address, telephone, email, role, customer_code FROM users WHERE role='user' AND (username LIKE ? OR customer_code LIKE ?) ORDER BY id";
        
        System.out.println("=== Executing searchUsers ===");
        System.out.println("SQL: " + sql);
        System.out.println("Keyword: '" + keyword + "'");
        
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String pat = "%" + keyword + "%";
            ps.setString(1, pat);
            ps.setString(2, pat);
            
            System.out.println("Search pattern: '" + pat + "'");

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    User user = mapRow(rs);
                    list.add(user);
                    count++;
                }
                System.out.println("Total users found in search: " + count);
            }
        } catch (SQLException ex) {
            System.err.println("Error in searchUsers: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        System.out.println("Returning search list with " + list.size() + " users");
        return list;
    }

    // 3) Update user
    public boolean updateUser(User u) {
        String sql = "UPDATE users SET username=?, address=?, telephone=?, email=? WHERE id=? AND role='user'";
        
        System.out.println("=== Updating user ===");
        System.out.println("User ID: " + u.getId() + ", Username: " + u.getUsername());
        
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getAddress());
            ps.setString(3, u.getTelephone());
            ps.setString(4, u.getEmail());
            ps.setInt(5, u.getId());
            
            int rowsAffected = ps.executeUpdate();
            boolean success = rowsAffected > 0;
            System.out.println("Update result: " + (success ? "SUCCESS" : "FAILED") + " (rows affected: " + rowsAffected + ")");
            
            return success;
        } catch (SQLException ex) {
            System.err.println("Error in updateUser: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    // 4) Delete user
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=? AND role='user'";
        
        System.out.println("=== Deleting user ===");
        System.out.println("User ID: " + id);
        
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            int rowsAffected = ps.executeUpdate();
            boolean success = rowsAffected > 0;
            System.out.println("Delete result: " + (success ? "SUCCESS" : "FAILED") + " (rows affected: " + rowsAffected + ")");
            
            return success;
        } catch (SQLException ex) {
            System.err.println("Error in deleteUser: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    // 5) Register user
    public boolean registerUser(User u) {
        String sql = """
            INSERT INTO users (username, address, telephone, email, password, role, customer_code)
            VALUES (?, ?, ?, ?, SHA2(?, 256), 'user', ?)
        """;
        
        System.out.println("=== Registering new user ===");
        System.out.println("Username: " + u.getUsername());
        
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String newCode = nextCustomerCode(c);
            System.out.println("Generated customer code: " + newCode);

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getAddress());
            ps.setString(3, u.getTelephone());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPassword());
            ps.setString(6, newCode);

            boolean success = ps.executeUpdate() > 0;
            System.out.println("Registration result: " + (success ? "SUCCESS" : "FAILED"));
            
            return success;
        } catch (SQLException e) {
            System.err.println("Error in registerUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Generate next customer code
    private String nextCustomerCode(Connection c) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(customer_code, 2) AS UNSIGNED)) FROM users WHERE role='user'";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int max = rs.next() ? rs.getInt(1) : 0;
            return String.format("C%03d", max + 1);
        }
    }

    // 6) Login method
    public User getUserByEmailAndPassword(String idOrEmail, String pwd) {
        String sql = """
            SELECT id, username, address, telephone, email, role, customer_code
            FROM users 
            WHERE (email = ? OR username = ?) 
              AND password = SHA2(?, 256)
        """;
        
        System.out.println("=== Login attempt ===");
        System.out.println("ID/Email: " + idOrEmail);
        
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, idOrEmail);
            ps.setString(2, idOrEmail);
            ps.setString(3, pwd);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setCustomerCode(rs.getString("customer_code"));
                    u.setUsername(rs.getString("username"));
                    u.setAddress(rs.getString("address"));
                    u.setTelephone(rs.getString("telephone"));
                    u.setEmail(rs.getString("email"));
                    u.setRole(rs.getString("role"));
                    
                    System.out.println("Login successful for user: " + u.getUsername() + " (role: " + u.getRole() + ")");
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getUserByEmailAndPassword: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Login failed for: " + idOrEmail);
        return null;
    }
}