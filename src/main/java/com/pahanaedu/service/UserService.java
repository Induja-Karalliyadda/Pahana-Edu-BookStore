package com.pahanaedu.service;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.User;

import java.util.List;

public class UserService {
    private static final UserDAO dao = new UserDAO();

    public static List<User>   getAllUsers()    { return dao.findAllUsers(); }
    public static List<User>   searchUsers(String kw) { return dao.searchUsers(kw); }
    public static boolean      updateUser(User u)    { return dao.updateUser(u); }
    public static boolean      deleteUser(int id)    { return dao.deleteUser(id); }

    // your existing sign‑up / login methods:
    public static boolean      registerUser(User u)  { return dao.registerUser(u); }
    public static User         loginUser(String idOrEmail, String pwd)
                                                     { return dao.getUserByEmailAndPassword(idOrEmail,pwd); }
    
}
