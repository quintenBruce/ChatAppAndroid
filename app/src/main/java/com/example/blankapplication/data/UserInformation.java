package com.example.blankapplication.data;

public class UserInformation {
    private static UserInformation instance;
    private String userId;

    private UserInformation() {}

    public static synchronized UserInformation getInstance() {
        if (instance == null) {
            instance = new UserInformation();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String value) {
        this.userId = value;
    }
}
