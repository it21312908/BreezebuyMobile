package com.example.myapplication.Component04;

import java.util.ArrayList;
import java.util.List;

public class Vendor {
    private String id; // Corresponds to "_id" in JSON
    private String name; // Corresponds to "name" in JSON
    private String product; // Corresponds to "product" in JSON
    private String description; // Corresponds to "description" in JSON
    private double averageRating; // Average rating instead of rank
    private List<Comment> comments; // List to hold comments

    public Vendor(String id, String name, String product, String description, double averageRating) {
        this.id = id;
        this.name = name; // Initialize name
        this.product = product; // Initialize product
        this.description = description; // Initialize description
        this.averageRating = averageRating; // Initialize averageRating
        this.comments = new ArrayList<>(); // Initialize comments list
    }

    // Getters and setters for each field
    public String getId() {
        return id;
    }

    public String getName() {
        return name; // Getter for name
    }

    public String getProduct() {
        return product; // Getter for product
    }

    public String getDescription() {
        return description; // Getter for description
    }

    public double getAverageRating() {
        return averageRating; // Updated to return averageRating
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating; // Setter for averageRating
    }

    public List<Comment> getComments() {
        return comments; // Getter for comments
    }

    public void addComment(Comment comment) {
        this.comments.add(comment); // Method to add a comment
    }
}

// Separate class for Comment
class Comment {
    private String userId; // Corresponds to "userId" in JSON
    private String comment; // Corresponds to "comment" in JSON
    private int rating; // Corresponds to "rating" in JSON
    private String date; // Corresponds to "date" in JSON

    public Comment(String userId, String comment, int rating, String date) {
        this.userId = userId; // Initialize userId
        this.comment = comment; // Initialize comment
        this.rating = rating; // Initialize rating
        this.date = date; // Initialize date
    }

    // Getters for Comment fields
    public String getUserId() {
        return userId; // Getter for userId
    }

    public String getComment() {
        return comment; // Getter for comment
    }

    public int getRating() {
        return rating; // Getter for rating
    }

    public String getDate() {
        return date; // Getter for date
    }
}