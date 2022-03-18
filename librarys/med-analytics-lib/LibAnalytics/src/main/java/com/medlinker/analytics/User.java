package com.medlinker.analytics;


public class User {
    private String userID="android123";
    private String token="androidtoken123456";
    private String phoneNO;
    private String idCard;

    public User() {
    }

    public User(String userID) {
        this.userID = userID;
    }

    public User(String userID,String token) {
        this.userID = userID;
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhoneNO() {
        return phoneNO;
    }

    public void setPhoneNO(String phoneNO) {
        this.phoneNO = phoneNO;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}
