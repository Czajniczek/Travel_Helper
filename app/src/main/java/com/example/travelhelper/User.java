package com.example.travelhelper;

import android.net.Uri;

public class User {

    private String userName, phoneNumber, city, Id, email;
    private Uri ProfileImage;

    public User(String Id) {
        this.Id = Id;
    }

    public Uri getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(Uri profileImage) {
        ProfileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String eMail) {
        this.email = eMail;
    }
}
