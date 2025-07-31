package com.pahanaedu.model;

public class Accessory {
    private int accessoryId;
    private String name;
    private String category;
    private String description;
    private String supplier;
    private String imageUrl;
    private int minStock;

    // Default constructor
    public Accessory() {}

    // Constructor with parameters
    public Accessory(int accessoryId, String name, String category, String description, 
                    String supplier, String imageUrl, int minStock) {
        this.accessoryId = accessoryId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.supplier = supplier;
        this.imageUrl = imageUrl;
        this.minStock = minStock;
    }

    // Getters and Setters
    public int getAccessoryId() {
        return accessoryId;
    }

    public void setAccessoryId(int accessoryId) {
        this.accessoryId = accessoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getMinStock() {
        return minStock;
    }

    public void setMinStock(int minStock) {
        this.minStock = minStock;
    }

    @Override
    public String toString() {
        return "Accessory{" +
                "accessoryId=" + accessoryId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", supplier='" + supplier + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", minStock=" + minStock +
                '}';
    }
}
