package com.pahanaedu.model;

public class User {
    private int id;
    private String username;
    private String address;
    private String telephone;
    private String email;
    private String password;  // Stored as SHA-256 hash or BCrypt hash
    private String role;
    private String customerCode;

    // NEW: For passing back to controller (not persisted)
    private transient String tempPassword;

    // Constructors
    public User() {}

    public User(String username, String address,
                String telephone, String email,
                String password, String role) {
        this.username  = username;
        this.address   = address;
        this.telephone = telephone;
        this.email     = email;
        this.password  = password;
        this.role      = role;
    }

    // Getters and Setters for all properties
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    // NEW: For temporary password (not persisted in DB)
    public String getTempPassword() { return tempPassword; }
    public void setTempPassword(String tempPassword) { this.tempPassword = tempPassword; }
}


