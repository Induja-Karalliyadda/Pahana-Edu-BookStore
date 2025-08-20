package com.pahanaedu.servicetest;

import com.pahanaedu.model.Staff;
import com.pahanaedu.service.StaffService;
import com.pahanaedu.util.Db;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class StaffServiceTest {

  private Integer createdId; // cleaned up after each test

  @BeforeEach
  void requireDatabase() throws Exception {
    try (Connection c = Db.get().getConnection()) {
      assumeTrue(c != null && !c.isClosed(), "DB not available for StaffService tests");
    } catch (Throwable t) {
      assumeTrue(false, "DB not available: " + t.getMessage());
    }
  }

  @AfterEach
  void cleanup() {
    if (createdId != null) {
      try { StaffService.delete(createdId); } catch (Exception ignore) {}
      createdId = null;
    }
  }

  @Test
  void add_getAll_update_delete_roundtrip_viaService() {
    String uniq = "junit_staff_" + System.currentTimeMillis();
    String email1 = uniq + "@example.com";

    // ---- insert ----
    Staff s = new Staff();
    s.setUsername(uniq);
    s.setAddress("123 Test St");
    s.setTelephone("0771234567");
    s.setEmail(email1);
    s.setPassword("p@ssw0rd"); // hashed by SQL

    assertTrue(StaffService.add(s), "Insert should succeed");

    createdId = findIdByUsername(uniq);
    assertNotNull(createdId, "Inserted staff id should be discoverable via getAll()");

    // customer_code should be like C###
    String code = findById(createdId).map(Staff::getCustomerCode).orElse(null);
    assertNotNull(code, "customer_code should be set");
    assertTrue(code.matches("C\\d{3}"), "customer_code should match C### pattern, was: " + code);

    // ---- update (no password change path) ----
    String newName = uniq + "_upd";
    String newEmail = "new_" + email1;

    Staff upd = new Staff();
    upd.setId(createdId);
    upd.setUsername(newName);
    upd.setAddress("456 New Rd");
    upd.setTelephone("0711111111");
    upd.setEmail(newEmail);
    // password not set -> triggers no-password SQL
    assertTrue(StaffService.update(upd), "Update (no password) should succeed");

    Staff afterNoPwd = findById(createdId).orElseThrow();
    assertEquals(newName, afterNoPwd.getUsername());
    assertEquals(newEmail, afterNoPwd.getEmail());
    assertEquals("456 New Rd", afterNoPwd.getAddress());
    assertEquals("0711111111", afterNoPwd.getTelephone());

    // ---- update (with password change path) ----
    upd.setPassword("n3wP@ss!");
    assertTrue(StaffService.update(upd), "Update (with password) should succeed");
    // we can't read hashed password back; just ensure other fields remain
    Staff afterPwd = findById(createdId).orElseThrow();
    assertEquals(newName, afterPwd.getUsername());
    assertEquals(newEmail, afterPwd.getEmail());

    // ---- delete ----
    assertTrue(StaffService.delete(createdId), "Delete should return true");
    assertFalse(findById(createdId).isPresent(), "Deleted staff should not be returned by getAll()");
    createdId = null; // already deleted
  }

  @Test
  void update_nonExisting_returnsFalse_viaService() {
    Staff ghost = new Staff();
    ghost.setId(-12345);
    ghost.setUsername("ghost");
    ghost.setAddress("n/a");
    ghost.setTelephone("n/a");
    ghost.setEmail("ghost@example.com");
    assertFalse(StaffService.update(ghost));
  }

  @Test
  void delete_nonExisting_returnsFalse_viaService() {
    assertFalse(StaffService.delete(-98765));
  }

  // ---------- helpers using StaffService.getAll() ----------

  private Integer findIdByUsername(String username) {
    return StaffService.getAll().stream()
        .filter(s -> username.equals(s.getUsername()))
        .map(Staff::getId)
        .findFirst()
        .orElse(null);
  }

  private Optional<Staff> findById(int id) {
    List<Staff> all = StaffService.getAll();
    return all.stream().filter(s -> s.getId() == id).findFirst();
  }
}
