package com.csc301.students.BookBarter.SearchAds;



import java.io.Serializable;
//Dazhi Chen: Data class and their attribute for each post used in Search function
public class Data implements Serializable {
    private String image;
    private String email;
    private String description;
    private String title;
    private String id;
    private String price;

    public Data() {
    }

    public Data(String image, String email, String description, String title, String id, String price) {
        this.image = image;
        this.email = email;
        this.description = description;
        this.title = title;
        this.id = id;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}