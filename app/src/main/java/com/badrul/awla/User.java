package com.badrul.awla;

public class User {

    private String userID;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userAge;
    private String userWorkExp;
    private String userToken;


    public User(String userID, String userName, String userEmail, String userPhone, String userAge, String userWorkExp, String userToken) {

        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userEmail = userPhone;
        this.userAge = userAge;
        this.userWorkExp = userWorkExp;
        this.userToken = userToken;


    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserAge() {
        return userAge;
    }

    public String getUserWorkExp() {
        return userWorkExp;
    }
    public String getUserToken() {
        return userToken;
    }

}
