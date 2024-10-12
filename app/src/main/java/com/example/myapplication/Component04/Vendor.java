package com.example.myapplication.Component04;

import java.util.List;

public class Vendor {
    private String id;
    private String name;
    private String product;
    private String description;
    private double averageRating;
    private List<Comment> comments;

    public Vendor(String id, String name, String product, String description, double averageRating, List<Comment> comments) {
        this.id = id;
        this.name = name;
        this.product = product;
        this.description = description;
        this.averageRating = averageRating;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProduct() {
        return product; 
    } 

    public String getDescription() {
        return description;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public List<Comment> getComments() {
        return comments;
    }
}