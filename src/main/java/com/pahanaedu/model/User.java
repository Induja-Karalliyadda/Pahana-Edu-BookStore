package com.pahanaedu.model;

public class User {
    private int userId;
    private String username;
    private String address;
    private String telephone;
    private String email;
    private String password;
    private String role;
    private String customerCode;

    public User() {}

    // Constructor for registration (no userId)
    public User(String username, String address, String telephone, String email, String password, String role, String customerCode) {
        this.username = username;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.customerCode = customerCode;
    }

    // Constructor for loading from DB (with userId)
    public User(int userId, String username, String address, String telephone, String email, String password, String role, String customerCode) {
        this.userId = userId;
        this.username = username;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.customerCode = customerCode;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
}