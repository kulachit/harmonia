package com.example.harmonia;

import java.util.jar.Attributes;

// builder?
public class Account {
    private String name;
    private String email;
    private String type; // user, instructor, admin
    private String accountID;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }
}
