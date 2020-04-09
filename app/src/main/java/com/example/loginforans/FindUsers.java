package com.example.loginforans;

public class FindUsers {

    private String Name,Semester,profileurl;



    public FindUsers(){


    }


    public FindUsers(String Name, String Semester, String Profileurl) {
        Name = Name;
        Semester = Semester;
        profileurl = Profileurl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        Name = Name;
    }

    public String getSemester() {
        return Semester;
    }

    public void setSemester(String semester) {
        Semester = Semester;
    }

    public String getprofileurl() {
        return profileurl;
    }

    public void setprofileurl(String Profileurl) {
        profileurl = Profileurl;
    }






}
