package com.pahanaedu.service;

import com.pahanaedu.dao.StaffDAO;
import com.pahanaedu.model.Staff;
import java.util.List;

public class StaffService {
    private static final StaffDAO dao = new StaffDAO();

    public static boolean add(Staff s)    { return dao.insert(s); }
    public static List<Staff> getAll()    { return dao.findAll(); }
    public static boolean delete(int id)  { return dao.delete(id); }
    public static boolean update(Staff s) { return dao.update(s); }
}
