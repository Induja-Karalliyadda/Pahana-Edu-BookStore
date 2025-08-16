package com.pahanaedu.controller;

import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.Book;
import com.pahanaedu.model.Accessory;
import com.pahanaedu.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/AdminPOSController")
public class AdminPOSController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	 System.out.println("AdminPOSController loaded!");
        
        try {
            ItemDAO itemDao = new ItemDAO();
            UserDAO userDao = new UserDAO();
            
            // Get all books and accessories
            List<Book> books = itemDao.getAllBooks();
            List<Accessory> accessories = itemDao.getAllAccessories();
            
            // Get all customers using your existing method
            List<User> customers = userDao.findAllUsers();
            
            request.setAttribute("books", books);
            request.setAttribute("accessories", accessories);
            request.setAttribute("customers", customers);
            
            request.getRequestDispatcher("view/AdminDashboard.jsp").forward(request, response);

            
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load POS data");
        }
    }
}