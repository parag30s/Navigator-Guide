package com.navigatorsguide.app.model;

public class User {
    public String userId;
    public String userName;
    public String email;
    public String password;
    public String position;
    public String shipType;
    public String createdAt;

    public User() {
    }

    public User(String userId, String userName, String email, String password, String position, String shipType, String createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.position = position;
        this.shipType = shipType;
        this.createdAt = createdAt;
    }

    public User(String userId, String userName, String email, String position, String shipType, String createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.position = position;
        this.shipType = shipType;
        this.createdAt = createdAt;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
