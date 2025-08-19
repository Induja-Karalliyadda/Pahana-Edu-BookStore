package com.pahanaedu.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OnlineOrder {
  private long id;
  private String customerName;
  private String phone;
  private String email;
  private String address;
  private BigDecimal totalAmount;
  private String status;
  private Timestamp createdAt;

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public String getCustomerName() { return customerName; }
  public void setCustomerName(String customerName) { this.customerName = customerName; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
  public BigDecimal getTotalAmount() { return totalAmount; }
  public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Timestamp getCreatedAt() { return createdAt; }
  public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
