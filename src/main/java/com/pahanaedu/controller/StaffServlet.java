package com.pahanaedu.controller;

import com.pahanaedu.model.Staff;
import com.pahanaedu.service.StaffService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/staff")
public class StaffServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Staff> staff = StaffService.getAll();
        req.setAttribute("staffList", staff);
        req.getRequestDispatcher("/view/Staff.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        System.out.println("StaffServlet action = " + action);

        if ("add".equals(action)) {
            Staff s = new Staff();
            s.setUsername(req.getParameter("username"));
            s.setAddress(req.getParameter("address"));
            s.setTelephone(req.getParameter("telephone"));
            s.setEmail(req.getParameter("email"));
            s.setPassword(req.getParameter("password"));

            boolean ok = StaffService.add(s);
            System.out.println("Insert result = " + ok);

        } else if ("update".equals(action)) {
            Staff s = new Staff();
            s.setId(Integer.parseInt(req.getParameter("id")));
            s.setUsername(req.getParameter("username"));
            s.setAddress(req.getParameter("address"));
            s.setTelephone(req.getParameter("telephone"));
            s.setEmail(req.getParameter("email"));

            StaffService.update(s);

        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            StaffService.delete(id);
        }

        resp.sendRedirect(req.getContextPath() + "/staff");
    }
}

