package com.pahanaedu.model;

public class User {
    private int    id;
    private String username;
    private String address;
    private String telephone;
    private String email;
    private String password;    // stored as SHA-256 hex
    private String role;
    private String customerCode;

    // NEW: only for passing back to controller (not persisted)
    private transient String tempPassword;

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

    // getters & setters …
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String u){ this.username = u; }

    public String getAddress() { return address; }
    public void setAddress(String a){ this.address = a; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String t){ this.telephone = t; }

    public String getEmail() { return email; }
    public void setEmail(String e){ this.email = e; }

    public String getPassword() { return password; }
    public void setPassword(String p){ this.password = p; }

    public String getRole() { return role; }
    public void setRole(String r) { this.role = r; }

    public String getCustomerCode(){ return customerCode; }
    public void setCustomerCode(String c){ this.customerCode = c; }

    // NEW
    public String getTempPassword() { return tempPassword; }
    public void setTempPassword(String tempPassword) { this.tempPassword = tempPassword; }
}


