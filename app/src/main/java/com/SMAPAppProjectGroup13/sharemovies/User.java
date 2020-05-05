package com.SMAPAppProjectGroup13.sharemovies;

public class User {

    private String email;
    private String groupID;

    public User(String email, String groupID)
    {
        this.email = email;
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







}
