package com.pahanaedu.dao;

import com.pahanaedu.model.Order;
import com.pahanaedu.model.OrderDetail;
import com.pahanaedu.util.Db;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private Connection getConn() throws SQLException {
        return Db.get().getConnection();
    }

    // ---------- CREATE ----------

    public int insertOrder(Order o) throws SQLException {
        String sql = "INSERT INTO orders(" +
                     "customer_id, customer_name, customer_code, total_amount, invoice_number" +
                     ") VALUES(?,?,?,?,?)";
        try (Connection c = getConn();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, o.getCustomerId());
            p.setString(2, o.getCustomerName());
            p.setString(3, o.getCustomerCode());
            p.setBigDecimal(4, o.getTotalAmount());
            p.setString(5, o.getInvoiceNumber());
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void insertDetail(OrderDetail d) throws SQLException {
        String sql = "INSERT INTO order_details(" +
                     "order_id, item_type, item_id, item_name, quantity, unit_price, total_price" +
                     ") VALUES(?,?,?,?,?,?,?)";
        try (Connection c = getConn();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, d.getOrderId());
            p.setString(2, d.getItemType());
            p.setInt(3, d.getItemId());
            p.setString(4, d.getItemName());
            p.setInt(5, d.getQuantity());
            p.setBigDecimal(6, d.getUnitPrice());
            p.setBigDecimal(7, d.getTotalPrice());
            p.executeUpdate();
        }
    }

    public void decrementBookStock(int bookId, int qty) throws SQLException {
        try (Connection c = getConn();
             PreparedStatement p = c.prepareStatement(
                 "UPDATE books SET stock = stock - ? WHERE book_id = ?")) {
            p.setInt(1, qty);
            p.setInt(2, bookId);
            p.executeUpdate();
        }
    }

    public void decrementAccessoryStock(int accId, int qty) throws SQLException {
        try (Connection c = getConn();
             PreparedStatement p = c.prepareStatement(
                 "UPDATE accessories SET stock = stock - ? WHERE accessory_id = ?")) {
            p.setInt(1, qty);
            p.setInt(2, accId);
            p.executeUpdate();
        }
    }

    // ---------- READ for Invoice PDF ----------

    public Order getOrderWithDetails(int orderId) {
        String orderSql =
            "SELECT order_id, invoice_number, customer_id, customer_name, customer_code, total_amount, " +
            "       COALESCE(created_at, order_date) AS created_at " +
            "FROM orders WHERE order_id = ?";

        String detailSql =
            "SELECT detail_id, order_id, item_type, item_id, item_name, quantity, unit_price, total_price " +
            "FROM order_details WHERE order_id = ? ORDER BY detail_id";

        try (Connection c = getConn();
             PreparedStatement po = c.prepareStatement(orderSql)) {

            po.setInt(1, orderId);

            Order o = null;
            try (ResultSet rs = po.executeQuery()) {
                if (rs.next()) {
                    o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setInvoiceNumber(rs.getString("invoice_number"));
                    o.setCustomerId(rs.getInt("customer_id"));
                    o.setCustomerName(rs.getString("customer_name"));
                    o.setCustomerCode(rs.getString("customer_code"));
                    o.setTotalAmount(rs.getBigDecimal("total_amount"));
                    try {
                        o.setOrderDate(rs.getTimestamp("created_at"));
                    } catch (SQLException ignore) {
                        o.setOrderDate(null);
                    }
                }
            }
            if (o == null) return null;

            List<OrderDetail> list = new ArrayList<>();
            try (PreparedStatement pd = c.prepareStatement(detailSql)) {
                pd.setInt(1, orderId);
                try (ResultSet rs = pd.executeQuery()) {
                    while (rs.next()) {
                        OrderDetail d = new OrderDetail();
                        d.setDetailId(rs.getInt("detail_id"));
                        d.setOrderId(rs.getInt("order_id"));
                        d.setItemType(rs.getString("item_type"));
                        d.setItemId(rs.getInt("item_id"));
                        d.setItemName(rs.getString("item_name"));
                        d.setQuantity(rs.getInt("quantity"));
                        d.setUnitPrice(rs.getBigDecimal("unit_price"));

                        BigDecimal tp = rs.getBigDecimal("total_price");
                        if (tp == null && d.getUnitPrice() != null) {
                            tp = d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity()));
                        }
                        d.setTotalPrice(tp);

                        list.add(d);
                    }
                }
            }
            o.setDetails(list);
            return o;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

