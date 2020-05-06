package com.SMAPAppProjectGroup13.sharemovies;

public class User {

    private String email;
    private String groupID;
    private String userID;

    public User(String userID, String groupID)
    {
        this.userID = userID;
        this.groupID = groupID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }







}
