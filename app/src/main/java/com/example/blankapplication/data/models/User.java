package com.example.blankapplication.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.bson.types.ObjectId;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String userName;

    public User() {}

    public User(@NonNull String id, String name, String username) {
        this.id = id;
        this.name = name;
        this.userName = username;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}

