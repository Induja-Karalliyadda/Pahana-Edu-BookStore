package com.pahanaedu.controller;

import com.pahanaedu.service.ItemService;
import com.pahanaedu.model.Book;
import com.pahanaedu.model.Accessory;
import com.pahanaedu.model.User;
import com.pahanaedu.model.CartItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/UserDashboardController")
public class UserDashboardController extends HttpServlet {
    private ItemService itemService;

    @Override
    public void init() throws ServletException {
        itemService = new ItemService();
        System.out.println("✅ UserDashboardController initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n📍 UserDashboardController doGet() called");

        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedInUser == null) {
            System.out.println("❌ No user in session, redirecting to login");
            response.sendRedirect("login.jsp");
            return;
        }
        System.out.println("✅ User logged in: " + loggedInUser.getUsername());

        try {
            // Read filters from the JSP form (empty string = no filter)
            String bookSearch        = nv(request.getParameter("bookSearch"));
            String bookCategory      = nv(request.getParameter("bookCategory"));
            String accessorySearch   = nv(request.getParameter("accessorySearch"));
            String accessoryCategory = nv(request.getParameter("accessoryCategory"));

            // Load data via service (handles null/empty)
            List<Book> books = itemService.getBooks(bookSearch, bookCategory);
            List<Accessory> accessories = itemService.getAccessories(accessorySearch, accessoryCategory);

            // Provide data to JSP
            request.setAttribute("books", books);
            request.setAttribute("accessories", accessories);
            request.setAttribute("bookCategories", getBookCategories());
            request.setAttribute("accessoryCategories", getAccessoryCategories());

            // Only needed if your JSP reads request-scoped "user" (yours also reads from session—both are fine)
            request.setAttribute("user", loggedInUser);

            System.out.println("\n📍 Setting request attributes:");
            System.out.println("   books: " + books.size() + " items");
            System.out.println("   accessories: " + accessories.size() + " items");
            System.out.println("   user: " + loggedInUser.getUsername());

            // 🔧 Use a context-absolute path. If your JSP is at /UserDashboard.jsp, change to "/UserDashboard.jsp"
            request.getRequestDispatcher("/view/UserDashboard.jsp").forward(request, response);
            System.out.println("✅ Successfully forwarded to JSP");

        } catch (Exception e) {
            System.err.println("❌ ERROR in UserDashboardController doGet(): " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("books", new ArrayList<Book>());
            request.setAttribute("accessories", new ArrayList<Accessory>());
            request.setAttribute("user", loggedInUser);
            request.setAttribute("error", "Error loading items: " + e.getMessage());

            request.getRequestDispatcher("/view/UserDashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n📍 UserDashboardController doPost() called");

        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        System.out.println("🎯 Action: " + action);

        if ("addToCart".equals(action)) {
            handleAddToCart(request, session);
        }

        // Reload the dashboard so the lists + cart badge refresh
        doGet(request, response);
    }

    private void handleAddToCart(HttpServletRequest request, HttpSession session) {
        System.out.println("\n📍 handleAddToCart() called");
        try {
            String itemType = request.getParameter("itemType");
            int itemId      = Integer.parseInt(request.getParameter("itemId"));
            int quantity    = Integer.parseInt(request.getParameter("quantity"));

            System.out.println("🛒 Adding: " + itemType + " ID:" + itemId + " Qty:" + quantity);

            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
                System.out.println("🆕 Created new cart");
            }

            if ("book".equals(itemType)) {
                Book book = itemService.getBookById(itemId);
                if (book != null && book.getStock() >= quantity) {
                    boolean found = false;
                    for (CartItem item : cart) {
                        if ("book".equals(item.getItemType()) && item.getItemId() == itemId) {
                            item.setQuantity(item.getQuantity() + quantity);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        cart.add(new CartItem("book", book.getBookId(), book.getTitle(),
                                book.getSellingPrice(), quantity, book.getImageUrl()));
                    }
                    updateCartCount(session, cart);
                    request.setAttribute("message", book.getTitle() + " added to cart successfully!");
                    System.out.println("✅ Added " + book.getTitle() + " to cart");
                } else {
                    request.setAttribute("error", "Book not available or insufficient stock!");
                }

            } else if ("accessory".equals(itemType)) {
                Accessory a = itemService.getAccessoryById(itemId);
                if (a != null && a.getStock() >= quantity) {
                    boolean found = false;
                    for (CartItem item : cart) {
                        if ("accessory".equals(item.getItemType()) && item.getItemId() == itemId) {
                            item.setQuantity(item.getQuantity() + quantity);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        cart.add(new CartItem("accessory", a.getAccessoryId(), a.getName(),
                                a.getSellingPrice(), quantity, a.getImageUrl()));
                    }
                    updateCartCount(session, cart);
                    request.setAttribute("message", a.getName() + " added to cart successfully!");
                    System.out.println("✅ Added " + a.getName() + " to cart");
                } else {
                    request.setAttribute("error", "Accessory not available or insufficient stock!");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR in handleAddToCart: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error adding item to cart: " + e.getMessage());
        }
    }

    private void updateCartCount(HttpSession session, List<CartItem> cart) {
        int totalItems = cart.stream().mapToInt(CartItem::getQuantity).sum();
        session.setAttribute("cartItemCount", totalItems);
    }

    // Helpers
    private static String nv(String s) { return (s == null) ? "" : s.trim(); }

    private List<String> getBookCategories() {
        return Arrays.asList(
                "Fiction",
                "Non-Fiction",
                "Science",
                "History",
                "Comics & Graphic Novels",
                "Education",
                "Technology",
                "Literature"
        );
    }

    private List<String> getAccessoryCategories() {
        return Arrays.asList(
                "Writing Instruments",
                "Office Supplies",
                "Art Supplies",
                "Electronics",
                "Stationery"
        );
    }
}
