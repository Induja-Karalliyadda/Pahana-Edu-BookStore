package com.pahanaedu.model;

import java.math.BigDecimal;

public class OnlineOrderItem {
  private long id;
  private long orderId;
  private String itemType;
  private long itemId;
  private String itemName;
  private long quantity;
  private BigDecimal unitPrice;

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public long getOrderId() { return orderId; }
  public void setOrderId(long orderId) { this.orderId = orderId; }
  public String getItemType() { return itemType; }
  public void setItemType(String itemType) { this.itemType = itemType; }
  public long getItemId() { return itemId; }
  public void setItemId(long itemId) { this.itemId = itemId; }
  public String getItemName() { return itemName; }
  public void setItemName(String itemName) { this.itemName = itemName; }
  public long getQuantity() { return quantity; }
  public void setQuantity(long quantity) { this.quantity = quantity; }
  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
