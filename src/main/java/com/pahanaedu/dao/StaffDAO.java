package com.pahanaedu.dao;

import com.pahanaedu.model.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    private static final String URL  = "jdbc:mysql://localhost:3306/pahana_edu_book_store";
    private static final String USER = "root";
    private static final String PASS = "1234";

    private Connection getConnection() throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new SQLException("MySQL Driver not found", e); }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private String nextCode(Connection c) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(customer_code,2) AS UNSIGNED)) FROM users";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int max = 0;
            if (rs.next()) max = rs.getInt(1);
            return String.format("C%03d", max + 1);
        }
    }

    private Staff map(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setId(rs.getInt("id"));
        s.setUsername(rs.getString("username"));
        s.setAddress(rs.getString("address"));
        s.setTelephone(rs.getString("telephone"));
        s.setEmail(rs.getString("email"));
        s.setPassword(rs.getString("password"));
        s.setCustomerCode(rs.getString("customer_code"));
        return s;
    }

    public List<Staff> findAll() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='staff' ORDER BY id DESC";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Staff s) {
        String sql = "INSERT INTO users (username,address,telephone,email,password,role,customer_code) " +
                     "VALUES (?,?,?,?,SHA2(?,256),'staff',?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String code = nextCode(c);
            ps.setString(1, s.getUsername());
            ps.setString(2, s.getAddress());
            ps.setString(3, s.getTelephone());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPassword());
            ps.setString(6, code);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Insert Staff failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Staff s) {
        String sql = "UPDATE users SET username=?,address=?,telephone=?,email=? WHERE id=? AND role='staff'";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getUsername());
            ps.setString(2, s.getAddress());
            ps.setString(3, s.getTelephone());
            ps.setString(4, s.getEmail());
            ps.setInt(5, s.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id=? AND role='staff'";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}

