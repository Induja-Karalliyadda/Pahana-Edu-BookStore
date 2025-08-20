package com.pahanaedu.servicetest;

import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.model.Accessory;
import com.pahanaedu.model.Book;
import com.pahanaedu.service.ItemService;
import com.pahanaedu.util.Db;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ItemServiceTest {

  private final ItemService svc = new ItemService();
  private Integer createdBookId = null;
  private Integer createdAccId  = null;
  private String  createdBookTitle = null;
  private String  createdAccName   = null;

  @BeforeEach
  void requireDatabase() {
    // Skip the whole test run if DB isn't configured.
    assumeTrue(ItemDAO.testConnection(), "DB not available for ItemService tests");
  }

  @AfterEach
  void cleanup() {
    // Best-effort cleanup in case a test aborts mid-way.
    try {
      if (createdBookId == null && createdBookTitle != null) {
        createdBookId = findBookIdByTitle(createdBookTitle);
      }
      if (createdBookId != null) svc.deleteBook(createdBookId);
    } catch (Exception ignore) {}

    try {
      if (createdAccId == null && createdAccName != null) {
        createdAccId = findAccessoryIdByName(createdAccName);
      }
      if (createdAccId != null) svc.deleteAccessory(createdAccId);
    } catch (Exception ignore) {}

    createdBookId = null; createdAccId = null;
    createdBookTitle = null; createdAccName = null;
  }

  // ---------- BOOKS ----------

  @Test
  void books_roundTrip_viaService() {
    String unique = "JUnit-Book-" + System.currentTimeMillis();
    createdBookTitle = unique;

    Book b = new Book();
    b.setTitle(unique);
    b.setAuthor("Tester");
    b.setCategory("Programming");
    b.setDescription("Inserted by ItemServiceTest");
    b.setSupplier("TestSupplier");
    b.setCostPrice(10.50);
    b.setSellingPrice(15.75);
    b.setStock(7);
    b.setImageUrl("http://example/test.png");
    b.setMinStock(2);

    // create
    assertTrue(svc.addBook(b));

    // locate created id using service search
    Book loaded = svc.getBooks(unique, "").stream()
                     .filter(x -> unique.equals(x.getTitle()))
                     .findFirst().orElse(null);
    assertNotNull(loaded, "Inserted book should be returned by getBooks()");
    createdBookId = loaded.getBookId();
    assertTrue(createdBookId > 0);

    // read by id
    Book byId = svc.getBookById(createdBookId);
    assertNotNull(byId);
    assertEquals("Programming", byId.getCategory());

    // update
    byId.setSellingPrice(19.99);
    byId.setStock(12);
    assertTrue(svc.updateBook(byId));

    Book afterUpd = svc.getBookById(createdBookId);
    assertNotNull(afterUpd);
    assertEquals(19.99, afterUpd.getSellingPrice(), 0.0001);
    assertEquals(12, afterUpd.getStock());

    // delete
    assertTrue(svc.deleteBook(createdBookId));
    assertNull(svc.getBookById(createdBookId));
    createdBookId = null; // already removed
  }

  @Test
  void getBooks_handlesNullFilters_returnsListPossiblyEmpty() {
    // Just verify it doesn't throw and returns a list when params are null.
    assertNotNull(svc.getBooks(null, null));
  }

  // ---------- ACCESSORIES ----------

  @Test
  void accessories_roundTrip_viaService() {
    String unique = "JUnit-Acc-" + System.currentTimeMillis();
    createdAccName = unique;

    Accessory a = new Accessory();
    a.setName(unique);
    a.setCategory("Stationery");
    a.setDescription("Accessory by test");
    a.setSupplier("TestSupplier");
    a.setCostPrice(2.25);
    a.setSellingPrice(3.50);
    a.setStock(5);
    a.setImageUrl("");
    a.setMinStock(1);

    // create
    assertTrue(svc.addAccessory(a));

    // locate created id using service search
    Accessory found = svc.getAccessories(unique, "").stream()
                         .filter(x -> unique.equals(x.getName()))
                         .findFirst().orElse(null);
    assertNotNull(found, "Inserted accessory should be returned by getAccessories()");
    createdAccId = found.getAccessoryId();
    assertTrue(createdAccId > 0);

    // read by id
    Accessory byId = svc.getAccessoryById(createdAccId);
    assertNotNull(byId);
    assertEquals("Stationery", byId.getCategory());

    // update
    byId.setSellingPrice(4.25);
    byId.setStock(9);
    assertTrue(svc.updateAccessory(byId));

    Accessory afterUpd = svc.getAccessoryById(createdAccId);
    assertNotNull(afterUpd);
    assertEquals(4.25, afterUpd.getSellingPrice(), 0.0001);
    assertEquals(9, afterUpd.getStock());

    // delete
    assertTrue(svc.deleteAccessory(createdAccId));
    assertNull(svc.getAccessoryById(createdAccId));
    createdAccId = null; // already removed
  }

  @Test
  void getAccessories_handlesNullFilters_returnsListPossiblyEmpty() {
    assertNotNull(svc.getAccessories(null, null));
  }

  // ---------- helpers (DB lookups for cleanup fallback) ----------

  private Integer findBookIdByTitle(String title) throws Exception {
    try (Connection c = Db.get().getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT book_id FROM books WHERE title = ? ORDER BY book_id DESC LIMIT 1")) {
      ps.setString(1, title);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    }
    return null;
  }

  private Integer findAccessoryIdByName(String name) throws Exception {
    try (Connection c = Db.get().getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT accessory_id FROM accessories WHERE name = ? ORDER BY accessory_id DESC LIMIT 1")) {
      ps.setString(1, name);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    }
    return null;
  }
}
