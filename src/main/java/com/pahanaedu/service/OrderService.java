package com.pahanaedu.service;

import com.pahanaedu.dao.OrderDAO;
import com.pahanaedu.model.Order;
import com.pahanaedu.model.OrderDetail;

public class OrderService {
    private OrderDAO dao = new OrderDAO();

    public int placeOrder(Order o) throws Exception {
        int orderId = dao.insertOrder(o);
        for (OrderDetail d : o.getDetails()) {
            d.setOrderId(orderId);
            dao.insertDetail(d);
            if ("book".equals(d.getItemType())) {
                dao.decrementBookStock(d.getItemId(), d.getQuantity());
            } else {
                dao.decrementAccessoryStock(d.getItemId(), d.getQuantity());
            }
        }
        return orderId;
    }
}
