package com.pahanaedu.controller;

import com.pahanaedu.model.Book;
import com.pahanaedu.model.Accessory;
import com.pahanaedu.service.ItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;

@WebServlet("/items")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,     // 1 MB
    maxFileSize = 10 * 1024 * 1024,      // 10 MB
    maxRequestSize = 50 * 1024 * 1024    // 50 MB
)
public class ItemController extends HttpServlet {
    private ItemService itemService;

    private final List<String> BOOK_CATEGORIES = Arrays.asList("Educational", "Children's Books", "Fiction", "Non-Fiction",
            "Comics & Graphic Novels", "Technology & Science", "Language Learning", "Art & Design",
            "Cookbooks & Food", "Poetry", "Law", "Sports & Outdoors");
    private final List<String> ACCESSORY_CATEGORIES = Arrays.asList("Writing Instruments", "Erasers & Correction", "Paper Products", "Files & Folders");

    @Override
    public void init() throws ServletException {
        itemService = new ItemService();
        System.out.println("ItemController initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String mainCategory = Optional.ofNullable(request.getParameter("mainCategory")).orElse("book");
        String searchName = request.getParameter("searchName");
        String searchCategory = request.getParameter("searchCategory");

        if ("book".equals(mainCategory)) {
            List<Book> books = itemService.getBooks(searchName, searchCategory);
            request.setAttribute("books", books);
            request.setAttribute("bookCategories", BOOK_CATEGORIES);
        } else {
            List<Accessory> accessories = itemService.getAccessories(searchName, searchCategory);
            request.setAttribute("accessories", accessories);
            request.setAttribute("accessoryCategories", ACCESSORY_CATEGORIES);
        }

        String action = request.getParameter("action");
        String type = request.getParameter("type");
        String idParam = request.getParameter("id");

        if ("edit".equals(action) && idParam != null && type != null) {
            try {
                int id = Integer.parseInt(idParam);
                if ("book".equals(type)) {
                    Book book = itemService.getBookById(id);
                    request.setAttribute("editBook", book);
                } else if ("accessory".equals(type)) {
                    Accessory acc = itemService.getAccessoryById(id);
                    request.setAttribute("editAccessory", acc);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if ("delete".equals(action) && idParam != null && type != null) {
            try {
                int id = Integer.parseInt(idParam);
                boolean success;
                if ("book".equals(type)) {
                    success = itemService.deleteBook(id);
                } else {
                    success = itemService.deleteAccessory(id);
                }
                if (success) {
                    response.sendRedirect("items?mainCategory=" + mainCategory + "&success=deleted");
                } else {
                    response.sendRedirect("items?mainCategory=" + mainCategory + "&error=" + URLEncoder.encode("Delete failed", "UTF-8"));
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("items?mainCategory=" + mainCategory + "&error=" + URLEncoder.encode("Delete exception", "UTF-8"));
                return;
            }
        }

        request.setAttribute("mainCategory", mainCategory);
        request.getRequestDispatcher("/view/Item.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String mainCategory = Optional.ofNullable(request.getParameter("mainCategory")).orElse("book");
        String action = Optional.ofNullable(request.getParameter("action")).orElse("create");

        String uploadDir = getServletContext().getRealPath("") + File.separator + "img" + File.separator + "items";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            if (!uploadDirFile.mkdirs()) {
                System.err.println("Could not create upload directory: " + uploadDir);
            }
        }

        boolean success = false;
        String errorMsg = null;
        if ("book".equals(mainCategory)) {
            try {
                success = handleBook(request, uploadDir, action);
            } catch (Exception e) {
                errorMsg = e.getMessage();
                e.printStackTrace();
            }
        } else {
            try {
                success = handleAccessory(request, uploadDir, action);
            } catch (Exception e) {
                errorMsg = e.getMessage();
                e.printStackTrace();
            }
        }

        String redirect = "items?mainCategory=" + mainCategory;
        if (success) {
            redirect += "&success=1";
        } else {
            String safeMsg = (errorMsg != null) ? URLEncoder.encode(errorMsg, "UTF-8") : "Failed to save";
            redirect += "&error=" + safeMsg;
        }
        response.sendRedirect(redirect);
    }

    private boolean handleBook(HttpServletRequest request, String uploadDir, String action) {
        try {
            Book book;
            if ("update".equalsIgnoreCase(action)) {
                int bookId = Integer.parseInt(request.getParameter("bookId"));
                book = itemService.getBookById(bookId);
                if (book == null) {
                    book = new Book();
                    book.setBookId(bookId);
                }
            } else {
                book = new Book();
            }

            book.setTitle(request.getParameter("title"));
            book.setAuthor(request.getParameter("author"));
            book.setCategory(request.getParameter("bookSub"));
            book.setDescription(request.getParameter("descriptionBook"));
            book.setSupplier(request.getParameter("supplierBook"));
            book.setMinStock(Integer.parseInt(Optional.ofNullable(request.getParameter("minStockBook")).orElse("0")));

            Part imagePart = request.getPart("imgBook");
            if (imagePart != null && imagePart.getSize() > 0) {
                String submitted = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
                String cleanName = submitted.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
                String fileName = System.currentTimeMillis() + "_" + cleanName;
                File file = new File(uploadDir, fileName);
                try (InputStream in = imagePart.getInputStream(); FileOutputStream out = new FileOutputStream(file)) {
                    in.transferTo(out);
                }
                book.setImageUrl("img/items/" + fileName);
            }

            if ("update".equalsIgnoreCase(action)) {
                return itemService.updateBook(book);
            } else {
                return itemService.addBook(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean handleAccessory(HttpServletRequest request, String uploadDir, String action) {
        try {
            Accessory acc;
            if ("update".equalsIgnoreCase(action)) {
                int accId = Integer.parseInt(request.getParameter("accessoryId"));
                acc = itemService.getAccessoryById(accId);
                if (acc == null) {
                    acc = new Accessory();
                    acc.setAccessoryId(accId);
                }
            } else {
                acc = new Accessory();
            }

            acc.setName(request.getParameter("itemName"));
            acc.setCategory(request.getParameter("accSub"));
            acc.setDescription(request.getParameter("descriptionAcc"));
            acc.setSupplier(request.getParameter("supplierAcc"));
            acc.setMinStock(Integer.parseInt(Optional.ofNullable(request.getParameter("minStockAcc")).orElse("0")));

            Part imagePart = request.getPart("imgAcc");
            if (imagePart != null && imagePart.getSize() > 0) {
                String submitted = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
                String cleanName = submitted.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
                String fileName = System.currentTimeMillis() + "_" + cleanName;
                File file = new File(uploadDir, fileName);
                try (InputStream in = imagePart.getInputStream(); FileOutputStream out = new FileOutputStream(file)) {
                    in.transferTo(out);
                }
                acc.setImageUrl("img/items/" + fileName);
            }

            if ("update".equalsIgnoreCase(action)) {
                return itemService.updateAccessory(acc);
            } else {
                return itemService.addAccessory(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}





