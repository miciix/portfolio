package com.csc301.students.BookBarter;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonResult {
    private String token;
    private String success;
    private String username;
    private String campus;
    private String yearOfStudy;
    private String email;
    private String[] Posts;
    private JsonArray PostList;
    private JsonArray interested;

    private String[] Titles;
    private String[] Ids;
    private String[] AdFields;
    @SerializedName("TextbookList")
    @Expose
    private List<TextbookList> textbookList = null;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTitles(String[] titles) { this.Titles = titles; }

    public String[] getPosts() { return Posts;}

    public JsonArray getAllPosts(){return PostList;}

    public String[] getTitles() { return Titles;}

    public void setIds(String[] ids) { this.Ids = ids; }

    public String[] getIds() {return Ids;}

    public void setAdFields(String[] adfields) {this.AdFields = adfields;}

    public String[] getAdFields() {return AdFields;}

    public List<TextbookList> getTextbookList() {
        return textbookList;
    }

    public void setTextbookList(List<TextbookList> textbookList) {
        this.textbookList = textbookList;
    }

    public JsonArray getInterested() {
        return interested;
    }

    public void setInterested(JsonArray interested) {
        this.interested = interested;
    }
}
