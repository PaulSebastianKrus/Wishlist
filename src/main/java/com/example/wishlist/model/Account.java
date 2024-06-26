package com.example.wishlist.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID;
    private String username;
    private String password;

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
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
}
