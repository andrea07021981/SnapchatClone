package com.projects.andreafranco.snapchatclone.models;

import android.graphics.Bitmap;

public class Snap {

    private String mImageName;
    private Bitmap mImage;
    private String mEmail;
    private String mMessage;
    private String mKey;

    public Snap(Bitmap image, String email, String message, String key, String imageName) {
        this.mImage = image;
        this.mEmail= email;
        this.mMessage = message;
        this.mKey = key;
        this.mImageName = imageName;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap mImage) {
        this.mImage = mImage;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey= mKey;
    }

    public String getImageName() {
        return mImageName;
    }

    public void setImageName(String mImageName) {
        this.mImageName= mImageName;
    }
}
