package com.pahanaedu.controller;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.User;
import com.pahanaedu.util.EmailTemplate;
import com.pahanaedu.util.Mailer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/AddCustomerController")
public class AddCustomerController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            JsonObject reqJson = gson.fromJson(request.getReader(), JsonObject.class);

            String username  = reqJson.has("username")  ? reqJson.get("username").getAsString().trim()  : "";
            String address   = reqJson.has("address")   ? reqJson.get("address").getAsString().trim()   : "";
            String telephone = reqJson.has("telephone") ? reqJson.get("telephone").getAsString().trim() : "";
            String email     = reqJson.has("email")     ? reqJson.get("email").getAsString().trim()     : "";

            if (username.isEmpty()) {
                out.print("{\"error\":\"Customer name is required\"}");
                return;
            }

            User payload = new User();
            payload.setUsername(username);
            payload.setAddress(address);
            payload.setTelephone(telephone);
            payload.setEmail(email);
            payload.setRole("user");

            UserDAO dao = new UserDAO();
            User created = dao.createCustomer(payload); // returns id + customer_code + tempPassword

            if (created != null && created.getId() > 0) {
                boolean emailQueued = false;

                if (email != null && !email.isBlank()) {
                    String html = EmailTemplate.welcome(
                            created.getUsername(),
                            created.getCustomerCode(),
                            created.getTempPassword()
                    );
                    Mailer.sendHtmlAsync(email, "Welcome to Pahana Edu Bookshop", html);
                    emailQueued = true;
                }

                JsonObject res = new JsonObject();
                res.addProperty("id", created.getId());
                res.addProperty("username", created.getUsername());
                res.addProperty("customerCode", created.getCustomerCode());
                res.addProperty("tempPassword", created.getTempPassword());
                res.addProperty("address", created.getAddress());
                res.addProperty("telephone", created.getTelephone());
                res.addProperty("email", created.getEmail());
                res.addProperty("emailQueued", emailQueued);
                out.print(res.toString());
            } else {
                out.print("{\"error\":\"Failed to add customer\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("{\"error\":\"Server error\"}");
        }
    }
}