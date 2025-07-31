package com.pahanaedu.service;

import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.model.Book;
import com.pahanaedu.model.Accessory;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ItemService {
    private final ItemDAO itemDAO;

    public ItemService() {
        this.itemDAO = new ItemDAO();
    }

    public List<Book> getBooks(String name, String category) {
        try {
            return itemDAO.findBooks(name == null ? "" : name, category == null ? "" : category);
        } catch (SQLException e) {
            System.err.println("Error getting books: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<String> getBookCategories() {
        try {
            return itemDAO.findAllBookCategories();
        } catch (SQLException e) {
            System.err.println("Error getting book categories: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Accessory> getAccessories(String name, String category) {
        try {
            return itemDAO.findAccessories(name == null ? "" : name, category == null ? "" : category);
        } catch (SQLException e) {
            System.err.println("Error getting accessories: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Book getBookById(int id) {
        try {
            return itemDAO.getBookById(id);
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Accessory getAccessoryById(int id) {
        try {
            return itemDAO.getAccessoryById(id);
        } catch (SQLException e) {
            System.err.println("Error getting accessory by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean addBook(Book book) {
        try {
            itemDAO.saveBook(book);
            System.out.println("Book saved successfully: " + book.getTitle());
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBook(Book book) {
        try {
            itemDAO.updateBook(book);
            System.out.println("Book updated successfully: " + book.getTitle());
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(int id) {
        try {
            itemDAO.deleteBook(id);
            System.out.println("Book deleted successfully with ID: " + id);
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addAccessory(Accessory acc) {
        try {
            itemDAO.saveAccessory(acc);
            System.out.println("Accessory saved successfully: " + acc.getName());
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding accessory: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAccessory(Accessory acc) {
        try {
            itemDAO.updateAccessory(acc);
            System.out.println("Accessory updated successfully: " + acc.getName());
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating accessory: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAccessory(int id) {
        try {
            itemDAO.deleteAccessory(id);
            System.out.println("Accessory deleted successfully with ID: " + id);
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting accessory: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

