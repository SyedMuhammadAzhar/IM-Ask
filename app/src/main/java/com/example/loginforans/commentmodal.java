package com.example.loginforans;

public class commentmodal {


    String C_comment,C_date,C_dpurl,C_time,C_uid,C_username;


    public commentmodal()
    {

    }


    public commentmodal(String c_comment, String c_date, String c_dpurl, String c_time, String c_uid, String c_username) {
        C_comment = c_comment;
        C_date = c_date;
        C_dpurl = c_dpurl;
        C_time = c_time;
        C_uid = c_uid;
        C_username = c_username;
    }

    public String getC_comment() {
        return C_comment;
    }

    public void setC_comment(String c_comment) {
        C_comment = c_comment;
    }

    public String getC_date() {
        return C_date;
    }

    public void setC_date(String c_date) {
        C_date = c_date;
    }

    public String getC_dpurl() {
        return C_dpurl;
    }

    public void setC_dpurl(String c_dpurl) {
        C_dpurl = c_dpurl;
    }

    public String getC_time() {
        return C_time;
    }

    public void setC_time(String c_time) {
        C_time = c_time;
    }

    public String getC_uid() {
        return C_uid;
    }

    public void setC_uid(String c_uid) {
        C_uid = c_uid;
    }

    public String getC_username() {
        return C_username;
    }

    public void setC_username(String c_username) {
        C_username = c_username;
    }
}
