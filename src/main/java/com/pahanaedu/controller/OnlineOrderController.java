package com.pahanaedu.controller;

import com.pahanaedu.model.CartItem;
import com.pahanaedu.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

@WebServlet("/OnlineOrderController")
public class OnlineOrderController extends HttpServlet {

    private static final String URL  = "jdbc:mysql://localhost:3306/pahana_edu_book_store";
    private static final String USER = "root";
    private static final String PASS = "1234";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession s = req.getSession(false);
        User u = (s != null) ? (User) s.getAttribute("user") : null;
        if (u == null) { resp.sendRedirect(req.getContextPath()+"/login.jsp"); return; }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) s.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            s.setAttribute("flash", "Your cart is empty.");
            resp.sendRedirect(req.getContextPath()+"/cart");
            return;
        }

        // read (prefilled) customer fields (allow edits)
        String customerName = empty(req.getParameter("customerName"), u.getUsername());
        String phone        = empty(req.getParameter("phone"), u.getTelephone());
        String email        = empty(req.getParameter("email"), u.getEmail());
        String address      = empty(req.getParameter("address"), u.getAddress());

        try (Connection c = getConn()) {
            c.setAutoCommit(false);
            try {
                // 1) lock and validate stock, and compute total with current prices
                double total = 0.0;
                for (CartItem ci : cart) {
                    if ("book".equals(ci.getItemType())) {
                        try (PreparedStatement ps = c.prepareStatement(
                                "SELECT selling_price, stock, title FROM books WHERE book_id=? FOR UPDATE")) {
                            ps.setInt(1, ci.getItemId());
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) throw new SQLException("Book not found: " + ci.getItemId());
                                double price = rs.getDouble("selling_price");
                                int stock    = rs.getInt("stock");
                                if (stock < ci.getQuantity()) throw new SQLException("Not enough stock for book: " + rs.getString("title"));
                                total += price * ci.getQuantity();
                            }
                        }
                    } else { // accessory
                        try (PreparedStatement ps = c.prepareStatement(
                                "SELECT selling_price, stock, name FROM accessories WHERE accessory_id=? FOR UPDATE")) {
                            ps.setInt(1, ci.getItemId());
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) throw new SQLException("Accessory not found: " + ci.getItemId());
                                double price = rs.getDouble("selling_price");
                                int stock    = rs.getInt("stock");
                                if (stock < ci.getQuantity()) throw new SQLException("Not enough stock for accessory: " + rs.getString("name"));
                                total += price * ci.getQuantity();
                            }
                        }
                    }
                }

                // 2) insert header
                long orderId;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO online_orders (customer_name, phone, email, address, total_amount, status) " +
                        "VALUES (?,?,?,?,?,'PENDING')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, customerName);
                    ps.setString(2, phone);
                    ps.setString(3, email);
                    ps.setString(4, address);
                    ps.setBigDecimal(5, java.math.BigDecimal.valueOf(total));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); orderId = keys.getLong(1); }
                }

                // 3) insert lines + decrement stock using current DB price/name
                for (CartItem ci : cart) {
                    String selectSql, updateSql, nameCol, idCol;
                    if ("book".equals(ci.getItemType())) {
                        selectSql = "SELECT title AS nm, selling_price AS pr FROM books WHERE book_id=?";
                        updateSql = "UPDATE books SET stock = stock - ? WHERE book_id=?";
                        nameCol = "nm"; idCol="book_id";
                    } else {
                        selectSql = "SELECT name AS nm, selling_price AS pr FROM accessories WHERE accessory_id=?";
                        updateSql = "UPDATE accessories SET stock = stock - ? WHERE accessory_id=?";
                        nameCol = "nm"; idCol="accessory_id";
                    }

                    String dbName; double dbPrice;
                    try (PreparedStatement ps = c.prepareStatement(selectSql)) {
                        ps.setInt(1, ci.getItemId());
                        try (ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            dbName  = rs.getString("nm");
                            dbPrice = rs.getDouble("pr");
                        }
                    }

                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO online_order_items (order_id, item_type, item_id, item_name, quantity, unit_price) " +
                            "VALUES (?,?,?,?,?,?)")) {
                        ps.setLong(1, orderId);
                        ps.setString(2, ci.getItemType());
                        ps.setInt(3, ci.getItemId());
                        ps.setString(4, dbName);
                        ps.setInt(5, ci.getQuantity());
                        ps.setBigDecimal(6, java.math.BigDecimal.valueOf(dbPrice));
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = c.prepareStatement(updateSql)) {
                        ps.setInt(1, ci.getQuantity());
                        ps.setInt(2, ci.getItemId());
                        ps.executeUpdate();
                    }
                }

                c.commit();

                // 4) clear cart + badge, flash success and go to cart (now empty)
                s.setAttribute("cart", new java.util.ArrayList<CartItem>());
                s.setAttribute("cartItemCount", 0);
                s.setAttribute("flash", "✅ Order placed! Your order ID is #" + orderId + ".");
                resp.sendRedirect(req.getContextPath() + "/cart");
            } catch (Exception e) {
                c.rollback();
                e.printStackTrace();
                s.setAttribute("flash", "❌ Order failed: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/cart");
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            s.setAttribute("flash", "❌ Database error: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private Connection getConn() throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException ignore) {}
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static String empty(String v, String fallback) {
        return (v == null || v.trim().isEmpty()) ? (fallback == null ? "" : fallback) : v.trim();
    }
}
