package com.pahanaedu.dao;

import com.pahanaedu.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            throw new SQLException(e);
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
        try { u.setRole(rs.getString("role")); } catch (SQLException ignore) {}
        return u;
    }

    // -------- READS --------

    public List<User> findAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='user' ORDER BY id";
        System.out.println("Executing findAllUsers query: " + sql);
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                User user = mapRow(rs);
                list.add(user);
                count++;
                System.out.println("Found user: " + user.getUsername() + " (" + user.getCustomerCode() + ")");
            }
            System.out.println("Total users found: " + count);
        } catch (SQLException ex) {
            System.err.println("Error in findAllUsers: " + ex.getMessage());
            ex.printStackTrace();
        }
        return list;
    }

    public List<User> searchUsers(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='user' AND (username LIKE ? OR customer_code LIKE ?) ORDER BY id";
        System.out.println("Executing searchUsers query with keyword: " + keyword);
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String pat = "%" + keyword + "%";
            ps.setString(1, pat);
            ps.setString(2, pat);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    User user = mapRow(rs);
                    list.add(user);
                    count++;
                    System.out.println("Found user: " + user.getUsername() + " (" + user.getCustomerCode() + ")");
                }
                System.out.println("Total users found in search: " + count);
            }
        } catch (SQLException ex) {
            System.err.println("Error in searchUsers: " + ex.getMessage());
            ex.printStackTrace();
        }
        return list;
    }

    public boolean updateUser(User u) {
        String sql = "UPDATE users SET username=?, address=?, telephone=?, email=? WHERE id=? AND role='user'";
        System.out.println("Updating user: " + u.getId() + " - " + u.getUsername());
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

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=? AND role='user'";
        System.out.println("Deleting user with ID: " + id);
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

    public boolean registerUser(User u) {
        return createCustomer(u) != null;
    }

    public User getUserByEmailAndPassword(String idOrEmail, String pwd) {
        String sql = """
            SELECT * FROM users
            WHERE (email = ? OR username = ?)
              AND password = SHA2(?, 256)
        """;
        System.out.println("Login attempt for: " + idOrEmail);
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
                    System.out.println("Login successful for user: " + u.getUsername());
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

    // -------- HELPERS --------

    private User findByEmailOrUsername(Connection c, String email, String username) throws SQLException {
        String sql = "SELECT id, username, address, telephone, email, role, customer_code " +
                     "FROM users WHERE (email=? OR username=?) AND role='user' LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setAddress(rs.getString("address"));
                    u.setTelephone(rs.getString("telephone"));
                    u.setEmail(rs.getString("email"));
                    u.setRole(rs.getString("role"));
                    u.setCustomerCode(rs.getString("customer_code"));
                    return u;
                }
            }
        }
        return null;
    }

    // -------- CREATE (single, canonical version) --------

    public User createCustomer(User u) {
        final String insertSql =
            "INSERT INTO users (username,address,telephone,email,password,role,customer_code) " +
            "VALUES (?,?,?,?,SHA2(?,256),'user',?)";

        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try {
                // 1) Prevent duplicates by email/username
                User exists = findByEmailOrUsername(c, u.getEmail(), u.getUsername());
                if (exists != null) {
                    c.rollback();
                    System.out.println("createCustomer: duplicate email/username, aborting.");
                    return null;
                }

                // 2) Generate next code safely (gap-free while we hold the lock)
                String nextCode;
                try (PreparedStatement ps = c.prepareStatement(
                         "SELECT LPAD(COALESCE(MAX(CAST(SUBSTRING(customer_code,2) AS UNSIGNED)),0) + 1, 3, '0') " +
                         "FROM users FOR UPDATE");
                     ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    nextCode = "C" + rs.getString(1);
                }
                String defaultPwd = nextCode; // rule: temp password = code

                // 3) Insert row
                int newId;
                try (PreparedStatement ps = c.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, u.getUsername());
                    ps.setString(2, u.getAddress());
                    ps.setString(3, u.getTelephone());
                    ps.setString(4, u.getEmail());
                    ps.setString(5, defaultPwd);
                    ps.setString(6, nextCode);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        newId = keys.getInt(1);
                    }
                }

                c.commit();

                User created = new User();
                created.setId(newId);
                created.setUsername(u.getUsername());
                created.setAddress(u.getAddress());
                created.setTelephone(u.getTelephone());
                created.setEmail(u.getEmail());
                created.setRole("user");
                created.setCustomerCode(nextCode);
                created.setTempPassword(defaultPwd); // expose to controller for email
                System.out.println("Registration SUCCESS. New ID: " + newId + ", Code: " + nextCode);
                return created;

            } catch (SQLIntegrityConstraintViolationException dup) {
                c.rollback();
                System.out.println("createCustomer: unique constraint violation.");
                return null;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error in createCustomer: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
 // Find a user by customer_code (used as fallback if Order doesn't expose customerId)
    public User findByCustomerCode(String customerCode) throws Exception {
        String sql = "SELECT id, username, email, address, telephone, customer_code, role FROM users WHERE customer_code = ?";
        try (java.sql.Connection con = getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerCode);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setAddress(rs.getString("address"));
                    u.setTelephone(rs.getString("telephone"));
                    u.setCustomerCode(rs.getString("customer_code"));
                    u.setRole(rs.getString("role"));
                    return u;
                }
            }
        }
        return null;
    }
    public User findById(int id) throws Exception {
        String sql = "SELECT id, username, email, address, telephone, customer_code, role FROM users WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setAddress(rs.getString("address"));
                    u.setTelephone(rs.getString("telephone"));
                    u.setCustomerCode(rs.getString("customer_code"));
                    u.setRole(rs.getString("role"));
                    return u;
                }
            }
        }
        return null;
    }

}