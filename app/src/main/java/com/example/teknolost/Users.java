package com.example.teknolost;



public class Users {
    public String id;
    public String fullname;
    public String email;
    public String password;
    public String confirmpassword;

    public Users(String id, String fullname, String email, String password, String confirmpassword) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.confirmpassword = confirmpassword;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }
}
