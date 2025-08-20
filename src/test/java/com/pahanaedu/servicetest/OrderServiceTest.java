package com.pahanaedu.servicetest;

import com.pahanaedu.dao.OrderDAO;
import com.pahanaedu.model.Order;
import com.pahanaedu.model.OrderDetail;
import com.pahanaedu.service.OrderService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

  // ---------- Happy path ----------

  @Test
  void placeOrder_insertsEverything_decrementsStocks_setsOrderId() throws Exception {
    // Arrange
    OrderService svc = new OrderService();
    RecordingDao fake = new RecordingDao();
    injectDao(svc, fake);

    Order order = orderWith(
        detail("book",  10, 2),
        detail("accessory", 7, 5)
    );

    // Act
    int returnedId = svc.placeOrder(order);

    // Assert
    assertEquals(fake.nextId, returnedId);
    assertSame(order, fake.insertedOrder, "Service must pass the same Order to DAO.insertOrder");

    // details were inserted and received the generated order id
    assertEquals(2, fake.insertedDetails.size());
    assertTrue(fake.insertedDetails.stream().allMatch(d -> d.getOrderId() == fake.nextId),
        "Service must set orderId on each detail before insert");

    // stock decrements happened correctly on each branch
    assertEquals(10, fake.decBookItemId);
    assertEquals(2,  fake.decBookQty);
    assertEquals(7,  fake.decAccItemId);
    assertEquals(5,  fake.decAccQty);
  }

  // ---------- Exceptions ----------

  @Test
  void placeOrder_whenInsertOrderThrows_bubblesException() throws Exception {
    OrderService svc = new OrderService();
    OrderDAO fake = new OrderDaoInsertThrows();
    injectDao(svc, fake);

    Order order = orderWith(detail("book", 1, 1));

    Exception ex = assertThrows(Exception.class, () -> svc.placeOrder(order));
    assertTrue(ex.getMessage().toLowerCase().contains("boom"));
  }

  @Test
  void placeOrder_whenSecondDetailInsertThrows_firstBranchStillProcessed_thenBubbles() throws Exception {
    OrderService svc = new OrderService();
    RecordingDaoWithSecondDetailFailure fake = new RecordingDaoWithSecondDetailFailure();
    injectDao(svc, fake);

    // two details: after the first ("book") succeeds, the second throws
    Order order = orderWith(
        detail("book", 3, 2),
        detail("accessory", 9, 4)
    );

    Exception ex = assertThrows(Exception.class, () -> svc.placeOrder(order));
    assertTrue(ex.getMessage().toLowerCase().contains("detail"));

    // verify the first detail completed (insert + stock decrement) before the exception
    assertEquals(1, fake.insertedDetails.size(), "Only first detail should have been inserted");
    assertEquals(3, fake.decBookItemId);
    assertEquals(2, fake.decBookQty);
    // accessory path should not have run due to the thrown exception on second insert
    assertEquals(-1, fake.decAccItemId);
    assertEquals(0,  fake.decAccQty);
  }

  // ---------- Helpers ----------

  private static Order orderWith(OrderDetail... details) {
    Order o = new Order();
    List<OrderDetail> list = new ArrayList<>();
    for (OrderDetail d : details) list.add(d);
    // assume Order has a setter; if not, adapt to your model (e.g., o.getDetails().addAll(list))
    o.setDetails(list);
    return o;
  }

  private static OrderDetail detail(String type, int itemId, int qty) {
    OrderDetail d = new OrderDetail();
    d.setItemType(type);
    d.setItemId(itemId);
    d.setQuantity(qty);
    return d;
  }

  /** Replace the private 'dao' field without extra libraries (it's not final). */
  private static void injectDao(OrderService svc, OrderDAO replacement) throws Exception {
    Field f = OrderService.class.getDeclaredField("dao");
    f.setAccessible(true);
    f.set(svc, replacement);
  }

  // ---------- Fakes ----------

  /** Records all calls and returns a fixed order id. */
  static class RecordingDao extends OrderDAO {
    int nextId = 123;
    Order insertedOrder;
    List<OrderDetail> insertedDetails = new ArrayList<>();
    int decBookItemId = -1, decBookQty = 0;
    int decAccItemId  = -1, decAccQty  = 0;

    @Override public int insertOrder(Order o) { this.insertedOrder = o; return nextId; }
    @Override public void insertDetail(OrderDetail d) { insertedDetails.add(d); }
    @Override public void decrementBookStock(int bookId, int qty) { decBookItemId = bookId; decBookQty = qty; }
    @Override public void decrementAccessoryStock(int accId, int qty) { decAccItemId = accId; decAccQty = qty; }
  }

  /** Throws from insertOrder to test exception bubbling. */
  static class OrderDaoInsertThrows extends OrderDAO {
    @Override public int insertOrder(Order o) throws java.sql.SQLException {
      throw new java.sql.SQLException("boom on insertOrder");
    }
  }

  /** Succeeds on first detail insert, throws on second. */
  static class RecordingDaoWithSecondDetailFailure extends RecordingDao {
	  int detailCount = 0;

	  @Override
	  public void insertDetail(OrderDetail d) {          // <- no 'throws'
	    detailCount++;
	    if (detailCount == 2) {
	      throw new RuntimeException("detail insert failed");  // unchecked
	    }
	    super.insertDetail(d);
	  }
	}

}
