package com.pahanaedu.dao;

import com.pahanaedu.model.Accessory;
import com.pahanaedu.model.Book;
import com.pahanaedu.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    // Prefer composition over static DriverManager calls
    private final Db db;

    /** Default constructor uses the shared Db singleton. */
    public ItemDAO() {
        this(Db.get());
    }

    /** Package-private for tests or alternative wiring. */
    ItemDAO(Db db) {
        this.db = db;
    }

    // ---------- Helpers ----------
    private static Book mapBook(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId(rs.getInt("book_id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setCategory(rs.getString("category"));
        b.setDescription(rs.getString("description"));
        b.setSupplier(rs.getString("supplier"));
        b.setCostPrice(rs.getDouble("cost_price"));
        b.setSellingPrice(rs.getDouble("selling_price"));
        b.setStock(rs.getInt("stock"));
        b.setImageUrl(rs.getString("image_url"));
        b.setMinStock(rs.getInt("min_stock"));
        return b;
    }

    private static Accessory mapAccessory(ResultSet rs) throws SQLException {
        Accessory a = new Accessory();
        a.setAccessoryId(rs.getInt("accessory_id"));
        a.setName(rs.getString("name"));
        a.setCategory(rs.getString("category"));
        a.setDescription(rs.getString("description"));
        a.setSupplier(rs.getString("supplier"));
        a.setCostPrice(rs.getDouble("cost_price"));
        a.setSellingPrice(rs.getDouble("selling_price"));
        a.setStock(rs.getInt("stock"));
        a.setImageUrl(rs.getString("image_url"));
        a.setMinStock(rs.getInt("min_stock"));
        return a;
    }

    // ---------- Lists ----------
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY book_id";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(mapBook(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Accessory> getAllAccessories() {
        List<Accessory> accs = new ArrayList<>();
        String sql = "SELECT * FROM accessories ORDER BY accessory_id";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) accs.add(mapAccessory(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accs;
    }

    // ---------- Books ----------
    public List<Book> findBooks(String titlePattern, String categoryFilter) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books " +
                     "WHERE title LIKE ? AND ( ? = '' OR category = ? ) " +
                     "ORDER BY book_id";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + (titlePattern == null ? "" : titlePattern) + "%");
            ps.setString(2, categoryFilter == null ? "" : categoryFilter);
            ps.setString(3, categoryFilter == null ? "" : categoryFilter);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapBook(rs));
            }
        }
        return list;
    }

    public void saveBook(Book b) throws SQLException {
        String sql = "INSERT INTO books " +
                     "(title, author, category, description, supplier, cost_price, selling_price, stock, image_url, min_stock) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getCategory());
            ps.setString(4, b.getDescription());
            ps.setString(5, b.getSupplier());
            ps.setDouble(6, b.getCostPrice());
            ps.setDouble(7, b.getSellingPrice());
            ps.setInt(8, b.getStock());
            ps.setString(9, b.getImageUrl());
            ps.setInt(10, b.getMinStock());
            ps.executeUpdate();
        }
    }

    public void updateBook(Book b) throws SQLException {
        String sql = "UPDATE books SET " +
                     "title=?, author=?, category=?, description=?, supplier=?, " +
                     "cost_price=?, selling_price=?, stock=?, image_url=?, min_stock=? " +
                     "WHERE book_id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getCategory());
            ps.setString(4, b.getDescription());
            ps.setString(5, b.getSupplier());
            ps.setDouble(6, b.getCostPrice());
            ps.setDouble(7, b.getSellingPrice());
            ps.setInt(8, b.getStock());
            ps.setString(9, b.getImageUrl());
            ps.setInt(10, b.getMinStock());
            ps.setInt(11, b.getBookId());
            ps.executeUpdate();
        }
    }

    public Book getBookById(int bookId) throws SQLException {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBook(rs);
            }
        }
        return null;
    }

    public void deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        }
    }

    // ---------- Accessories ----------
    public List<Accessory> findAccessories(String namePattern, String categoryFilter) throws SQLException {
        List<Accessory> list = new ArrayList<>();
        String sql = "SELECT * FROM accessories " +
                     "WHERE name LIKE ? AND ( ? = '' OR category = ? ) " +
                     "ORDER BY accessory_id";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + (namePattern == null ? "" : namePattern) + "%");
            ps.setString(2, categoryFilter == null ? "" : categoryFilter);
            ps.setString(3, categoryFilter == null ? "" : categoryFilter);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapAccessory(rs));
            }
        }
        return list;
    }

    public void saveAccessory(Accessory a) throws SQLException {
        String sql = "INSERT INTO accessories " +
                     "(name, category, description, supplier, cost_price, selling_price, stock, image_url, min_stock) " +
                     "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getName());
            ps.setString(2, a.getCategory());
            ps.setString(3, a.getDescription());
            ps.setString(4, a.getSupplier());
            ps.setDouble(5, a.getCostPrice());
            ps.setDouble(6, a.getSellingPrice());
            ps.setInt(7, a.getStock());
            ps.setString(8, a.getImageUrl());
            ps.setInt(9, a.getMinStock());
            ps.executeUpdate();
        }
    }

    public void updateAccessory(Accessory a) throws SQLException {
        String sql = "UPDATE accessories SET " +
                     "name=?, category=?, description=?, supplier=?, cost_price=?, " +
                     "selling_price=?, stock=?, image_url=?, min_stock=? " +
                     "WHERE accessory_id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getName());
            ps.setString(2, a.getCategory());
            ps.setString(3, a.getDescription());
            ps.setString(4, a.getSupplier());
            ps.setDouble(5, a.getCostPrice());
            ps.setDouble(6, a.getSellingPrice());
            ps.setInt(7, a.getStock());
            ps.setString(8, a.getImageUrl());
            ps.setInt(9, a.getMinStock());
            ps.setInt(10, a.getAccessoryId());
            ps.executeUpdate();
        }
    }

    public Accessory getAccessoryById(int accessoryId) throws SQLException {
        String sql = "SELECT * FROM accessories WHERE accessory_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, accessoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAccessory(rs);
            }
        }
        return null;
    }

    public void deleteAccessory(int accessoryId) throws SQLException {
        String sql = "DELETE FROM accessories WHERE accessory_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, accessoryId);
            ps.executeUpdate();
        }
    }

    // ---------- Health check ----------
    public static boolean testConnection() {
        return Db.get().test();
    }
}

