package com.csc301.students.BookBarter;


import android.app.Application;

import com.google.firebase.FirebaseApp;

public class Token extends Application {
    private String token;

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}