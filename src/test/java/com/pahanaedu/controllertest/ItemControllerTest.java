package com.pahanaedu.controllertest;

import com.pahanaedu.controller.ItemController;
import com.pahanaedu.model.Book;
import com.pahanaedu.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

  /** Make doGet/doPost public so we can call them from a different package. */
  static class TestableItemController extends ItemController {
    @Override public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws javax.servlet.ServletException, java.io.IOException { super.doGet(req, res); }
    @Override public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws javax.servlet.ServletException, java.io.IOException { super.doPost(req, res); }
  }

  @InjectMocks private TestableItemController controller;
  @Mock private ItemService itemService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private RequestDispatcher dispatcher;

  // ---------- doGet: list books -> forward ----------

  @Test
  void doGet_books_setsAttributes_and_forwards() throws Exception {
    when(request.getParameter("searchName")).thenReturn(null);
    when(request.getParameter("searchCategory")).thenReturn(null);

    when(request.getParameter("mainCategory")).thenReturn("book");
    when(request.getRequestDispatcher("/view/Item.jsp")).thenReturn(dispatcher);

    List<Book> books = List.of(book(1, "Clean Code"));
    when(itemService.getBooks(any(), any())).thenReturn(books);

    controller.doGet(request, response);

    verify(itemService).getBooks(any(), any());
    verify(request).setAttribute(eq("books"), eq(books));
    verify(request).setAttribute(eq("bookCategories"), any());
    verify(request).setAttribute("mainCategory", "book");
    verify(dispatcher).forward(request, response);
    verify(response, never()).sendRedirect(anyString());
  }

  // ---------- doGet: edit book -> sets editBook and forwards ----------

  @Test
  void doGet_editBook_putsEditBook_in_request() throws Exception {
    when(request.getParameter("searchName")).thenReturn(null);
    when(request.getParameter("searchCategory")).thenReturn(null);

    when(request.getParameter("mainCategory")).thenReturn("book");
    when(request.getParameter("action")).thenReturn("edit");
    when(request.getParameter("type")).thenReturn("book");
    when(request.getParameter("id")).thenReturn("5");
    when(request.getRequestDispatcher("/view/Item.jsp")).thenReturn(dispatcher);

    when(itemService.getBookById(5)).thenReturn(book(5, "Refactoring"));

    controller.doGet(request, response);

    verify(itemService).getBookById(5);
    verify(request).setAttribute(eq("editBook"), argThat(o -> ((Book)o).getBookId() == 5));
    verify(dispatcher).forward(request, response);
  }

  // ---------- doGet: delete accessory -> redirect on success ----------

  @Test
  void doGet_deleteAccessory_redirects_on_success() throws Exception {
    when(request.getParameter("searchName")).thenReturn(null);
    when(request.getParameter("searchCategory")).thenReturn(null);

    when(request.getParameter("mainCategory")).thenReturn("accessory");
    when(request.getParameter("action")).thenReturn("delete");
    when(request.getParameter("type")).thenReturn("accessory");
    when(request.getParameter("id")).thenReturn("9");
    when(request.getContextPath()).thenReturn("/app");

    when(itemService.deleteAccessory(9)).thenReturn(true);

    controller.doGet(request, response);

    verify(itemService).deleteAccessory(9);

    ArgumentCaptor<String> urlCap = ArgumentCaptor.forClass(String.class);
    verify(response).sendRedirect(urlCap.capture());
    assertTrue(urlCap.getValue().endsWith("/items?mainCategory=accessory&success=deleted"));
    verifyNoInteractions(dispatcher);
  }

  // ---------- doPost: create book -> redirect with success=1 ----------

  @Test
  void doPost_createBook_success_redirects() throws Exception {
    TestableItemController spyCtrl = spy(controller);

    ServletContext ctx = mock(ServletContext.class);
    when(ctx.getRealPath("")).thenReturn(System.getProperty("java.io.tmpdir"));
    doReturn(ctx).when(spyCtrl).getServletContext();

    when(request.getParameter("mainCategory")).thenReturn("book");
    when(request.getParameter("action")).thenReturn("create");
    when(request.getContextPath()).thenReturn("/app");

    when(request.getParameter("title")).thenReturn("New Book");
    when(request.getParameter("author")).thenReturn("Author");
    when(request.getParameter("bookSub")).thenReturn("Fiction");
    when(request.getParameter("descriptionBook")).thenReturn("desc");
    when(request.getParameter("supplierBook")).thenReturn("Supp");
    when(request.getParameter("minStockBook")).thenReturn("1");
    when(request.getParameter("costPriceBook")).thenReturn("10");
    when(request.getParameter("sellingPriceBook")).thenReturn("12");
    when(request.getParameter("stockBook")).thenReturn("3");
    when(request.getPart("imgBook")).thenReturn(null);

    when(itemService.addBook(any(Book.class))).thenReturn(true);

    spyCtrl.doPost(request, response);

    verify(itemService).addBook(any(Book.class));
    ArgumentCaptor<String> urlCap = ArgumentCaptor.forClass(String.class);
    verify(response).sendRedirect(urlCap.capture());
    assertTrue(urlCap.getValue().endsWith("/items?mainCategory=book&success=1"));
  }

  // ---------- intentionally failing demo (1 red) ----------

  @Test
  void failing_demo_expectRedirectButItForwards() throws Exception {
    when(request.getParameter("searchName")).thenReturn(null);
    when(request.getParameter("searchCategory")).thenReturn(null);
    when(request.getParameter("mainCategory")).thenReturn("book");
    when(request.getRequestDispatcher("/view/Item.jsp")).thenReturn(dispatcher);
    when(itemService.getBooks(any(), any())).thenReturn(List.of());

    controller.doGet(request, response);

    // WRONG on purpose: doGet for "book" forwards, not redirects
    verify(response).sendRedirect(anyString());
  }

  // ---------- helper ----------

  private static Book book(int id, String title) {
    Book b = new Book();
    b.setBookId(id);
    b.setTitle(title);
    return b;
  }
}


