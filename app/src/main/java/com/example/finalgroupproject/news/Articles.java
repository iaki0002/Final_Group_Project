package com.example.finalgroupproject.news;

/*
Author: David Lee
Student Number: 040959646
Course: CST2335
Date: Dec 02, 2019
 */


/**
 * This class defines the object of a news article
 */
public class Articles {

    long id;
    String title;
    String description;
    String url;
    String image;

    /**
     * default constructor
     */
    public Articles(){
        super();
    }

    /**
     * @param id the primary key of the database
     * @param title the title of the news
     * @param link the web link to the news
     */
    public Articles(long id, String title, String link){
        this.id = id;
        this.title = title;
        this.url = url;
    }

    /**
     * @return gets the primary key of the database
     */
    public long getId(){return id;}


    /**
     * @param id the primary key of the database
     */
    public void setId(long id){this.id = id;}

    /**
     * @return the title of the news
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title of the news
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description of the news
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description of the news
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the web link to the news
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the web link to the news
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the image link of the news
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image  the image link of the news
     */
    public void setImage(String image) {
        this.image = image;
    }
}
