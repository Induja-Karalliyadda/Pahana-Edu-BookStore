package com.pahanaedu.dao;

import com.pahanaedu.model.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/pahana_edu_book_store";
    private static final String USER = "root";
    private static final String PASS = "1234";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✅ Database connection successful");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found");
            throw new SQLException("MySQL Driver not found", e);
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            throw e;
        }
    }

    public List<Staff> findAll() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT id, username, address, telephone, email, customer_code FROM users WHERE role = 'staff' ORDER BY id";
        
        System.out.println("🔍 Executing SQL: " + sql);
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
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
            
            System.out.println("✅ Total staff found: " + staffList.size());
            
        } catch (SQLException e) {
            System.err.println("❌ Error in findAll(): " + e.getMessage());
            e.printStackTrace();
        }
        
        return staffList;
    }

    public boolean insert(Staff staff) {
        String sql = "INSERT INTO users (username, address, telephone, email, password, role, customer_code) VALUES (?, ?, ?, ?, SHA2(?, 256), 'staff', ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String newCode = generateNextCustomerCode(conn);
            
            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getAddress());
            stmt.setString(3, staff.getTelephone());
            stmt.setString(4, staff.getEmail());
            stmt.setString(5, staff.getPassword());
            stmt.setString(6, newCode);

            int result = stmt.executeUpdate();
            System.out.println("✅ Staff insert result: " + result + " rows affected");
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error inserting staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Staff staff) {
        String sql = "UPDATE users SET username=?, address=?, telephone=?, email=? WHERE id=? AND role='staff'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getAddress());
            stmt.setString(3, staff.getTelephone());
            stmt.setString(4, staff.getEmail());
            stmt.setInt(5, staff.getId());

            int result = stmt.executeUpdate();
            System.out.println("✅ Staff update result: " + result + " rows affected");
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id=? AND role='staff'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            System.out.println("✅ Staff delete result: " + result + " rows affected");
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String generateNextCustomerCode(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(customer_code, 2) AS UNSIGNED)) FROM users WHERE customer_code LIKE 'C%'";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int maxNumber = 0;
            if (rs.next()) {
                maxNumber = rs.getInt(1);
            }
            
            String newCode = String.format("C%03d", maxNumber + 1);
            System.out.println("🔢 Generated new customer code: " + newCode);
            return newCode;
        }
    }
}