/*
 * User.java
 * Author: [Dayananda I.H.M.B.L. | IT21307058]
 * This Java class represents a User object in an Android application.
 
 */


package com.example.myapplication.Component01;

public class User {
    private String username;
    private String password;
    private String status;
    private String role;

    public User(String username, String password, String status, String role) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}