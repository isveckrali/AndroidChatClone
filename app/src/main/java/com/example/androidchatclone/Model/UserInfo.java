package com.example.androidchatclone.Model;

public class UserInfo {

    String mail;
    String name;
    String photoUrl;
    String uid;

    public UserInfo(String mail, String name, String photoUrl, String uid) {
        this.mail = mail;
        this.name = name;
        this.photoUrl = photoUrl;
        this.uid = uid;
    }

    public UserInfo() {

    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
