package com.navigatorsguide.app.model;

public class User {
    public String userId;
    public String userName;
    public String email;
    public String password;
    public int position;
    public int shipType;
    public String createdAt;
    public String token;

    public User() {
    }

    public User(String userId, String userName, String email, String password, int position, int shipType, String token, String createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.position = position;
        this.shipType = shipType;
        this.createdAt = createdAt;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getShipType() {
        return shipType;
    }

    public void setShipType(int shipType) {
        this.shipType = shipType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
