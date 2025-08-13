package com.pahanaedu.controller;

import com.pahanaedu.model.Book;
import com.pahanaedu.model.Accessory;
import com.pahanaedu.service.ItemService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/ItemStockController")
public class ItemStockController extends HttpServlet {
  private final ItemService itemService = new ItemService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setCharacterEncoding("UTF-8");
    resp.setContentType("application/json");
    resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");

    try (PrintWriter out = resp.getWriter()) {
      List<Book> books = itemService.getBooks(null, null);
      List<Accessory> accessories = itemService.getAccessories(null, null);

      StringBuilder json = new StringBuilder();
      json.append("{\"books\":[");
      for (int i = 0; i < books.size(); i++) {
        Book b = books.get(i);
        if (i > 0) json.append(',');
        json.append("{\"id\":").append(b.getBookId())
            .append(",\"stock\":").append(b.getStock())
            .append(",\"minStock\":").append(b.getMinStock())
            .append('}');
      }
      json.append("],\"accessories\":[");
      for (int i = 0; i < accessories.size(); i++) {
        Accessory a = accessories.get(i);
        if (i > 0) json.append(',');
        json.append("{\"id\":").append(a.getAccessoryId())
            .append(",\"stock\":").append(a.getStock())
            .append(",\"minStock\":").append(a.getMinStock())
            .append('}');
      }
      json.append("]}");

      out.print(json.toString());
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(500);
      resp.getWriter().print("{\"books\":[],\"accessories\":[],\"error\":\"" + e.getClass().getSimpleName() + "\"}");
    }
  }
}

