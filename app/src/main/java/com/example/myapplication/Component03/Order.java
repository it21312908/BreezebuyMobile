package com.example.myapplication.Component03;

public class Order {
    private String orderId;
    private String status;
    private String date;

    public Order(String orderId, String status, String date) {
        this.orderId = orderId;
        this.status = status;
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}