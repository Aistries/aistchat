package com.aistriesking.aistchat.Profiling;

// User.java
public class User {
    private String email;

    public User() {
        // Default constructor required for DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

