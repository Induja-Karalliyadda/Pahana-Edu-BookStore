package com.pahanaedu.dao;

import com.pahanaedu.model.Order;
import com.pahanaedu.model.OrderDetail;
import java.sql.*;

public class OrderDAO {
    private Connection getConn() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/pahana_edu_book_store",
            "root", "1234");
    }

    public int insertOrder(Order o) throws SQLException {
        String sql = "INSERT INTO orders(customer_id,customer_name,customer_code,total_amount,invoice_number) VALUES(?,?,?,?,?)";
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
        String sql = "INSERT INTO order_details(order_id,item_type,item_id,item_name,quantity,unit_price,total_price) VALUES(?,?,?,?,?,?,?)";
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
                 "UPDATE books SET stock=stock-? WHERE book_id=?")) {
            p.setInt(1, qty);
            p.setInt(2, bookId);
            p.executeUpdate();
        }
    }

    public void decrementAccessoryStock(int accId, int qty) throws SQLException {
        try (Connection c = getConn();
             PreparedStatement p = c.prepareStatement(
                 "UPDATE accessories SET stock=stock-? WHERE accessory_id=?")) {
            p.setInt(1, qty);
            p.setInt(2, accId);
            p.executeUpdate();
        }
    }
}
