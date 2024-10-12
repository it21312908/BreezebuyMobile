package com.example.myapplication.Component02;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private String categoryId;
    private String categoryName; // New field
    private int quantity; // New quantity field

    private boolean selected;

    public Product(String id, String name, String description, double price, String categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName; // Initialize new field
        this.selected = false;
        this.quantity = 1;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() { // Getter for category name
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}