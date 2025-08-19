package com.pahanaedu.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/sales-report")
public class SalesReportController extends HttpServlet {

    // TODO: swap to your DataSource/JNDI if you have one configured.
    private Connection getConnection() throws SQLException {
        // Make sure the driver is on classpath
        // Class.forName("com.mysql.cj.jdbc.Driver"); // if needed
        String url = "jdbc:mysql://localhost:3306/pahana_edu_book_store?useSSL=false&serverTimezone=UTC";
        return DriverManager.getConnection(url, "root", "1234");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fmt = Optional.ofNullable(req.getParameter("format")).orElse("page");
        LocalDate day = Optional.ofNullable(req.getParameter("day"))
                .map(LocalDate::parse).orElse(LocalDate.now());

        Map<String, Object> summary = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();

        try (Connection con = getConnection()) {
            // --- summary
            String sumSql =
                "SELECT COUNT(DISTINCT o.order_id) AS orders_count, " +
                "       IFNULL(SUM(od.quantity), 0) AS units_sold, " +
                "       IFNULL(SUM(od.total_price), 0) AS revenue, " +
                "       IFNULL(SUM(od.quantity * (od.unit_price - COALESCE(CASE " +
                "             WHEN od.item_type='book' THEN b.cost_price " +
                "             WHEN od.item_type='accessory' THEN a.cost_price " +
                "             ELSE 0 END,0))),0) AS profit " +
                "FROM orders o " +
                "JOIN order_details od ON od.order_id = o.order_id " +
                "LEFT JOIN books b        ON od.item_type='book'      AND od.item_id=b.book_id " +
                "LEFT JOIN accessories a  ON od.item_type='accessory' AND od.item_id=a.accessory_id " +
                "WHERE DATE(o.order_date) = ?";
            try (PreparedStatement ps = con.prepareStatement(sumSql)) {
                ps.setDate(1, java.sql.Date.valueOf(day));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        summary.put("orders",   rs.getInt("orders_count"));
                        summary.put("units",    rs.getInt("units_sold"));
                        summary.put("revenue",  rs.getBigDecimal("revenue"));
                        summary.put("profit",   rs.getBigDecimal("profit"));
                        summary.put("day",      day.toString());
                    }
                }
            }

            // --- items
            String itemsSql =
                "SELECT od.item_type, od.item_id, od.item_name, " +
                "       SUM(od.quantity) AS qty, " +
                "       SUM(od.total_price) AS revenue, " +
                "       SUM(od.quantity * (od.unit_price - COALESCE(CASE " +
                "           WHEN od.item_type='book' THEN b.cost_price " +
                "           WHEN od.item_type='accessory' THEN a.cost_price " +
                "           ELSE 0 END,0))) AS profit " +
                "FROM orders o " +
                "JOIN order_details od ON od.order_id = o.order_id " +
                "LEFT JOIN books b        ON od.item_type='book'      AND od.item_id=b.book_id " +
                "LEFT JOIN accessories a  ON od.item_type='accessory' AND od.item_id=a.accessory_id " +
                "WHERE DATE(o.order_date) = ? " +
                "GROUP BY od.item_type, od.item_id, od.item_name " +
                "ORDER BY revenue DESC";
            try (PreparedStatement ps = con.prepareStatement(itemsSql)) {
                ps.setDate(1, java.sql.Date.valueOf(day));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("type",    rs.getString("item_type"));
                        row.put("id",      rs.getInt("item_id"));
                        row.put("name",    rs.getString("item_name"));
                        row.put("qty",     rs.getInt("qty"));
                        row.put("revenue", rs.getBigDecimal("revenue"));
                        row.put("profit",  rs.getBigDecimal("profit"));
                        items.add(row);
                    }
                }
            }

        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

        if ("json".equalsIgnoreCase(fmt)) {
            resp.setContentType("application/json; charset=UTF-8");
            try (PrintWriter out = resp.getWriter()) {
                out.print(toJson(summary, items));
            }
        } else {
            req.setAttribute("summary", summary);
            req.setAttribute("items", items);
            req.getRequestDispatcher("/view/sales-report.jsp").forward(req, resp);

        }
    }

    // tiny JSON builder (no external deps)
    private String toJson(Map<String,Object> summary, List<Map<String,Object>> items){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"summary\":{");
        sb.append("\"day\":\"").append(summary.getOrDefault("day","")).append("\",");
        sb.append("\"orders\":").append(summary.getOrDefault("orders",0)).append(",");
        sb.append("\"units\":").append(summary.getOrDefault("units",0)).append(",");
        sb.append("\"revenue\":").append(number(summary.get("revenue"))).append(",");
        sb.append("\"profit\":").append(number(summary.get("profit")));
        sb.append("},\"items\":[");
        for (int i=0;i<items.size();i++){
            Map<String,Object> r = items.get(i);
            if (i>0) sb.append(",");
            sb.append("{")
              .append("\"type\":\"").append(r.get("type")).append("\",")
              .append("\"id\":").append(r.get("id")).append(",")
              .append("\"name\":\"").append(escape(String.valueOf(r.get("name")))).append("\",")
              .append("\"qty\":").append(r.get("qty")).append(",")
              .append("\"revenue\":").append(number(r.get("revenue"))).append(",")
              .append("\"profit\":").append(number(r.get("profit")))
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }
    private String escape(String s){ return s.replace("\"","\\\""); }
    private String number(Object o){
        return (o==null) ? "0" : String.valueOf(o);
    }
}
