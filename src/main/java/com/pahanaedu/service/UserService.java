package com.pahanaedu.service;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.User;

public class UserService {
    private static UserDAO userDAO = new UserDAO();

    public static boolean registerUser(User user) {
        if (userDAO.userExists(user.getEmail())) {
            return false; // User already exists
        }
        // Add more validation as needed
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) return false;
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) return false;
        if (user.getPassword() == null || user.getPassword().length() < 6) return false;

        return userDAO.registerUser(user);
    }

    public static User loginUser(String username, String password) {
        // Returns User object if credentials match, else null
        return userDAO.getUserByEmailAndPassword(username, password);
    }
}
