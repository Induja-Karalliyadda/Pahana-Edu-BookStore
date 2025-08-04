package com.pahanaedu.service;

import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.model.Book;
import com.pahanaedu.model.Accessory;

import java.util.Collections;
import java.util.List;

public class ItemService {
    private final ItemDAO dao;

    public ItemService() {
        dao = new ItemDAO();
    }

    // BOOKS
    public List<Book> getBooks(String titlePattern, String categoryFilter) {
        try {
            if (titlePattern == null) titlePattern = "";
            if (categoryFilter == null) categoryFilter = "";
            return dao.findBooks(titlePattern, categoryFilter);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Book getBookById(int id) {
        try {
            return dao.getBookById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addBook(Book book) {
        try {
            dao.saveBook(book);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBook(Book book) {
        try {
            dao.updateBook(book);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(int id) {
        try {
            dao.deleteBook(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ACCESSORIES
    public List<Accessory> getAccessories(String namePattern, String categoryFilter) {
        try {
            if (namePattern == null) namePattern = "";
            if (categoryFilter == null) categoryFilter = "";
            return dao.findAccessories(namePattern, categoryFilter);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Accessory getAccessoryById(int id) {
        try {
            return dao.getAccessoryById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addAccessory(Accessory accessory) {
        try {
            dao.saveAccessory(accessory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAccessory(Accessory accessory) {
        try {
            dao.updateAccessory(accessory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAccessory(int id) {
        try {
            dao.deleteAccessory(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


