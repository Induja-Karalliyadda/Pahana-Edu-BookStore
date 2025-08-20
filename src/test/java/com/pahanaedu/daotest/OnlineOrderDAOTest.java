package com.pahanaedu.daotest;

import com.pahanaedu.dao.OnlineOrderDAO;
import com.pahanaedu.model.OnlineOrderItem;
import com.pahanaedu.util.Db;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class OnlineOrderDAOTest {

  private Connection setupConn;
  private Long orderId; // created per test so we can clean up

  @BeforeEach
  void setUp() throws Exception {
    // Try to open a real DB connection; if not, skip the DB-backed tests.
    try {
      setupConn = Db.get().getConnection();
    } catch (Throwable t) {
      assumeTrue(false, "DB not available; skipping integration tests. " + t.getMessage());
    }
    setupConn.setAutoCommit(true);

    // Seed one order + two items visible to OnlineOrderDAO (which opens its own connections)
    orderId = insertOrder(setupConn, "Test Customer");
    insertItem(setupConn, orderId, "BOOK", 101L, "Test Book", 2L, new BigDecimal("12.50"));
    insertItem(setupConn, orderId, "BOOK", 102L, "Another Book", 1L, new BigDecimal("25.00"));
  }

  @AfterEach
  void tearDown() throws Exception {
    if (setupConn != null) {
      try (PreparedStatement ps = setupConn.prepareStatement(
              "DELETE FROM online_order_items WHERE order_id = ?")) {
        ps.setLong(1, orderId);
        ps.executeUpdate();
      } catch (Exception ignore) {}

      try (PreparedStatement ps = setupConn.prepareStatement(
              "DELETE FROM online_orders WHERE id = ?")) {
        ps.setLong(1, orderId);
        ps.executeUpdate();
      } catch (Exception ignore) {}

      setupConn.close();
    }
  }

  // --- Pure unit test path (no DB required) ---
  @Test
  void updateStatus_rejectsInvalidValues_withoutTouchingDb() throws Exception {
    boolean ok = OnlineOrderDAO.updateStatus(123456L, "WRONG_STATUS");
    assertFalse(ok, "Invalid status must return false before hitting DB");
  }

  // --- Integration-style tests (use real DB via your Db utility) ---

  @Test
  void findItems_returnsPersistedItems() throws Exception {
    List<OnlineOrderItem> items = OnlineOrderDAO.findItems(orderId);

    assertEquals(2, items.size(), "Should load two items for the seeded order");
    // DAO sorts by id; insert order is preserved, so first item is "Test Book"
    assertEquals("Test Book", items.get(0).getItemName());
    assertEquals(2L, items.get(0).getQuantity());
    assertEquals("Another Book", items.get(1).getItemName());
  }

  @Test
  void updateStatus_validValue_updatesRow() throws Exception {
    assertEquals("PENDING", readStatus(orderId), "Seeded order starts PENDING");

    boolean ok = OnlineOrderDAO.updateStatus(orderId, "PAID");
    assertTrue(ok, "DAO should report one row updated");
    assertEquals("PAID", readStatus(orderId));
  }

  // ---------- helpers ----------

  private long insertOrder(Connection c, String customerName) throws SQLException {
    String sql = "INSERT INTO online_orders " +
                 "(customer_name, phone, email, address, total_amount, status, created_at) " +
                 "VALUES (?,?,?,?,?,?,?)";
    try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, customerName);
      ps.setString(2, "000-0000000");
      ps.setString(3, "test@example.com");
      ps.setString(4, "123 Test St");
      ps.setBigDecimal(5, new BigDecimal("50.00"));
      ps.setString(6, "PENDING");
      ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
      int n = ps.executeUpdate();
      if (n != 1) throw new SQLException("Insert order failed");

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) return rs.getLong(1);
      }
    }
    throw new SQLException("No order id generated");
  }

  private void insertItem(Connection c, long orderId, String type, long itemId,
                          String name, long qty, BigDecimal unitPrice) throws SQLException {
    String sql = "INSERT INTO online_order_items " +
                 "(order_id, item_type, item_id, item_name, quantity, unit_price) " +
                 "VALUES (?,?,?,?,?,?)";
    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, orderId);
      ps.setString(2, type);
      ps.setLong(3, itemId);
      ps.setString(4, name);
      ps.setLong(5, qty);
      ps.setBigDecimal(6, unitPrice);
      ps.executeUpdate();
    }
  }

  private String readStatus(long id) throws SQLException {
    try (Connection c = Db.get().getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT status FROM online_orders WHERE id = ?")) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getString(1);
        throw new SQLException("Order not found id=" + id);
      }
    }
  }
}
