package com.example.travelhelper;

import android.net.Uri;

public class User {

    //region VARIABLES
    private String userName, phoneNumber, city, Id, email, accCreation;
    private Uri ProfileImage;
    //endregion

    public User(String Id) {
        this.Id = Id;
    }

    public String getAccCreation() {
        return accCreation;
    }

    public void setAccCreation(String accCreation) {
        this.accCreation = accCreation;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String eMail) {
        this.email = eMail;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
