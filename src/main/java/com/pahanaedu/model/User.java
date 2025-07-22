package com.pahanaedu.model;

public class User {
    private String username;
    private String address;
    private String telephone;
    private String email;
    private String password;
    private String role;

    // This is the missing constructor!
    public User(String username, String address, String telephone, String email, String password, String role) {
        this.username = username;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and setters...
    public String getUsername() { return username; }
    public String getAddress() { return address; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public void setUsername(String username) { this.username = username; }
    public void setAddress(String address) { this.address = address; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}
