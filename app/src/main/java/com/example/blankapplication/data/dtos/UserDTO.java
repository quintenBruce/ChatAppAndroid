package com.example.blankapplication.data.dtos;




public class UserDTO {
    private String id;
    private String name;
    private String userName;

//    public User() {}
//
//    public User(String id, String name, String username) {
//        this.id = id;
//        this.name = name;
//        this.userName = username;
//    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}

