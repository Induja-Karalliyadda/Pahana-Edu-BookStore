package com.pahanaedu.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.pahanaedu.dao.OnlineOrderDAO;
import com.pahanaedu.model.OnlineOrderItem;

@WebServlet("/api/online-orders")
public class OnlineOrderApiController extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    String action = req.getParameter("action");
    if ("items".equalsIgnoreCase(action)) {
      long orderId = Long.parseLong(req.getParameter("orderId"));
      try {
        List<OnlineOrderItem> items = OnlineOrderDAO.findItems(orderId);
        String json = "{\"items\":[" + items.stream().map(it ->
          String.format("{\"id\":%d,\"order_id\":%d,\"item_type\":\"%s\",\"item_id\":%d,\"item_name\":\"%s\",\"quantity\":%d,\"unit_price\":%s}",
            it.getId(), it.getOrderId(), esc(it.getItemType()), it.getItemId(), esc(it.getItemName()),
            it.getQuantity(), it.getUnitPrice().toPlainString())
        ).collect(Collectors.joining(",")) + "]}";
        resp.getWriter().write(json);
      } catch (SQLException e) {
        e.printStackTrace();
        resp.getWriter().write("{\"items\":[]}");
      }
    } else {
      resp.getWriter().write("{\"items\":[]}");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    try {
      String body = req.getReader().lines().collect(Collectors.joining());
      // naive parse: look for "orderId" and "status"
      long orderId = Long.parseLong(valueOf(body, "orderId"));
      String status  = valueOf(body, "status");
      boolean ok = OnlineOrderDAO.updateStatus(orderId, status);
      if (ok) resp.getWriter().write("{\"status\":\"ok\"}");
      else   resp.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid status or update failed\"}");
    } catch (Exception e) {
      e.printStackTrace();
      resp.getWriter().write("{\"status\":\"error\",\"message\":\"Server error\"}");
    }
  }

  private static String esc(String s) { return s == null ? "" : s.replace("\"","\\\""); }
  private static String valueOf(String json, String key) {
    // VERY simple extractor for {"key":"value"} or {"key":123}
    int i = json.indexOf("\""+key+"\"");
    if (i < 0) return "";
    int colon = json.indexOf(':', i);
    if (colon < 0) return "";
    int start = colon + 1;
    // trim
    while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
    if (start < json.length() && json.charAt(start) == '\"') {
      int end = json.indexOf('\"', start+1);
      return json.substring(start+1, end);
    } else {
      int end = start;
      while (end < json.length() && " ,}]".indexOf(json.charAt(end)) == -1) end++;
      return json.substring(start, end);
    }
  }
}
