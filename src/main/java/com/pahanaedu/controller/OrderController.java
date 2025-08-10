package com.pahanaedu.controller;

import com.pahanaedu.dao.OrderDAO;
import com.pahanaedu.model.Order;
import com.pahanaedu.model.OrderDetail;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/OrderController")
public class OrderController extends HttpServlet {
    private Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            // Parse JSON request
            JsonObject requestData = gson.fromJson(request.getReader(), JsonObject.class);
            
            int customerId = requestData.get("customerId").getAsInt();
            String customerCode = requestData.get("customerCode").getAsString();
            String customerName = requestData.get("customerName").getAsString();
            BigDecimal total = requestData.get("total").getAsBigDecimal();
            
            // Generate invoice number
            String invoiceNumber = "INV" + System.currentTimeMillis();
            
            // Create order
            Order order = new Order();
            order.setCustomerId(customerId);
            order.setCustomerCode(customerCode);
            order.setCustomerName(customerName);
            order.setTotalAmount(total);
            order.setInvoiceNumber(invoiceNumber);
            
            // Parse cart items
            List<OrderDetail> details = new ArrayList<>();
            JsonArray cartArray = requestData.getAsJsonArray("cart");
            
            for (int i = 0; i < cartArray.size(); i++) {
                JsonObject item = cartArray.get(i).getAsJsonObject();
                
                OrderDetail detail = new OrderDetail();
                detail.setItemType(item.get("type").getAsString());
                detail.setItemId(item.get("id").getAsInt());
                detail.setItemName(item.get("name").getAsString());
                detail.setQuantity(item.get("qty").getAsInt());
                detail.setUnitPrice(item.get("price").getAsBigDecimal());
                detail.setTotalPrice(detail.getUnitPrice().multiply(
                    BigDecimal.valueOf(detail.getQuantity())));
                
                details.add(detail);
            }
            
            order.setDetails(details);
            
            // Save order
            OrderDAO orderDao = new OrderDAO();
            int orderId = orderDao.insertOrder(order);
            
            // Save order details and update stock
            for (OrderDetail detail : details) {
                detail.setOrderId(orderId);
                orderDao.insertDetail(detail);
                
                // Decrement stock
                if ("book".equals(detail.getItemType())) {
                    orderDao.decrementBookStock(detail.getItemId(), detail.getQuantity());
                } else {
                    orderDao.decrementAccessoryStock(detail.getItemId(), detail.getQuantity());
                }
            }
            
            // Return success response
            JsonObject response_json = new JsonObject();
            response_json.addProperty("status", "ok");
            response_json.addProperty("orderId", orderId);
            response_json.addProperty("invoiceNumber", invoiceNumber);
            
            out.print(response_json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", e.getMessage());
            out.print(error.toString());
        }
    }
}