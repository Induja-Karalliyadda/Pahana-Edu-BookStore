package com.pahanaedu.daotest;

import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.model.Accessory;
import com.pahanaedu.model.Book;
import com.pahanaedu.util.Db;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ItemDAOTest {

  @BeforeEach
  void requireDatabase() {
    // Skip tests if your test DB isn’t configured / reachable.
    assumeTrue(ItemDAO.testConnection(), "DB not available for ItemDAO tests");
  }

  // ---------- Book lifecycle ----------

  @Test
  void save_get_update_delete_book_roundtrip() throws Exception {
    ItemDAO dao = new ItemDAO();
    String unique = "JUnit-Book-" + System.currentTimeMillis();

    // save
    Book b = new Book();
    b.setTitle(unique);
    b.setAuthor("Tester");
    b.setCategory("Programming");
    b.setDescription("Inserted by ItemDAOTest");
    b.setSupplier("TestSupplier");
    b.setCostPrice(10.50);
    b.setSellingPrice(15.75);
    b.setStock(7);
    b.setImageUrl("http://example/test.png");
    b.setMinStock(2);
    dao.saveBook(b);

    int id = findBookIdByTitle(unique);
    assertTrue(id > 0, "Book ID should be generated");

    // get
    Book loaded = dao.getBookById(id);
    assertNotNull(loaded);
    assertEquals(unique, loaded.getTitle());
    assertEquals("Programming", loaded.getCategory());

    // update
    loaded.setBookId(id);
    loaded.setSellingPrice(19.99);
    loaded.setStock(12);
    dao.updateBook(loaded);

    Book updated = dao.getBookById(id);
    assertNotNull(updated);
    assertEquals(19.99, updated.getSellingPrice(), 0.0001);
    assertEquals(12, updated.getStock());

    // delete
    dao.deleteBook(id);
    assertNull(dao.getBookById(id), "Deleted book should not be found");
  }

  @Test
  void findBooks_filtersByTitle_and_Category() throws Exception {
    ItemDAO dao = new ItemDAO();
    String base = "JUnit-Search-" + System.currentTimeMillis();

    // Seed a row we can find
    Book b = new Book();
    b.setTitle(base + " Clean Code");
    b.setAuthor("S. Tester");
    b.setCategory("Programming");
    b.setDescription("findBooks test");
    b.setSupplier("Test");
    b.setCostPrice(9.0);
    b.setSellingPrice(12.0);
    b.setStock(3);
    b.setImageUrl("");
    b.setMinStock(1);
    dao.saveBook(b);
    int id = findBookIdByTitle(b.getTitle());

    // Exercise
    var results = dao.findBooks("Search", "Programming");
    assertTrue(results.stream().anyMatch(x -> x.getBookId() == id));

    // Cleanup
    dao.deleteBook(id);
  }

  // ---------- Accessory lifecycle ----------

  @Test
  void save_get_update_delete_accessory_roundtrip() throws Exception {
    ItemDAO dao = new ItemDAO();
    String unique = "JUnit-Acc-" + System.currentTimeMillis();

    // save
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
    dao.saveAccessory(a);

    int id = findAccessoryIdByName(unique);
    assertTrue(id > 0);

    // get
    Accessory got = dao.getAccessoryById(id);
    assertNotNull(got);
    assertEquals(unique, got.getName());

    // update
    got.setAccessoryId(id);
    got.setSellingPrice(4.25);
    got.setStock(9);
    dao.updateAccessory(got);

    Accessory updated = dao.getAccessoryById(id);
    assertNotNull(updated);
    assertEquals(4.25, updated.getSellingPrice(), 0.0001);
    assertEquals(9, updated.getStock());

    // delete
    dao.deleteAccessory(id);
    assertNull(dao.getAccessoryById(id));
  }

  @Test
  void findAccessories_filtersByName_and_Category() throws Exception {
    ItemDAO dao = new ItemDAO();
    String base = "JUnit-Clip-" + System.currentTimeMillis();

    // Seed
    Accessory a = new Accessory();
    a.setName(base + " Paper Clip");
    a.setCategory("Stationery");
    a.setDescription("findAccessories test");
    a.setSupplier("Test");
    a.setCostPrice(0.10);
    a.setSellingPrice(0.25);
    a.setStock(100);
    a.setImageUrl("");
    a.setMinStock(10);
    dao.saveAccessory(a);
    int id = findAccessoryIdByName(a.getName());

    // Exercise
    var list = dao.findAccessories("Clip", "Stationery");
    assertTrue(list.stream().anyMatch(x -> x.getAccessoryId() == id));

    // Cleanup
    dao.deleteAccessory(id);
  }

  // ---------- helpers (use your real Db wiring) ----------

  private int findBookIdByTitle(String title) throws Exception {
    try (Connection c = Db.get().getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT book_id FROM books WHERE title = ? ORDER BY book_id DESC LIMIT 1")) {
      ps.setString(1, title);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    }
    return -1;
  }

  private int findAccessoryIdByName(String name) throws Exception {
    try (Connection c = Db.get().getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT accessory_id FROM accessories WHERE name = ? ORDER BY accessory_id DESC LIMIT 1")) {
      ps.setString(1, name);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    }
    return -1;
  }
}
