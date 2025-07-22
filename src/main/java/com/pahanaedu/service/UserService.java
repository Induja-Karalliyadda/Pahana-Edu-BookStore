package com.pahanaedu.service;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.User;

public class UserService {
    private static UserDAO userDAO = new UserDAO();

    public static boolean registerUser(User user) {
        if (userDAO.userExists(user.getEmail())) {
            return false; // User already exists
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) return false;
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) return false;
        if (user.getPassword() == null || user.getPassword().length() < 6) return false;

        return userDAO.registerUser(user);
    }

    // This now logs in by email
    public static User loginUser(String email, String password) {
        return userDAO.getUserByEmailAndPassword(email, password);
    }
}
