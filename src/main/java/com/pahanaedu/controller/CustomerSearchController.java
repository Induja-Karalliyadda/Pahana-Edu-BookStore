package com.pahanaedu.controller;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/CustomerSearchController")
public class CustomerSearchController extends HttpServlet {
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            String query = request.getParameter("q");
            if (query == null) query = "";
            
            UserDAO userDao = new UserDAO();
            List<User> customers = userDao.searchUsers(query);
            
            out.print(gson.toJson(customers));
            
        } catch (Exception e) {
            e.printStackTrace();
            out.print("[]");
        }
    }
}