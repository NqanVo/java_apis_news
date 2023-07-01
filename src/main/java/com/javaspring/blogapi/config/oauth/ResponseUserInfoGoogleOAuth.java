package com.javaspring.blogapi.config.oauth;

public class ResponseUserInfoGoogleOAuth {
    String id;
    String email;
    boolean verified_email;
    String name;
    String picture;
    String locale;

    public ResponseUserInfoGoogleOAuth() {
    }

    public ResponseUserInfoGoogleOAuth(String id, String email, boolean verified_email, String name, String picture, String locale) {
        this.id = id;
        this.email = email;
        this.verified_email = verified_email;
        this.name = name;
        this.picture = picture;
        this.locale = locale;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified_email() {
        return verified_email;
    }

    public void setVerified_email(boolean verified_email) {
        this.verified_email = verified_email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
