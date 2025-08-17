package com.pahanaedu.model;

public class CartItem {
    private String itemType;
    private int itemId;
    private String itemName;
    private double price;
    private int quantity;
    private String imageUrl;
    
    // Default constructor
    public CartItem() {}
    
    // Constructor with parameters
    public CartItem(String itemType, int itemId, String itemName, double price, int quantity, String imageUrl) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }
    
    // Getters and setters
    public String getItemType() { 
        return itemType; 
    }
    
    public void setItemType(String itemType) { 
        this.itemType = itemType; 
    }
    
    public int getItemId() { 
        return itemId; 
    }
    
    public void setItemId(int itemId) { 
        this.itemId = itemId; 
    }
    
    public String getItemName() { 
        return itemName; 
    }
    
    public void setItemName(String itemName) { 
        this.itemName = itemName; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public void setPrice(double price) { 
        this.price = price; 
    }
    
    public int getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
    }
    
    public String getImageUrl() { 
        return imageUrl; 
    }
    
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
    }
    
    public double getSubtotal() {
        return price * quantity;
    }
    
    @Override
    public String toString() {
        return "CartItem{" +
                "itemType='" + itemType + '\'' +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", imageUrl='" + imageUrl + '\'' +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}