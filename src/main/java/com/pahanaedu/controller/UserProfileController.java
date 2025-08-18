package com.pahanaedu.controller;

import com.pahanaedu.model.User;
import com.pahanaedu.service.UserService;
import com.pahanaedu.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/UserProfileController")
public class UserProfileController extends HttpServlet {

    private final UserDAO dao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        User sessionUser = (User) session.getAttribute("user");
        try {
            // refresh from DB
            User fresh = dao.findById(sessionUser.getId());
            if (fresh != null) {
                session.setAttribute("user", fresh);
                req.setAttribute("user", fresh);
            }
        } catch (Exception ignore) {}
        req.getRequestDispatcher("/view/UserProfile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User current = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        try {
            if ("updateProfile".equals(action)) {
                User u = new User();
                u.setId(current.getId());
                u.setUsername(req.getParameter("username"));
                u.setAddress(req.getParameter("address"));
                u.setTelephone(req.getParameter("telephone"));
                u.setEmail(req.getParameter("email"));

                boolean ok = UserService.updateUser(u);
                if (ok) {
                    // reload and update session
                    User fresh = dao.findById(current.getId());
                    session.setAttribute("user", fresh);
                    req.setAttribute("msg", "Profile updated successfully.");
                } else {
                    req.setAttribute("err", "Could not update profile. Please try again.");
                }

            } else if ("changePassword".equals(action)) {
                String cur = req.getParameter("currentPassword");
                String npw = req.getParameter("newPassword");
                String cpw = req.getParameter("confirmPassword");

                if (npw == null || !npw.equals(cpw)) {
                    req.setAttribute("err", "New passwords do not match.");
                } else if (npw.length() < 6) {
                    req.setAttribute("err", "Password must be at least 6 characters.");
                } else {
                    boolean ok = UserService.changePassword(current.getId(), cur, npw);
                    if (ok) req.setAttribute("msg", "Password updated.");
                    else    req.setAttribute("err", "Current password is incorrect.");
                }
            }
        } catch (Exception e) {
            req.setAttribute("err", "Unexpected error: " + e.getMessage());
        }

        // show page again with message
        doGet(req, resp);
    }
}
