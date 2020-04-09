package com.example.loginforans;

public class Posts {


    String Description,date,dpurl,name,postpicurl,time,uid;

    public Posts()
    {

    }

    public Posts(String description, String date, String dpurl, String name, String postpicurl, String time, String uid) {
        Description = description;
        this.date = date;
        this.dpurl = dpurl;
        this.name = name;
        this.postpicurl = postpicurl;
        this.time = time;
        this.uid = uid;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDpurl() {
        return dpurl;
    }

    public void setDpurl(String dpurl) {
        this.dpurl = dpurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostpicurl() {
        return postpicurl;
    }

    public void setPostpicurl(String postpicurl) {
        this.postpicurl = postpicurl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
