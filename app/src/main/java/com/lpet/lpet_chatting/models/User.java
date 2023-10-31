package com.lpet.lpet_chatting.models;

import com.google.firebase.Timestamp;

public class User {
    private String phone;
    private String username;
    private Timestamp createdAt;
    private String userId;

    public User() {
    }

    public User(String phone, String username, Timestamp createdAt, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}