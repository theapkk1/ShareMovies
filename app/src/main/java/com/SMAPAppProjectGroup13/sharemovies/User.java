package com.SMAPAppProjectGroup13.sharemovies;

public class User {

    private String userID;
    private String groupID;

    public User(String userID, String groupID)
    {
        this.userID = userID;
        this.groupID = groupID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }







}
