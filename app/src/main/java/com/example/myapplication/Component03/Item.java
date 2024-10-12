package com.example.myapplication.Component03;

public class Item {
    private String productId;
    private int quantity;
    private double price;
    private double totalAmount;
    private String productName; // New field for product name

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getProductName() { // Getter for product name
        return productName;
    }

    public void setProductName(String productName) { // Setter for product name
        this.productName = productName;
    }
}