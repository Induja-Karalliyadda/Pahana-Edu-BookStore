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
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String query = request.getParameter("q");
        if (query == null) query = "";
        query = query.trim();

        try (PrintWriter out = response.getWriter()) {
            UserDAO userDao = new UserDAO();
            List<User> customers = userDao.searchUsers(query);
            out.print(gson.toJson(customers));
        } catch (Exception e) {
            e.printStackTrace();
            try (PrintWriter out = response.getWriter()) {
                out.print("[]");
            }
        }
    }
}
