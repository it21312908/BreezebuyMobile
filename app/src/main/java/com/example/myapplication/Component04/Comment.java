package com.example.myapplication.Component04;

public class Comment {
    private String id;
    private String customerId;
    private int rank;
    private String commentText;
    private boolean isCommentEditable;

    public Comment(String id, String customerId, int rank, String commentText, boolean isCommentEditable) {
        this.id = id;
        this.customerId = customerId;
        this.rank = rank; 
        this.commentText = commentText;
        this.isCommentEditable = isCommentEditable;
    }

    public String getId() {
        return id; 
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getRank() {
        return rank;
    }

    public String getCommentText() {
        return commentText;
    }

    public boolean isCommentEditable() {
        return isCommentEditable;
    }
}