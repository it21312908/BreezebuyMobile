package com.example.myapplication.Component02;

public class OrderItem {
    private String productId;
    private double price; // Price of the item
    private int quantity; // Quantity of the item
    private double totalAmount; // Total amount for the item

    // Constructor
    public OrderItem(String productId, double price, int quantity, double totalAmount) {
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    // Getter for price
    public String getProductId() {
        return productId;
    }
    public double getPrice() {
        return price;
    }

    // Getter for quantity
    public int getQuantity() {
        return quantity;
    }

    // Getter for totalAmount
    public double getTotalAmount() {
        return totalAmount;
    }
}