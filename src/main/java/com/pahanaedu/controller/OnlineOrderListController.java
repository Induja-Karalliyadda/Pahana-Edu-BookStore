package com.pahanaedu.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pahanaedu.dao.OnlineOrderDAO;
import com.pahanaedu.model.OnlineOrder;

@WebServlet("/online-orders")
public class OnlineOrderListController extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      List<OnlineOrder> orders = OnlineOrderDAO.findAll();
      req.setAttribute("orders", orders);
      // CHANGE THIS PATH if your JSP lives somewhere else:
   // OnlineOrderListController.java
   // OnlineOrderListController.java
      req.getRequestDispatcher("/view/OnlineOrders.jsp").forward(req, resp);

      // e.g. if it's directly under webapp: req.getRequestDispatcher("/OnlineOrders.jsp").forward(req, resp);
    } catch (Exception e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to load online orders");
    }
  }
}

