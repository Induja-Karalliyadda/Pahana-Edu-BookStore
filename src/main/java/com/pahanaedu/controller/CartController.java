package com.pahanaedu.controller;

import com.pahanaedu.model.CartItem;
import com.pahanaedu.model.User;
import com.pahanaedu.model.Book;
import com.pahanaedu.model.Accessory;
import com.pahanaedu.service.ItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/cart")
public class CartController extends HttpServlet {
    private ItemService itemService;

    @Override public void init() { itemService = new ItemService(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        User u = (s != null) ? (User) s.getAttribute("user") : null;
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        // flash support
        Object flash = (s != null) ? s.getAttribute("flash") : null;
        if (flash != null) { req.setAttribute("message", flash.toString()); s.removeAttribute("flash"); }

        req.getRequestDispatcher("/view/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("user") == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        String action   = req.getParameter("action");
        String itemType = req.getParameter("itemType");
        int itemId      = Integer.parseInt(req.getParameter("itemId"));
        int quantity    = Integer.parseInt(req.getParameter("quantity"));

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) s.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        if ("updateQty".equals(action)) {
            // clamp by current stock
            int max = 99;
            try {
                if ("book".equals(itemType)) {
                    Book b = itemService.getBookById(itemId);
                    if (b != null) max = Math.max(0, b.getStock());
                } else if ("accessory".equals(itemType)) {
                    Accessory a = itemService.getAccessoryById(itemId);
                    if (a != null) max = Math.max(0, a.getStock());
                }
            } catch (Exception ignore){}

            for (Iterator<CartItem> it = cart.iterator(); it.hasNext();) {
                CartItem ci = it.next();
                if (ci.getItemType().equals(itemType) && ci.getItemId() == itemId) {
                    if (quantity <= 0) it.remove();
                    else ci.setQuantity(Math.min(quantity, max));
                    break;
                }
            }
            s.setAttribute("flash", "Cart updated.");
        } else if ("remove".equals(action)) {
            cart.removeIf(ci -> ci.getItemType().equals(itemType) && ci.getItemId() == itemId);
            s.setAttribute("flash", "Item removed from cart.");
        }

        s.setAttribute("cart", cart);
        s.setAttribute("cartItemCount", cart.stream().mapToInt(CartItem::getQuantity).sum());
        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}
