package com.pahanaedu.dao;

import java.sql.*;
import java.util.*;

import com.pahanaedu.model.OnlineOrder;
import com.pahanaedu.model.OnlineOrderItem;

public class OnlineOrderDAO {

  // --- DB config ---
  private static final String URL  =
      "jdbc:mysql://localhost:3306/pahana_edu_book_store"
      + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
  private static final String USER = "root";
  private static final String PASS = "1234";

  // Load MySQL driver once
  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      System.err.println("JDBC Driver load failed: " + e.getMessage());
    }
  }

  // Get a connection
  private static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASS);
  }

  // ===== QUERIES =====

  public static List<OnlineOrder> findAll() throws SQLException {
    String sql = "SELECT id, customer_name, phone, email, address, total_amount, status, created_at "
               + "FROM online_orders ORDER BY created_at DESC";
    List<OnlineOrder> list = new ArrayList<>();
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        OnlineOrder o = new OnlineOrder();
        o.setId(rs.getLong("id"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setPhone(rs.getString("phone"));
        o.setEmail(rs.getString("email"));
        o.setAddress(rs.getString("address"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        list.add(o);
      }
    }
    return list;
  }

  public static List<OnlineOrderItem> findItems(long orderId) throws SQLException {
    String sql = "SELECT id, order_id, item_type, item_id, item_name, quantity, unit_price "
               + "FROM online_order_items WHERE order_id = ? ORDER BY id";
    List<OnlineOrderItem> list = new ArrayList<>();
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

      ps.setLong(1, orderId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          OnlineOrderItem it = new OnlineOrderItem();
          it.setId(rs.getLong("id"));
          it.setOrderId(rs.getLong("order_id"));
          it.setItemType(rs.getString("item_type"));
          it.setItemId(rs.getLong("item_id"));
          it.setItemName(rs.getString("item_name"));
          it.setQuantity(rs.getLong("quantity"));
          it.setUnitPrice(rs.getBigDecimal("unit_price"));
          list.add(it);
        }
      }
    }
    return list;
  }

  public static boolean updateStatus(long orderId, String status) throws SQLException {
    if (!Arrays.asList("PENDING", "PAID", "CANCELLED").contains(status)) return false;
    String sql = "UPDATE online_orders SET status = ? WHERE id = ?";
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, status);
      ps.setLong(2, orderId);
      return ps.executeUpdate() == 1;
    }
  }
}
